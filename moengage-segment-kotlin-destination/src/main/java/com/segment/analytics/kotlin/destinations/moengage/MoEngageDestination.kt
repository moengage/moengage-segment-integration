package com.segment.analytics.kotlin.destinations.moengage

import android.app.Application
import com.moengage.core.LogLevel
import com.moengage.core.internal.USER_ATTRIBUTE_UNIQUE_ID
import com.moengage.core.internal.USER_ATTRIBUTE_USER_BDAY
import com.moengage.core.internal.USER_ATTRIBUTE_USER_EMAIL
import com.moengage.core.internal.USER_ATTRIBUTE_USER_FIRST_NAME
import com.moengage.core.internal.USER_ATTRIBUTE_USER_GENDER
import com.moengage.core.internal.USER_ATTRIBUTE_USER_LAST_NAME
import com.moengage.core.internal.USER_ATTRIBUTE_USER_LOCATION
import com.moengage.core.internal.USER_ATTRIBUTE_USER_MOBILE
import com.moengage.core.internal.USER_ATTRIBUTE_USER_NAME
import com.moengage.core.internal.integrations.segment.SegmentIntegrationHelper
import com.moengage.core.internal.logger.Logger
import com.moengage.core.internal.model.IntegrationMeta
import com.moengage.core.model.GeoLocation
import com.segment.analytics.kotlin.android.utilities.toJSONObject
import com.segment.analytics.kotlin.core.AliasEvent
import com.segment.analytics.kotlin.core.BaseEvent
import com.segment.analytics.kotlin.core.IdentifyEvent
import com.segment.analytics.kotlin.core.Settings
import com.segment.analytics.kotlin.core.Storage
import com.segment.analytics.kotlin.core.TrackEvent
import com.segment.analytics.kotlin.core.platform.DestinationPlugin
import com.segment.analytics.kotlin.core.platform.Plugin
import com.segment.analytics.kotlin.core.platform.VersionedPlugin
import com.segment.analytics.kotlin.core.utilities.getDouble
import com.segment.analytics.kotlin.core.utilities.getString
import com.segment.analytics.kotlin.destinations.moengage.internal.map
import kotlinx.serialization.json.jsonObject
import org.json.JSONObject
import java.util.concurrent.Executors

private const val MOENGAGE_SEGMENT_INTEGRATION_KEY = "MoEngage"
private const val INTEGRATION_META_TYPE = "segment"
private const val USER_TRAIT_ANONYMOUS_ID = "anonymousId"
private const val USER_TRAIT_EMAIL = "email"
private const val USER_TRAIT_UNIQUE_ID = "userId"
private const val USER_TRAIT_NAME = "name"
private const val USER_TRAIT_MOBILE = "phone"
private const val USER_TRAIT_FIRST_NAME = "firstName"
private const val USER_TRAIT_LAST_NAME = "lastName"
private const val USER_TRAIT_GENDER = "gender"
private const val USER_TRAIT_BIRTHDAY = "birthday"
private const val USER_TRAIT_ADDRESS = "address"
private const val USER_TRAIT_ADDRESS_CITY = "city"
private const val USER_TRAIT_ADDRESS_STATE = "state"
private const val USER_TRAIT_ADDRESS_COUNTRY = "country"
private const val USER_TRAIT_LOCATION = "location"
private const val USER_TRAIT_LOCATION_LONGITUDE = "longitude"
private const val USER_TRAIT_LOCATION_LATITUDE = "latitude"
private const val USER_ATTRIBUTE_SEGMENT_ID = "USER_ATTRIBUTE_SEGMENT_ID"

@kotlinx.serialization.Serializable
data class MoEngageSettings(val apiKey: String)

class MoEngageDestination(
    private val application: Application
) : DestinationPlugin(), VersionedPlugin {

    private val integrationHelper: SegmentIntegrationHelper =
        SegmentIntegrationHelper(application.applicationContext)
    private var workspaceId: String? = null

    companion object {
        private const val tag = "MoEngageDestination_${BuildConfig.MOENGAGE_SEGMENT_KOTLIN_VERSION}"

        val mapper: Map<String, String> = mapOf(
            USER_TRAIT_ANONYMOUS_ID to USER_ATTRIBUTE_SEGMENT_ID,
            USER_TRAIT_EMAIL to USER_ATTRIBUTE_USER_EMAIL,
            USER_TRAIT_UNIQUE_ID to USER_ATTRIBUTE_UNIQUE_ID,
            USER_TRAIT_NAME to USER_ATTRIBUTE_USER_NAME,
            USER_TRAIT_MOBILE to USER_ATTRIBUTE_USER_MOBILE,
            USER_TRAIT_FIRST_NAME to USER_ATTRIBUTE_USER_FIRST_NAME,
            USER_TRAIT_LAST_NAME to USER_ATTRIBUTE_USER_LAST_NAME,
            USER_TRAIT_GENDER to USER_ATTRIBUTE_USER_GENDER,
            USER_TRAIT_BIRTHDAY to USER_ATTRIBUTE_USER_BDAY

        )
    }

    override val key: String
        get() = MOENGAGE_SEGMENT_INTEGRATION_KEY

    override fun update(settings: Settings, type: Plugin.UpdateType) {
        try {
            super.update(settings, type)
            Logger.print { "$tag update(): will try to sync $settings" }
            if (!settings.hasIntegrationSettings(key)) {
                Logger.print { "$tag update(): no integration settings found for $key" }
                return
            }
            val moEngageSettings: MoEngageSettings = settings.destinationSettings(key) ?: run {
                Logger.print { "$tag update(): required keys missing" }
                return
            }
            if (workspaceId != null && workspaceId == moEngageSettings.apiKey) {
                Logger.print { "$tag update(): instanceId already initialised" }
                return
            }

            Logger.print { "$tag update(): initialising sdk" }
            workspaceId = moEngageSettings.apiKey
            integrationHelper.initialize(
                application,
                workspaceId,
                IntegrationMeta(INTEGRATION_META_TYPE, version())
            )
            Logger.print { "$tag update(): Segment Integration initialised." }
            trackAnonymousId()
        } catch (t: Throwable) {
            Logger.print(LogLevel.ERROR, t) { "$tag update(): " }
        }
    }

    override fun alias(payload: AliasEvent): BaseEvent {
        try {
            super.alias(payload)
            Logger.print { "$tag alias(): will try to update $payload" }
            integrationHelper.setAlias(alias = payload.userId, workspaceId = workspaceId)
        } catch (t: Throwable) {
            Logger.print(LogLevel.ERROR, t) { "$tag alias(): " }
        }
        return payload
    }

    @Suppress("UNCHECKED_CAST")
    override fun identify(payload: IdentifyEvent): BaseEvent {
        try {
            super.identify(payload)
            Logger.print { "$tag identify(): will try to track $payload" }

            val traits = payload.traits
            val transformedTraits = traits.map(mapper) as MutableMap<String, Any>

            val userId = payload.userId.ifEmpty {
                transformedTraits.remove(USER_ATTRIBUTE_UNIQUE_ID) as? String ?: ""
            }
            integrationHelper.identifyUser(identity = userId, workspaceId = workspaceId)
            integrationHelper.trackUserAttributes(transformedTraits, workspaceId)
            if (traits.isNotEmpty()) {
                val address = traits[USER_TRAIT_ADDRESS]
                if (address != null && address.jsonObject.isNotEmpty()) {
                    val city = address.jsonObject.getString(USER_TRAIT_ADDRESS_CITY)

                    if (!city.isNullOrEmpty()) {
                        integrationHelper.trackUserAttribute(
                            USER_TRAIT_ADDRESS_CITY,
                            city,
                            workspaceId
                        )
                    }
                    val country = address.jsonObject.getString(USER_TRAIT_ADDRESS_COUNTRY)
                    if (!country.isNullOrEmpty()) {
                        integrationHelper.trackUserAttribute(
                            USER_TRAIT_ADDRESS_COUNTRY,
                            country,
                            workspaceId
                        )
                    }
                    val state = address.jsonObject.getString(USER_TRAIT_ADDRESS_STATE)
                    if (!state.isNullOrEmpty()) {
                        integrationHelper.trackUserAttribute(
                            USER_TRAIT_ADDRESS_STATE,
                            state,
                            workspaceId
                        )
                    }
                }
            }
            val location = payload.traits[USER_TRAIT_LOCATION]
            if (location != null && location.jsonObject.isNotEmpty()) {
                integrationHelper.trackUserAttribute(
                    USER_ATTRIBUTE_USER_LOCATION,
                    GeoLocation(
                        location.jsonObject.getDouble(USER_TRAIT_LOCATION_LATITUDE) ?: 0.0,
                        location.jsonObject.getDouble(USER_TRAIT_LOCATION_LONGITUDE) ?: 0.0
                    ),
                    workspaceId
                )
            }
        } catch (t: Throwable) {
            Logger.print(LogLevel.ERROR, t) { "$tag identify(): " }
        }

        return payload
    }

    override fun reset() {
        try {
            super.reset()
            Logger.print { "$tag reset(): will try to reset" }
            integrationHelper.logout(workspaceId)
        } catch (t: Throwable) {
            Logger.print(LogLevel.ERROR, t) { "$tag reset(): " }
        }
    }

    override fun track(payload: TrackEvent): BaseEvent {
        try {
            super.track(payload)
            Logger.print { "$tag track(): will try to track $payload" }
            if (payload.properties.isNotEmpty()) {
                integrationHelper.trackEvent(
                    payload.event,
                    payload.properties.toJSONObject(),
                    workspaceId
                )
            } else {
                integrationHelper.trackEvent(payload.event, JSONObject(), workspaceId)
            }
        } catch (t: Throwable) {
            Logger.print(LogLevel.ERROR, t) { "$tag track(): " }
        }
        return payload
    }

    override fun version(): String {
        return BuildConfig.MOENGAGE_SEGMENT_KOTLIN_VERSION
    }

    private fun trackAnonymousId() {
        try {
            Executors.newSingleThreadExecutor().submit {
                try {
                    Logger.print { "$tag trackAnonymousId() : will try to sync anonymousId" }
                    val anonymousId = analytics.storage.read(Storage.Constants.AnonymousId)
                    Logger.print { "$tag trackAnonymousId() : $anonymousId" }
                    anonymousId?.let {
                        integrationHelper.trackAnonymousId(anonymousId, workspaceId)
                    }
                    Logger.print { "$tag trackAnonymousId() : anonymousId synced" }
                } catch (t: Throwable) {
                    Logger.print(LogLevel.ERROR, t) { "$tag trackAnonymousId(): " }
                }
            }
        } catch (t: Throwable) {
            Logger.print(LogLevel.ERROR, t) { "$tag trackAnonymousId(): " }
        }
    }
}

package com.segment.analytics.kotlin.destinations.moengage

import android.app.Application
import com.moengage.core.LogLevel
import com.moengage.core.MoECoreHelper
import com.moengage.core.analytics.MoEAnalyticsHelper
import com.moengage.core.internal.USER_ATTRIBUTE_UNIQUE_ID
import com.moengage.core.internal.USER_ATTRIBUTE_USER_BDAY
import com.moengage.core.internal.USER_ATTRIBUTE_USER_EMAIL
import com.moengage.core.internal.USER_ATTRIBUTE_USER_FIRST_NAME
import com.moengage.core.internal.USER_ATTRIBUTE_USER_GENDER
import com.moengage.core.internal.USER_ATTRIBUTE_USER_LAST_NAME
import com.moengage.core.internal.USER_ATTRIBUTE_USER_LOCATION
import com.moengage.core.internal.USER_ATTRIBUTE_USER_MOBILE
import com.moengage.core.internal.USER_ATTRIBUTE_USER_NAME
import com.moengage.core.internal.integrations.MoEIntegrationHelper
import com.moengage.core.internal.logger.Logger
import com.moengage.core.internal.model.IntegrationMeta
import com.moengage.core.model.GeoLocation
import com.moengage.core.model.IntegrationPartner
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

    private lateinit var integrationHelper: MoEIntegrationHelper
    private lateinit var instanceId: String

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
            if (type == Plugin.UpdateType.Initial && settings.hasIntegrationSettings(key)) {
                val moEngageSettings: MoEngageSettings = settings.destinationSettings(key) ?: return
                instanceId = moEngageSettings.apiKey
                integrationHelper = MoEIntegrationHelper(application, IntegrationPartner.SEGMENT)
                integrationHelper.initialize(instanceId, application)
                MoEIntegrationHelper.addIntegrationMeta(
                    IntegrationMeta(
                        INTEGRATION_META_TYPE,
                        version()
                    ),
                    instanceId
                )
                Logger.print { "$tag update(): Segment Integration initialised." }
                trackAnonymousId()
            }
        } catch (t: Throwable) {
            Logger.print(LogLevel.ERROR, t) { "$tag update(): " }
        }
    }

    override fun alias(payload: AliasEvent): BaseEvent {
        try {
            super.alias(payload)
            Logger.print { "$tag alias(): will try to update $payload" }
            MoEAnalyticsHelper.setAlias(application.applicationContext, payload.userId, instanceId)
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

            val uniqueId = if (payload.userId.isNotEmpty()) {
                payload.userId
            } else if (transformedTraits.containsKey(USER_ATTRIBUTE_UNIQUE_ID)) {
                transformedTraits.remove(USER_ATTRIBUTE_UNIQUE_ID) ?: ""
            } else {
                ""
            }
            MoEAnalyticsHelper.setUniqueId(
                application.applicationContext,
                uniqueId,
                instanceId
            )
            integrationHelper.trackUserAttribute(transformedTraits, instanceId)
            if (traits.isNotEmpty()) {
                val address = traits[USER_TRAIT_ADDRESS]
                if (address != null && address.jsonObject.isNotEmpty()) {
                    val city = address.jsonObject.getString(USER_TRAIT_ADDRESS_CITY)
                    if (!city.isNullOrEmpty()) {
                        MoEAnalyticsHelper.setUserAttribute(
                            application.applicationContext,
                            USER_TRAIT_ADDRESS_CITY,
                            city,
                            instanceId
                        )
                    }
                    val country = address.jsonObject.getString(USER_TRAIT_ADDRESS_COUNTRY)
                    if (!country.isNullOrEmpty()) {
                        MoEAnalyticsHelper.setUserAttribute(
                            application.applicationContext,
                            USER_TRAIT_ADDRESS_COUNTRY,
                            country,
                            instanceId
                        )
                    }
                    val state = address.jsonObject.getString(USER_TRAIT_ADDRESS_STATE)
                    if (!state.isNullOrEmpty()) {
                        MoEAnalyticsHelper.setUserAttribute(
                            application.applicationContext,
                            USER_TRAIT_ADDRESS_STATE,
                            state,
                            instanceId
                        )
                    }
                }
            }
            val location = payload.traits[USER_TRAIT_LOCATION]
            if (location != null && location.jsonObject.isNotEmpty()) {
                MoEAnalyticsHelper.setUserAttribute(
                    application.applicationContext,
                    USER_ATTRIBUTE_USER_LOCATION,
                    GeoLocation(
                        location.jsonObject.getDouble(USER_TRAIT_LOCATION_LATITUDE) ?: 0.0,
                        location.jsonObject.getDouble(USER_TRAIT_LOCATION_LONGITUDE) ?: 0.0
                    ),
                    instanceId
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
            MoECoreHelper.logoutUser(application.applicationContext, instanceId)
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
                    instanceId
                )
            } else {
                integrationHelper.trackEvent(payload.event, JSONObject(), instanceId)
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
                    integrationHelper.trackAnonymousId(anonymousId, instanceId)
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

package com.segment.analytics.kotlin.destinations.moengage

import android.app.Application
import com.moengage.core.LogLevel
import com.moengage.core.MoECoreHelper.logoutUser
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
import com.moengage.core.internal.integrations.MoEIntegrationHelper.Companion.addIntegrationMeta
import com.moengage.core.internal.logger.Logger
import com.moengage.core.internal.model.IntegrationMeta
import com.moengage.core.model.GeoLocation
import com.moengage.core.model.IntegrationPartner
import com.segment.analytics.kotlin.android.utilities.toJSONObject
import com.segment.analytics.kotlin.core.AliasEvent
import com.segment.analytics.kotlin.core.BaseEvent
import com.segment.analytics.kotlin.core.IdentifyEvent
import com.segment.analytics.kotlin.core.Settings
import com.segment.analytics.kotlin.core.TrackEvent
import com.segment.analytics.kotlin.core.Traits
import com.segment.analytics.kotlin.core.platform.DestinationPlugin
import com.segment.analytics.kotlin.core.platform.Plugin
import com.segment.analytics.kotlin.core.platform.VersionedPlugin
import com.segment.analytics.kotlin.core.utilities.getDouble
import com.segment.analytics.kotlin.core.utilities.getString
import com.segment.analytics.kotlin.core.utilities.mapTransform
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import org.json.JSONObject

@kotlinx.serialization.Serializable
data class MoEngageSettings(val apiKey: String)

class MoEngageDestination(private val application: Application) : DestinationPlugin(),
    VersionedPlugin {

    private var moEngageSettings: MoEngageSettings? = null
    private lateinit var integrationHelper: MoEIntegrationHelper
    private lateinit var instanceId: String
    private lateinit var analyticsHelper: MoEAnalyticsHelper


    companion object {
        private const val tag = "MoEngageDestination"

        val mapper: Map<String, String> = mapOf(
            "anonymousId" to "USER_ATTRIBUTE_SEGMENT_ID",
            "email" to USER_ATTRIBUTE_USER_EMAIL,
            "userId" to USER_ATTRIBUTE_UNIQUE_ID,
            "name" to USER_ATTRIBUTE_USER_NAME,
            "phone" to USER_ATTRIBUTE_USER_MOBILE,
            "firstName" to USER_ATTRIBUTE_USER_FIRST_NAME,
            "lastName" to USER_ATTRIBUTE_USER_LAST_NAME,
            "gender" to USER_ATTRIBUTE_USER_GENDER,
            "birthday" to USER_ATTRIBUTE_USER_BDAY

        )
    }

    override val key: String
        get() = "MoEngage"

    override fun update(settings: Settings, type: Plugin.UpdateType) {
        super.update(settings, type)
        if (type == Plugin.UpdateType.Initial) {
            if (settings.hasIntegrationSettings(key)) {
                moEngageSettings = settings.destinationSettings(key)
                moEngageSettings?.let {
                    instanceId = it.apiKey
                    analyticsHelper = MoEAnalyticsHelper
                    integrationHelper =
                        MoEIntegrationHelper(application, IntegrationPartner.SEGMENT)
                    Logger.print { "$tag Segment Integration initialised." }
                    integrationHelper.initialize(instanceId, application)
                    addIntegrationMeta(
                        IntegrationMeta("segment", BuildConfig.MOENGAGE_SEGMENT_KOTLIN_VERSION),
                        instanceId
                    )
                }
                trackAnonymousId()
            }
        }
    }

    override fun alias(payload: AliasEvent): BaseEvent {
        try {
            super.alias(payload)
            Logger.print { "$tag alias(): " }
            analyticsHelper.setAlias(application.applicationContext, payload.userId, instanceId)
        } catch (t: Throwable) {
            Logger.print(LogLevel.ERROR) { "$tag alias(): " }
        }
        return payload
    }

    override fun identify(payload: IdentifyEvent): BaseEvent {
        try {
            super.identify(payload)
            Logger.print { "$tag identify(): " }
            val traits: Traits = payload.traits
            if (!traits.isEmpty()) {
                integrationHelper.trackUserAttribute(traits.map(mapper), instanceId)
                val address = traits["address"]
                if (address != null && !address.jsonObject.isEmpty()) {
                    val city = address.jsonObject.getString("city")
                    if (!city.isNullOrEmpty()) {
                        analyticsHelper.setUserAttribute(
                            application.applicationContext,
                            "city",
                            city,
                            instanceId
                        )
                    }
                    val country = address.jsonObject.getString("country")
                    if (!country.isNullOrEmpty()) {
                        analyticsHelper.setUserAttribute(
                            application.applicationContext, "country", country,
                            instanceId
                        )
                    }
                    val state = address.jsonObject.getString("state")
                    if (!state.isNullOrEmpty()) {
                        analyticsHelper.setUserAttribute(
                            application.applicationContext, "state", state,
                            instanceId
                        )
                    }
                }
            }
            val location = payload.traits["location"]
            if (location != null && !location.jsonObject.isEmpty()) {
                analyticsHelper.setUserAttribute(
                    application.applicationContext, USER_ATTRIBUTE_USER_LOCATION,
                    GeoLocation(
                        location.jsonObject.getDouble("latitude") ?: 0.0, location
                            .jsonObject.getDouble("longitude") ?: 0.0
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
            Logger.print { "$tag reset(): " }
            logoutUser(application.applicationContext, instanceId)
        } catch (t: Throwable) {
            Logger.print(LogLevel.ERROR, t) { "$tag reset(): " }
        }
    }

    override fun track(payload: TrackEvent): BaseEvent {
        try {
            super.track(payload)
            Logger.print { "$tag track(): " }
            if (!payload.properties.isEmpty()) {
                integrationHelper.trackEvent(
                    payload.event, payload.properties.toJSONObject(),
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

    private fun Map<String, JsonElement>.map(
        keyMapper: Map<String, String>,
    ): Map<String, JsonElement> = JsonObject(this).mapTransform(keyMapper, null)

    private fun trackAnonymousId() {
        try {
            integrationHelper.trackAnonymousId(
                analytics.traits()?.getString("anonymousId"),
                instanceId
            )
        } catch (th: Throwable) {
            Logger.print(LogLevel.ERROR, th) { "$tag trackAnonymousId(): " }
        }
    }
}
package com.moengage.segment.kotlin.sampleapp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.moengage.core.internal.utils.currentISOTime
import com.segment.analytics.kotlin.core.Analytics
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AnalyticsKotlinActivity : AppCompatActivity() {

    private lateinit var analytics: Analytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analytics_kotlin)
        analytics = KotlinSampleApplication.analytics
        findViewById<TextView>(R.id.moengage_app_id).text = buildString {
            append("MoEngage App Id: ")
            append(BuildConfig.MOENAGE_APP_ID)
        }

        analytics.identify(getTraits())
        analytics.track("ON_CREATE")
        analytics.track("TRACK_LOCATION", getEventProperties())
    }

    private fun getTraits(): JsonObject {
        return buildJsonObject {
            put("userId", "abc@example.com")
            put("name", "MoEngage")
            put("email", "abc@example.com")
            put("date", currentISOTime())
            put("birthday", currentISOTime())
            put("location", getLocationProperties())
            put("salary", 190000.0f)
            put("isEmployed", true)
        }
    }

    private fun getEventProperties(): JsonObject {
        return buildJsonObject {
            put("location", getLocationProperties())
        }
    }

    private fun getLocationProperties(): JsonObject {
        return buildJsonObject {
            put("latitude", 12.971599)
            put("longitude", 77.594566)
        }
    }
}
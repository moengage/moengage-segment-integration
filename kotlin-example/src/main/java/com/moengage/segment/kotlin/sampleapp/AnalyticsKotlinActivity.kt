package com.moengage.segment.kotlin.sampleapp

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.moengage.core.internal.utils.currentISOTime
import com.segment.analytics.kotlin.core.Analytics
import kotlinx.serialization.json.Json
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
        findViewById<Button>(R.id.button_identify_1).setOnClickListener {
            analytics.identify("abc@example.com", getTraitsWithoutUniqueId())
        }
        findViewById<Button>(R.id.button_identify_2).setOnClickListener {
            analytics.identify(getTraitsWithoutUniqueId())
        }
        findViewById<Button>(R.id.button_identify_3).setOnClickListener {
            analytics.identify(getTraits())
        }
        findViewById<Button>(R.id.button_track).setOnClickListener {
            analytics.track("ON_CREATE")
            analytics.track("TRACK_LOCATION", getEventProperties())
        }
        findViewById<Button>(R.id.button_alias).setOnClickListener {
            analytics.alias("abc1@example.com")
        }
        findViewById<Button>(R.id.button_reset).setOnClickListener {
            analytics.reset()
        }

    }

    private fun getTraitsWithoutUniqueId(): JsonObject {
        return buildJsonObject {
            put("name", "MoEngage")
            put("email", "abc@example.com")
            put("date", currentISOTime())
            put("birthday", currentISOTime())
            put("location", getLocationProperties())
            put("salary", 190000.0f)
            put("isEmployed", true)
        }
    }

    private fun getTraits(): JsonObject {
        return buildJsonObject {
            put("userId", "abc2@example.com")
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
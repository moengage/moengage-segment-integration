package com.moengage.segment.kotlin.sampleapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.moengage.core.internal.utils.currentISOTime
import com.segment.analytics.kotlin.core.Analytics
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AnalyticsKotlinActivity : AppCompatActivity() {

    private lateinit var analytics: Analytics

    private val name: String = "MoEngage"
    private val email: String = "abc@example.com"
    private val date: String = currentISOTime()
    private val dob: String = currentISOTime()
    private val salary: Float = 190000.0f
    private val isEmployed: Boolean = true
    private val latitude: Double = 12.971599
    private val longitude: Double = 77.594566

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
            put("userId", email)
            put("name", name)
            put("email", email)
            put("date", date)
            put("birthday", dob)
            put("location", getLocationProperties())
            put("salary", salary)
            put("isEmployed", isEmployed)
        }
    }

    private fun getEventProperties(): JsonObject {
        return buildJsonObject {
            put("location", getLocationProperties())
        }
    }

    private fun getLocationProperties(): JsonObject {
        return buildJsonObject {
            put("latitude", latitude)
            put("longitude", longitude)
        }
    }
}
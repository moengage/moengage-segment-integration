package com.moengage.segment.example

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.segment.analytics.Analytics
import com.segment.analytics.Properties
import com.segment.analytics.Traits
import java.util.*

class AnalyticsAndroidActivity : AppCompatActivity() {

    private val name: String = "MoEngage"
    private val email: String = "abc@example.com"
    private val salary: Float = 190000.0f
    private val isEmployed: Boolean = true
    private val latitude: Double = 12.971599
    private val longitude: Double = 77.594566

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analytics_android)
        findViewById<TextView>(R.id.moengage_app_id).text = buildString {
            append("MoEngage App Id: ")
            append(BuildConfig.MOENAGE_APP_ID)
        }

        Analytics.with(this).identify(getTraits())
        Analytics.with(this).track("ON_CREATE")
        Analytics.with(this).track("TRACK_LOCATION", getEventProperties())
    }

    private fun getTraits(): Traits {
        val traits = Traits()
        traits.putName(name)
        traits.putBirthday(Date())
        traits.putEmail(email)
        traits["time"] = Date()
        traits["salary"] = salary
        traits["isEmployed"] = isEmployed
        traits["location"] = getLocationProperties()
        return traits
    }

    private fun getEventProperties(): Properties {
        val properties = Properties()
        properties["latitude"] = latitude
        properties["longitude"] = longitude
        return properties
    }

    private fun getLocationProperties(): Traits {
        val traits = Traits()
        traits["latitude"] = latitude
        traits["longitude"] = longitude
        return traits
    }
}
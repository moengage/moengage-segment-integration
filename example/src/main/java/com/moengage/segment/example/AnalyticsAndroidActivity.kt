package com.moengage.segment.example

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.segment.analytics.Analytics
import com.segment.analytics.Properties
import com.segment.analytics.Traits
import java.util.*

class AnalyticsAndroidActivity : AppCompatActivity() {

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
        traits.putName("MoEngage")
        traits.putBirthday(Date())
        traits.putEmail("abc@example.com")
        traits["time"] = Date()
        traits["salary"] = 190000.0f
        traits["isEmployed"] = true
        return traits
    }

    private fun getEventProperties(): Properties {
        val properties = Properties()
        properties["latitude"] = 12.971599
        properties["longitude"] = 77.594566
        return properties
    }
}
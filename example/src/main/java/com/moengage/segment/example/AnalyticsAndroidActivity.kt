package com.moengage.segment.example

import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.segment.analytics.Analytics
import com.segment.analytics.Properties
import com.segment.analytics.Traits
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class AnalyticsAndroidActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analytics_android)
        findViewById<TextView>(R.id.moengage_app_id).text = buildString {
            append("MoEngage App Id : ")
            append(BuildConfig.MOENAGE_APP_ID)
        }

        findViewById<TextView>(R.id.segment_write_key).text = buildString {
            append("Segment Write Key : ")
            append(BuildConfig.SEGMENT_WRITE_KEY)
        }

        findViewById<Button>(R.id.button_alias).setOnClickListener {
            val userId = findViewById<EditText>(R.id.text_alias).text.toString()
            Analytics.with(this).alias(userId)
            val properties = Properties()
            properties["alias_name"] = properties[userId]
            Analytics.with(this).track("EVENT_ALIAS_CLICKED", properties)
            Toast.makeText(this, "Tracking Event: ALIAS Clicked", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.pick_time).setOnClickListener {
            object : TimePickerDialog(this, object : OnTimeSetListener {
                override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                    view ?: return
                    val df: DateFormat =
                        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'", Locale.ENGLISH)
                    findViewById<EditText>(R.id.text_user_attribute_value).setText(
                        df.format(
                            Date(
                                view.drawingTime
                            )
                        )
                    )

                }
            }, 12, 0, false) {}.show()
        }

        findViewById<Button>(R.id.button_identify).setOnClickListener {
            val traits = Traits()
            val attributeName =
                findViewById<EditText>(R.id.text_user_attribute_name).text.toString()
            val attributeValue =
                findViewById<EditText>(R.id.text_user_attribute_value).text.toString()
            traits[attributeName] = attributeValue

            Analytics.with(this).identify(traits)

            val properties = Properties()
            properties[attributeName] = attributeValue
            Analytics.with(this).track("EVENT_IDENTIFY_CLICKED", properties)
            Toast.makeText(this, "Tracking Event: Identify Clicked", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.button_reset).setOnClickListener {
            Analytics.with(this).reset()
            Analytics.with(this).track("EVENT_RESET_CLICKED")
            Toast.makeText(this, "Tracking Event: Reset Clicked", Toast.LENGTH_SHORT).show()
        }
    }
}
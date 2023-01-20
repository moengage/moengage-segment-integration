package com.moengage.segment.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class KotlinActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin)
        val analytics = (application as SampleApplication).analyticsKt

        //track user attributes
        trackUserInfo("25", "Male", "Bengaluru", analytics)

        //track event without attributes
        analytics.track("only event")
        //track event with attributes
        analytics.track("Email button Click", buildJsonObject {
            put("email", "opened")
        })

    }

    private fun trackUserInfo(
        age: String?,
        gender: String?,
        city: String?,
        analytics: com.segment.analytics.kotlin.core.Analytics
    ) {
        //userId is mapped to USER_ATTRIBUTE_UNIQUE_ID of MoEngage SDK
        val userId = "mobiledev@moengage.com"
        val firstName = "Mobile"
        val lastName = "Dev"
        val fullName = "$firstName $lastName"
        val email = "mobiledev@moengage.com"
        try {

            analytics.identify(userId, buildJsonObject {
                put("email", email)
                put("firstName", firstName)
                put("lastName", lastName)
                put("name", fullName)
                if (age != null && age.isNotEmpty()) {
                    try {
                        val parsedAge = age.toInt()
                        put("age", parsedAge)
                    } catch (e: NumberFormatException) {
                    }
                }
                if (gender != null && gender.isNotEmpty()) {
                    put("gender", gender)
                }
                if (city != null && city.isNotEmpty()) {
                    put("user_location", city)
                }
            })
        } catch (e: Exception) {
        }
    }
}
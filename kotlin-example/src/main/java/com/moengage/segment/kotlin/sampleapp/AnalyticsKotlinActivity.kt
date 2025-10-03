/*
 * Copyright (c) 2014-2025 MoEngage Inc.
 *
 * All rights reserved.
 *
 *  Use of source code or binaries contained within MoEngage SDK is permitted only to enable use of the MoEngage platform by customers of MoEngage.
 *  Modification of source code and inclusion in mobile apps is explicitly allowed provided that all other conditions are met.
 *  Neither the name of MoEngage nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 *  Redistribution of source code or binaries is disallowed except with specific prior written permission. Any such redistribution must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.moengage.segment.kotlin.sampleapp

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.moengage.core.internal.utils.currentISOTime
import com.segment.analytics.kotlin.core.Analytics
import com.segment.analytics.kotlin.core.checkSettings
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AnalyticsKotlinActivity : AppCompatActivity() {

    private lateinit var analytics: Analytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analytics_kotlin)
        analytics = KotlinSampleApplication.analytics

        analytics.identify(
            "user-123",
            buildJsonObject {
                put("username", "MisterWhiskers")
                put("email", "hello@test.com")
                put("plan", "premium")
            })

        findViewById<TextView>(R.id.moengage_app_id).text = buildString {
            append("MoEngage App Id: ")
            append(BuildConfig.MOENGAGE_WORKSPACE_ID)
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
            analytics.track("SegmentTestEvent", getEventProperties())
        }
        findViewById<Button>(R.id.button_alias).setOnClickListener {
            analytics.alias("abc1@example.com")
        }
        findViewById<Button>(R.id.button_reset).setOnClickListener { analytics.reset() }
        findViewById<Button>(R.id.fetching_settings).setOnClickListener {
            lifecycleScope.launch { analytics.checkSettings() }
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
            put(
                "address",
                buildJsonObject {
                    put("street", "6th St")
                    put("city", "San Francisco")
                    put("state", "CA")
                    put("postalCode", "94103")
                    put("country", "USA")
                })
            put(
                "jsonArrayKey",
                buildJsonArray {
                    add("val1")
                    add("val2")
                    add("val3")
                    add(
                        buildJsonObject {
                            put("nestedKey1", "nestedValue1")
                            put("nestedKey2", 1)
                            put("nestedKey3", true)
                        })
                })
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
            put(
                "jsonArrayKey",
                buildJsonArray {
                    add(1)
                    add(2)
                    add(3)
                })
            put(
                "jsonObjectKey",
                buildJsonObject {
                    put("stringKey", "stringVal")
                    put("intKey", 1)
                    put("boolKey", false)
                })
        }
    }

    private fun getLocationProperties(): JsonObject {
        return buildJsonObject {
            put("latitude", 12.971599)
            put("longitude", 77.594566)
        }
    }
}

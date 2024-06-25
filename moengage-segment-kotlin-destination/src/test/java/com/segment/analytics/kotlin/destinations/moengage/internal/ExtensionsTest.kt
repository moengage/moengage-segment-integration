package com.segment.analytics.kotlin.destinations.moengage.internal

import com.moengage.core.internal.USER_ATTRIBUTE_USER_EMAIL
import com.moengage.core.internal.USER_ATTRIBUTE_USER_NAME
import com.segment.analytics.kotlin.destinations.moengage.MoEngageDestination.Companion.mapper
import junitparams.JUnitParamsRunner
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Test class for [Extension]
 *
 * @author Abhishek Kumar
 */
@RunWith(value = JUnitParamsRunner::class)
internal class ExtensionsTest {

    @Test
    fun jsonObjectToMapTest() {
        val attributes = JsonObject(
            mapOf(
                "name" to JsonPrimitive("MoEngage"),
                "email" to JsonPrimitive("abc@example.com"),
                "salary" to JsonPrimitive(190000),
                "isEmployed" to JsonPrimitive(true),
                "address" to JsonObject(
                    mapOf(
                        "street" to JsonPrimitive("6th St"),
                        "city" to JsonPrimitive("San Francisco"),
                        "state" to JsonPrimitive("CA"),
                        "postalCode" to JsonPrimitive(94103),
                        "country" to JsonPrimitive("USA")
                    )
                ),
                "jsonArrayKey" to JsonArray(
                    listOf(
                        JsonPrimitive("val1"),
                        JsonPrimitive("val2"),
                        JsonPrimitive("val3"),
                        JsonObject(
                            mapOf(
                                "nestedKey1" to JsonPrimitive("nestedValue1"),
                                "nestedKey2" to JsonPrimitive(1),
                                "nestedKey3" to JsonPrimitive(true)
                            )
                        )
                    )
                )
            )
        )

        val mappedAttributes = mapOf(
            USER_ATTRIBUTE_USER_NAME to "MoEngage",
            USER_ATTRIBUTE_USER_EMAIL to "abc@example.com",
            "salary" to 190000,
            "isEmployed" to true,
            "address" to JSONObject()
                .put("country", "USA")
                .put("city", "San Francisco")
                .put("street", "6th St")
                .put("postalCode", 94103)
                .put("state", "CA"),
            "jsonArrayKey" to JSONArray(
                listOf(
                    "val1",
                    "val2",
                    "val3",
                    JSONObject()
                        .put("nestedKey3", true)
                        .put("nestedKey2", 1)
                        .put("nestedKey1", "nestedValue1")
                )
            )
        )

        assert(
            isEqual(
                attributes.map(mapper),
                mappedAttributes
            )
        )
    }
}

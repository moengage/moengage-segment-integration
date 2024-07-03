package com.segment.analytics.kotlin.destinations.moengage.internal

import com.segment.analytics.kotlin.android.utilities.toJSONArray
import com.segment.analytics.kotlin.android.utilities.toJSONObject
import com.segment.analytics.kotlin.core.utilities.toContent
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject

/**
 * Map the JsonObject to Map with provided key mapper
 * Notes: This doesn't convert JSONObject / JSONArray to Map
 */
internal fun JsonObject.map(mapper: Map<String, String>): Map<String, Any> {
    val mappedValue = mutableMapOf<String, Any>()
    for (key in this.keys) {
        val value = this[key]
        val mappedKey = mapper[key] ?: key
        when (value) {
            is JsonObject -> mappedValue[mappedKey] = value.toJSONObject()

            is JsonArray -> mappedValue[mappedKey] = value.toJSONArray()

            else -> {
                value?.toContent()?.let {
                    mappedValue[mappedKey] = it
                }
            }
        }
    }

    return mappedValue
}

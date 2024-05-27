package com.segment.analytics.kotlin.destinations.moengage.internal

import org.json.JSONArray
import org.json.JSONObject
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode

/**
 * Return true of twp given map is equal
 */
internal fun isEqual(
    actual: Map<String, Any>,
    expected: Map<String, Any>
): Boolean {
    if (actual.size != expected.size) return false
    if (actual.keys != expected.keys) return false

    actual.forEach { (actualKey, actualValue) ->
        when (actualValue) {
            is JSONObject -> {
                JSONAssert.assertEquals(
                    actualValue,
                    expected[actualKey] as JSONObject,
                    JSONCompareMode.LENIENT
                )
            }

            is JSONArray -> {
                JSONAssert.assertEquals(
                    actualValue,
                    expected[actualKey] as JSONArray,
                    JSONCompareMode.LENIENT
                )
            }

            else -> {
                if (actualValue != expected[actualKey]) return false
            }
        }
    }

    return true
}

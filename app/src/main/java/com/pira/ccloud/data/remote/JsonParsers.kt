package com.pira.ccloud.data.remote

import com.pira.ccloud.data.model.Country
import com.pira.ccloud.data.model.Genre
import com.pira.ccloud.data.model.Source
import org.json.JSONArray
import org.json.JSONObject

/**
 * Shared JSON parsing utilities used by all repositories.
 * Eliminates duplicate parsing code across MovieRepository, SeriesRepository, etc.
 */
object JsonParsers {

    fun parseGenres(jsonArray: JSONArray): List<Genre> {
        val genres = mutableListOf<Genre>()
        for (i in 0 until jsonArray.length()) {
            try {
                val obj = jsonArray.getJSONObject(i)
                genres.add(
                    Genre(
                        id = obj.optInt("id", 0),
                        title = obj.optString("title", "Unknown")
                    )
                )
            } catch (_: Exception) {
                // Skip items that fail to parse
            }
        }
        return genres
    }

    fun parseSources(jsonArray: JSONArray): List<Source> {
        val sources = mutableListOf<Source>()
        for (i in 0 until jsonArray.length()) {
            try {
                val obj = jsonArray.getJSONObject(i)
                sources.add(
                    Source(
                        id = obj.optInt("id", 0),
                        quality = obj.optString("quality", "Unknown"),
                        type = obj.optString("type", "Unknown"),
                        url = obj.optString("url", "")
                    )
                )
            } catch (_: Exception) {
                // Skip items that fail to parse
            }
        }
        return sources
    }

    fun parseCountries(jsonArray: JSONArray): List<Country> {
        val countries = mutableListOf<Country>()
        for (i in 0 until jsonArray.length()) {
            try {
                val obj = jsonArray.getJSONObject(i)
                countries.add(
                    Country(
                        id = obj.optInt("id", 0),
                        title = obj.optString("title", "Unknown"),
                        image = obj.optString("image", "")
                    )
                )
            } catch (_: Exception) {
                // Skip items that fail to parse
            }
        }
        return countries
    }

    /**
     * Safely parse a nested array, returning empty list on failure.
     */
    fun parseNestedArray(obj: JSONObject, key: String, parser: (JSONArray) -> List<*>): List<*> {
        return try {
            parser(obj.getJSONArray(key))
        } catch (_: Exception) {
            emptyList<Any>()
        }
    }
}

package com.pira.ccloud.data.repository

import com.pira.ccloud.data.model.FilterType
import com.pira.ccloud.data.model.Genre
import com.pira.ccloud.data.model.Poster
import com.pira.ccloud.data.model.Source
import com.pira.ccloud.data.model.Country
import com.pira.ccloud.util.ApiException
import com.pira.ccloud.util.Result
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class CountryPostersRepository @Inject constructor(
    client: OkHttpClient,
    @Named("apiKey") apiKey: String,
    @Named("apiBaseUrl") apiBaseUrl: String,
    @Named("fallbackServer1") fallbackServer1: String,
    @Named("fallbackServer2") fallbackServer2: String
) : BaseRepository(client, apiKey, apiBaseUrl, fallbackServer1, fallbackServer2), ICountryPostersRepository {

    private val BASE_URL = "$API_BASE_URL/api/poster/by/filtres"

    override suspend fun getPostersByCountry(countryId: Int, page: Int, filterType: FilterType): Result<List<Poster>> {
        return try {
            val url = buildUrl(countryId, filterType, page)
            val jsonData = executeRequest(url) { Request.Builder().url(it).build() }
            Result.success(parsePosters(jsonData))
        } catch (e: Exception) {
            Result.error(ApiException.fromException(e))
        }
    }

    private fun buildUrl(countryId: Int, filterType: FilterType, page: Int): String {
        return when (filterType) {
            FilterType.DEFAULT -> "$BASE_URL/0/$countryId/created/$page/$API_KEY"
            FilterType.BY_YEAR -> "$BASE_URL/0/$countryId/year/$page/$API_KEY"
            FilterType.BY_IMDB -> "$BASE_URL/0/$countryId/imdb/$page/$API_KEY"
        }
    }

    private fun parsePosters(jsonData: String): List<Poster> {
        val posters = mutableListOf<Poster>()
        val jsonArray = JSONArray(jsonData)

        for (i in 0 until jsonArray.length()) {
            try {
                val posterObj = jsonArray.getJSONObject(i)
                posters.add(parsePoster(posterObj))
            } catch (e: Exception) {
                // Skip posters that fail to parse
                continue
            }
        }

        return posters
    }

    private fun parsePoster(posterObj: JSONObject): Poster {
        return Poster(
            id = posterObj.optInt("id", 0),
            title = posterObj.optString("title", "Unknown Title"),
            type = posterObj.optString("type", ""),
            description = posterObj.optString("description", "No description available"),
            year = posterObj.optInt("year", 0),
            imdb = posterObj.optDouble("imdb", 0.0),
            rating = posterObj.optDouble("rating", 0.0),
            duration = posterObj.optString("duration", null).takeIf { it != "null" && it != "N/A" },
            image = posterObj.optString("image", ""),
            cover = posterObj.optString("cover", ""),
            genres = try {
                val genresArray = posterObj.getJSONArray("genres")
                val genres = mutableListOf<Genre>()
                for (j in 0 until genresArray.length()) {
                    val genreObj = genresArray.getJSONObject(j)
                    genres.add(
                        Genre(
                            id = genreObj.optInt("id", 0),
                            title = genreObj.optString("title", "Unknown")
                        )
                    )
                }
                genres
            } catch (e: Exception) {
                emptyList()
            },
            sources = try {
                val sourcesArray = posterObj.getJSONArray("sources")
                val sources = mutableListOf<Source>()
                for (j in 0 until sourcesArray.length()) {
                    val sourceObj = sourcesArray.getJSONObject(j)
                    sources.add(
                        Source(
                            id = sourceObj.optInt("id", 0),
                            quality = sourceObj.optString("quality", "Unknown"),
                            type = sourceObj.optString("type", "Unknown"),
                            url = sourceObj.optString("url", "")
                        )
                    )
                }
                sources
            } catch (e: Exception) {
                emptyList()
            },
            country = try {
                val countriesArray = posterObj.getJSONArray("country")
                val countries = mutableListOf<Country>()
                for (j in 0 until countriesArray.length()) {
                    val countryObj = countriesArray.getJSONObject(j)
                    countries.add(
                        Country(
                            id = countryObj.optInt("id", 0),
                            title = countryObj.optString("title", "Unknown"),
                            image = countryObj.optString("image", "")
                        )
                    )
                }
                countries
            } catch (e: Exception) {
                emptyList()
            }
        )
    }
}

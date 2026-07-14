package com.pira.ccloud.data.repository

import com.pira.ccloud.data.model.Genre
import com.pira.ccloud.util.ApiException
import com.pira.ccloud.util.Result
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class GenreRepository @Inject constructor(
    client: OkHttpClient,
    @Named("apiKey") apiKey: String,
    @Named("apiBaseUrl") apiBaseUrl: String,
    @Named("fallbackServer1") fallbackServer1: String,
    @Named("fallbackServer2") fallbackServer2: String
) : BaseRepository(client, apiKey, apiBaseUrl, fallbackServer1, fallbackServer2), IGenreRepository {

    private val GENRE_URL = "$API_BASE_URL/api/genre/all"

    override suspend fun getGenres(): Result<List<Genre>> {
        return try {
            val url = "$GENRE_URL/$API_KEY"
            val jsonData = executeRequest(url) { Request.Builder().url(it).build() }
            Result.success(parseGenres(jsonData))
        } catch (e: Exception) {
            Result.error(ApiException.fromException(e))
        }
    }

    private fun parseGenres(jsonData: String): List<Genre> {
        val genres = mutableListOf<Genre>()
        val jsonArray = JSONArray(jsonData)

        for (i in 0 until jsonArray.length()) {
            try {
                val genreObj = jsonArray.getJSONObject(i)
                val genre = Genre(
                    id = genreObj.optInt("id", 0),
                    title = genreObj.optString("title", "Unknown")
                )
                genres.add(genre)
            } catch (e: Exception) {
                // Skip genres that fail to parse
                continue
            }
        }

        return genres.sortedBy { it.title }
    }
}

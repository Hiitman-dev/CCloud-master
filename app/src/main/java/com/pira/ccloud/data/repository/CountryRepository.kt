package com.pira.ccloud.data.repository

import com.pira.ccloud.data.model.Country
import com.pira.ccloud.util.ApiException
import com.pira.ccloud.util.Result
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class CountryRepository @Inject constructor(
    client: OkHttpClient,
    @Named("apiKey") apiKey: String,
    @Named("apiBaseUrl") apiBaseUrl: String,
    @Named("fallbackServer1") fallbackServer1: String,
    @Named("fallbackServer2") fallbackServer2: String
) : BaseRepository(client, apiKey, apiBaseUrl, fallbackServer1, fallbackServer2), ICountryRepository {

    private val BASE_URL = "$API_BASE_URL/api/country/all"

    override suspend fun getAllCountries(): Result<List<Country>> {
        return try {
            val url = "$BASE_URL/$API_KEY/"
            val jsonData = executeRequest(url) { Request.Builder().url(it).build() }
            Result.success(parseCountries(jsonData))
        } catch (e: Exception) {
            Result.error(ApiException.fromException(e))
        }
    }

    private fun parseCountries(jsonData: String): List<Country> {
        val jsonArray = JSONArray(jsonData)
        val countries = mutableListOf<Country>()

        for (i in 0 until jsonArray.length()) {
            try {
                val countryObj = jsonArray.getJSONObject(i)
                countries.add(
                    Country(
                        id = countryObj.optInt("id", 0),
                        title = countryObj.optString("title", "Unknown"),
                        image = countryObj.optString("image", "")
                    )
                )
            } catch (e: Exception) {
                // Skip countries that fail to parse
                continue
            }
        }

        return countries
    }
}

package com.pira.ccloud.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject
import javax.inject.Named

open class BaseRepository @Inject constructor(
    protected val client: OkHttpClient,
    @Named("apiKey") protected val API_KEY: String,
    @Named("apiBaseUrl") protected val API_BASE_URL: String,
    @Named("fallbackServer1") private val fallbackServer1: String,
    @Named("fallbackServer2") private val fallbackServer2: String
) {
    // Helper servers array (fallback when primary server fails)
    protected val helperServers = arrayOf(fallbackServer1, fallbackServer2)

    protected suspend fun executeRequest(
        primaryUrl: String,
        requestBuilder: (String) -> Request
    ): String {
        return withContext(Dispatchers.IO) {
            // First, try the primary server
            try {
                val primaryRequest = requestBuilder(primaryUrl)
                client.newCall(primaryRequest).execute().use { response ->
                    if (response.isSuccessful) {
                        return@withContext response.body?.string()
                            ?: throw Exception("Empty response body from primary server")
                    } else {
                        throw Exception("Primary server returned error: ${response.code}")
                    }
                }
            } catch (primaryException: Exception) {
                // If primary server fails, try helper servers
                for (helperServer in helperServers) {
                    if (helperServer.isEmpty()) continue
                    try {
                        // Replace the host in the URL with the helper server
                        val helperUrl = primaryUrl.replace(Regex("^https?://[^/]+"), helperServer)
                        val helperRequest = requestBuilder(helperUrl)
                        client.newCall(helperRequest).execute().use { response ->
                            if (response.isSuccessful) {
                                return@withContext response.body?.string()
                                    ?: throw Exception("Empty response body from helper server")
                            }
                        }
                    } catch (helperException: Exception) {
                        // Continue to next helper server
                        continue
                    }
                }

                // If all servers fail, throw the original exception
                throw primaryException
            }
        }
    }
}

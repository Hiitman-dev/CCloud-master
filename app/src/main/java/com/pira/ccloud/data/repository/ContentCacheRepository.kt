package com.pira.ccloud.data.repository

import android.content.Context
import android.util.Log
import com.pira.ccloud.data.model.Movie
import com.pira.ccloud.data.model.Series
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContentCacheRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : IContentCacheRepository {

    companion object {
        private const val TAG = "ContentCacheRepository"
    }

    // In-memory cache for recently accessed content
    private val movieCache = mutableMapOf<Int, Movie>()
    private val seriesCache = mutableMapOf<Int, Series>()

    // ── Movie Caching ───────────────────────────────────────

    override suspend fun saveMovie(movie: Movie): Unit = withContext(Dispatchers.IO) {
        try {
            val jsonString = Json.encodeToString(movie)
            File(context.filesDir, "movie_${movie.id}.json").writeText(jsonString)
            movieCache[movie.id] = movie
            Log.d(TAG, "Movie saved: ${movie.id} - ${movie.title}")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving movie", e)
        }
    }

    override suspend fun loadMovie(movieId: Int): Movie? {
        // Check in-memory cache first
        movieCache[movieId]?.let { return it }

        // Load from disk
        return try {
            val file = File(context.filesDir, "movie_$movieId.json")
            if (file.exists()) {
                Json.decodeFromString<Movie>(file.readText()).also { movieCache[movieId] = it }
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading movie", e)
            null
        }
    }

    override suspend fun clearAllMovies(): Unit = withContext(Dispatchers.IO) {
        try {
            val filesDir = context.filesDir
            val movieFiles = filesDir.listFiles { file ->
                file.name.startsWith("movie_") && file.name.endsWith(".json")
            }
            movieFiles?.forEach { file ->
                file.delete()
                Log.d(TAG, "Deleted movie file: ${file.name}")
            }
            movieCache.clear()
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing all movies", e)
        }
    }

    // ── Series Caching ──────────────────────────────────────

    override suspend fun saveSeries(series: Series): Unit = withContext(Dispatchers.IO) {
        try {
            val jsonString = Json.encodeToString(series)
            File(context.filesDir, "series_${series.id}.json").writeText(jsonString)
            seriesCache[series.id] = series
            Log.d(TAG, "Series saved: ${series.id} - ${series.title}")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving series", e)
        }
    }

    override suspend fun loadSeries(seriesId: Int): Series? {
        // Check in-memory cache first
        seriesCache[seriesId]?.let { return it }

        // Load from disk
        return try {
            val file = File(context.filesDir, "series_$seriesId.json")
            if (file.exists()) {
                Json.decodeFromString<Series>(file.readText()).also { seriesCache[seriesId] = it }
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading series", e)
            null
        }
    }

    override suspend fun clearAllSeries(): Unit = withContext(Dispatchers.IO) {
        try {
            val filesDir = context.filesDir
            val seriesFiles = filesDir.listFiles { file ->
                file.name.startsWith("series_") && file.name.endsWith(".json")
            }
            seriesFiles?.forEach { file ->
                file.delete()
                Log.d(TAG, "Deleted series file: ${file.name}")
            }
            seriesCache.clear()
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing all series", e)
        }
    }
}

package com.pira.ccloud.data.repository

import android.content.Context
import android.util.Log
import com.pira.ccloud.data.model.FavoriteItem
import com.pira.ccloud.data.model.WatchedEpisode
import com.pira.ccloud.utils.ViewHistoryManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : IHistoryRepository {

    companion object {
        private const val TAG = "HistoryRepository"
        private const val WATCHED_EPISODES_FILE = "watched_episodes.json"
        private const val RECENTLY_VIEWED_FILE = "recently_viewed.json"
        private const val MAX_RECENTLY_VIEWED = 20
    }

    // In-memory caches
    private var cachedWatchedEpisodes: List<WatchedEpisode>? = null
    private var cachedRecentlyViewed: List<FavoriteItem>? = null

    // ── View History (delegates to ViewHistoryManager) ───────

    override suspend fun addToContinueWatching(
        id: Int, type: String, title: String, image: String,
        genres: List<String>, episodeId: Int, seasonId: Int
    ) = withContext(Dispatchers.IO) {
        ViewHistoryManager.addToContinueWatching(context, id, type, title, image, genres, episodeId, seasonId)
    }

    override suspend fun markAsWatching(id: Int, type: String, episodeId: Int) = withContext(Dispatchers.IO) {
        ViewHistoryManager.markAsWatching(context, id, type, episodeId)
    }

    override suspend fun updateProgress(
        id: Int, type: String, watchDurationMs: Long,
        totalDurationMs: Long, lastPositionMs: Long, episodeId: Int
    ) = withContext(Dispatchers.IO) {
        ViewHistoryManager.updateProgress(context, id, type, watchDurationMs, totalDurationMs, lastPositionMs, episodeId)
    }

    override suspend fun markAsExternal(id: Int, type: String) = withContext(Dispatchers.IO) {
        ViewHistoryManager.markAsExternal(context, id, type)
    }

    override suspend fun markAsDownloaded(id: Int, type: String) = withContext(Dispatchers.IO) {
        ViewHistoryManager.markAsDownloaded(context, id, type)
    }

    override suspend fun markAsNotPlaying(id: Int, type: String, episodeId: Int) = withContext(Dispatchers.IO) {
        ViewHistoryManager.markAsNotPlaying(context, id, type, episodeId)
    }

    override suspend fun getLastPosition(id: Int, type: String, episodeId: Int): Long {
        return ViewHistoryManager.getLastPosition(context, id, type, episodeId)
    }

    override suspend fun getContinueWatching(limit: Int): List<ViewHistoryManager.ViewEntry> {
        return ViewHistoryManager.getContinueWatching(context, limit)
    }

    override suspend fun getRecentlyWatched(limit: Int): List<ViewHistoryManager.ViewEntry> {
        return ViewHistoryManager.getRecentlyWatched(context, limit)
    }

    override suspend fun getInProgress(): List<ViewHistoryManager.ViewEntry> {
        return ViewHistoryManager.getInProgress(context)
    }

    override suspend fun getFinished(): List<ViewHistoryManager.ViewEntry> {
        return ViewHistoryManager.getFinished(context)
    }

    override suspend fun getTopGenres(limit: Int): List<Pair<String, Int>> {
        return ViewHistoryManager.getTopGenres(context, limit)
    }

    override suspend fun getTotalWatchTime(): Long {
        return ViewHistoryManager.getTotalWatchTime(context)
    }

    override suspend fun getStats(): ViewHistoryManager.WatchStats {
        return ViewHistoryManager.getStats(context)
    }

    override suspend fun clearHistory() = withContext(Dispatchers.IO) {
        ViewHistoryManager.clearHistory(context)
    }

    // ── Watched Episodes ────────────────────────────────────

    override suspend fun saveWatchedEpisode(watchedEpisode: WatchedEpisode) = withContext(Dispatchers.IO) {
        try {
            val episodes = (cachedWatchedEpisodes ?: loadWatchedEpisodesFromDisk()).toMutableList()
            episodes.removeAll {
                it.seriesId == watchedEpisode.seriesId &&
                it.seasonId == watchedEpisode.seasonId &&
                it.episodeId == watchedEpisode.episodeId
            }
            episodes.add(watchedEpisode)
            saveWatchedEpisodesToDisk(episodes)
            cachedWatchedEpisodes = episodes
            Log.d(TAG, "Watched episode saved: Series ${watchedEpisode.seriesId}, Season ${watchedEpisode.seasonId}, Episode ${watchedEpisode.episodeId}")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving watched episode", e)
        }
    }

    override suspend fun removeWatchedEpisode(seriesId: Int, seasonId: Int, episodeId: Int) = withContext(Dispatchers.IO) {
        try {
            val episodes = (cachedWatchedEpisodes ?: loadWatchedEpisodesFromDisk()).toMutableList()
            episodes.removeAll { it.seriesId == seriesId && it.seasonId == seasonId && it.episodeId == episodeId }
            saveWatchedEpisodesToDisk(episodes)
            cachedWatchedEpisodes = episodes
            Log.d(TAG, "Watched episode removed: Series $seriesId, Season $seasonId, Episode $episodeId")
        } catch (e: Exception) {
            Log.e(TAG, "Error removing watched episode", e)
        }
    }

    override suspend fun isEpisodeWatched(seriesId: Int, seasonId: Int, episodeId: Int): Boolean {
        val episodes = loadAllWatchedEpisodes()
        return episodes.any { it.seriesId == seriesId && it.seasonId == seasonId && it.episodeId == episodeId }
    }

    override suspend fun loadAllWatchedEpisodes(): List<WatchedEpisode> {
        return cachedWatchedEpisodes ?: loadWatchedEpisodesFromDisk().also { cachedWatchedEpisodes = it }
    }

    override suspend fun clearAllWatchedEpisodes() = withContext(Dispatchers.IO) {
        try {
            val file = File(context.filesDir, WATCHED_EPISODES_FILE)
            if (file.exists()) file.delete()
            cachedWatchedEpisodes = emptyList()
            Log.d(TAG, "All watched episodes cleared")
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing all watched episodes", e)
        }
    }

    // ── Recently Viewed ─────────────────────────────────────

    override suspend fun saveRecentlyViewed(item: FavoriteItem) = withContext(Dispatchers.IO) {
        try {
            val items = (cachedRecentlyViewed ?: loadRecentlyViewedFromDisk()).toMutableList()
            items.removeAll { it.id == item.id && it.type == item.type }
            items.add(0, item)
            val capped = items.take(MAX_RECENTLY_VIEWED)
            saveRecentlyViewedToDisk(capped)
            cachedRecentlyViewed = capped
        } catch (e: Exception) {
            Log.e(TAG, "Error saving recently viewed item", e)
        }
    }

    override suspend fun loadRecentlyViewed(): List<FavoriteItem> {
        return cachedRecentlyViewed ?: loadRecentlyViewedFromDisk().also { cachedRecentlyViewed = it }
    }

    // ── Private disk I/O ────────────────────────────────────

    private fun loadWatchedEpisodesFromDisk(): List<WatchedEpisode> {
        return try {
            val file = File(context.filesDir, WATCHED_EPISODES_FILE)
            if (file.exists()) {
                Json.decodeFromString<List<WatchedEpisode>>(file.readText())
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading watched episodes from disk", e)
            emptyList()
        }
    }

    private fun saveWatchedEpisodesToDisk(episodes: List<WatchedEpisode>) {
        val jsonString = Json.encodeToString(episodes)
        File(context.filesDir, WATCHED_EPISODES_FILE).writeText(jsonString)
    }

    private fun loadRecentlyViewedFromDisk(): List<FavoriteItem> {
        return try {
            val file = File(context.filesDir, RECENTLY_VIEWED_FILE)
            if (file.exists()) {
                Json.decodeFromString<List<FavoriteItem>>(file.readText())
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading recently viewed from disk", e)
            emptyList()
        }
    }

    private fun saveRecentlyViewedToDisk(items: List<FavoriteItem>) {
        val jsonString = Json.encodeToString(items)
        File(context.filesDir, RECENTLY_VIEWED_FILE).writeText(jsonString)
    }
}

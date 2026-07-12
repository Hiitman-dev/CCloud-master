package com.pira.ccloud.utils

import android.content.Context
import android.util.Log
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

/**
 * Tracks viewing history with status, progress, and resume position.
 * Powers: Continue Watching, Downloads status, Watch Analytics, Resume playback.
 */
object ViewHistoryManager {
    private const val TAG = "ViewHistoryManager"
    private const val MAX_HISTORY = 100
    private const val FILE_NAME = "view_history.json"

    /**
     * Status of a content item.
     * - ADDED: User clicked on it (added to Continue Watching)
     * - WATCHING: User has watched at least 30 seconds
     * - WATCHED: User has finished watching (>90%)
     * - DOWNLOADED: User downloaded it but hasn't watched yet
     * - EXTERNAL: User played it in an external player
     */
    enum class ContentStatus {
        ADDED,       // Just added to list (clicked but not played)
        WATCHING,    // Watching now (played >30s but not finished)
        WATCHED,     // Finished (>90% progress)
        DOWNLOADED,  // Downloaded but not watched in-app
        EXTERNAL     // Played in external player
    }

    @Serializable
    data class ViewEntry(
        val id: Int,
        val type: String, // "movie" or "series"
        val title: String,
        val image: String,
        val genres: List<String>,
        val status: ContentStatus = ContentStatus.ADDED,
        val watchedAt: Long = System.currentTimeMillis(),
        val watchDurationMs: Long = 0,
        val totalDurationMs: Long = 0,
        val lastPositionMs: Long = 0,      // Resume position
        val episodeId: Int = -1,            // For series episodes
        val seasonId: Int = -1,             // For series seasons
        val isPlayingNow: Boolean = false   // Currently playing in-app
    ) {
        val progress: Float
            get() = if (totalDurationMs > 0) (watchDurationMs.toFloat() / totalDurationMs).coerceIn(0f, 1f) else 0f

        val isFinished: Boolean
            get() = status == ContentStatus.WATCHED || progress > 0.9f

        val statusLabel: String
            get() = when (status) {
                ContentStatus.ADDED -> "Added"
                ContentStatus.WATCHING -> "Watching Now"
                ContentStatus.WATCHED -> "Watched"
                ContentStatus.DOWNLOADED -> "Downloaded"
                ContentStatus.EXTERNAL -> "External Player"
            }
    }

    /**
     * Add content to Continue Watching when user clicks on it.
     * Even if they haven't played or downloaded it yet.
     */
    fun addToContinueWatching(
        context: Context,
        id: Int,
        type: String,
        title: String,
        image: String,
        genres: List<String>,
        episodeId: Int = -1,
        seasonId: Int = -1
    ) {
        try {
            val history = loadHistory(context).toMutableList()
            val existing = history.find {
                it.id == id && it.type == type &&
                (episodeId == -1 || it.episodeId == episodeId)
            }
            if (existing != null) {
                // Update watchedAt to move to front
                val index = history.indexOf(existing)
                val updated = existing.copy(watchedAt = System.currentTimeMillis())
                history.removeAt(index)
                history.add(0, updated)
            } else {
                // Add new entry
                val entry = ViewEntry(
                    id = id,
                    type = type,
                    title = title,
                    image = image,
                    genres = genres,
                    status = ContentStatus.ADDED,
                    episodeId = episodeId,
                    seasonId = seasonId
                )
                history.add(0, entry)
            }
            saveHistory(context, history.take(MAX_HISTORY))
        } catch (e: Exception) {
            Log.e(TAG, "Error adding to continue watching", e)
        }
    }

    /**
     * Mark content as actively watching (called when playback starts).
     */
    fun markAsWatching(
        context: Context,
        id: Int,
        type: String,
        episodeId: Int = -1
    ) {
        try {
            val history = loadHistory(context).toMutableList()
            val index = history.indexOfFirst {
                it.id == id && it.type == type &&
                (episodeId == -1 || it.episodeId == episodeId)
            }
            if (index >= 0) {
                val existing = history[index]
                if (existing.status != ContentStatus.WATCHED) {
                    history[index] = existing.copy(
                        status = ContentStatus.WATCHING,
                        isPlayingNow = true,
                        watchedAt = System.currentTimeMillis()
                    )
                    saveHistory(context, history)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error marking as watching", e)
        }
    }

    /**
     * Update watch progress and last position for resume.
     */
    fun updateProgress(
        context: Context,
        id: Int,
        type: String,
        watchDurationMs: Long,
        totalDurationMs: Long,
        lastPositionMs: Long = 0,
        episodeId: Int = -1
    ) {
        try {
            val history = loadHistory(context).toMutableList()
            val index = history.indexOfFirst {
                it.id == id && it.type == type &&
                (episodeId == -1 || it.episodeId == episodeId)
            }
            if (index >= 0) {
                val existing = history[index]
                val newDuration = maxOf(existing.watchDurationMs, watchDurationMs)
                val progress = if (totalDurationMs > 0) newDuration.toFloat() / totalDurationMs else 0f
                val newStatus = when {
                    progress > 0.9f -> ContentStatus.WATCHED
                    newDuration >= 30_000L -> ContentStatus.WATCHING // 30 seconds threshold
                    else -> existing.status
                }
                history[index] = existing.copy(
                    watchDurationMs = newDuration,
                    totalDurationMs = totalDurationMs,
                    lastPositionMs = lastPositionMs,
                    status = newStatus,
                    isPlayingNow = false,
                    watchedAt = System.currentTimeMillis()
                )
                // Move to front
                val entry = history.removeAt(index)
                history.add(0, entry)
                saveHistory(context, history)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating progress", e)
        }
    }

    /**
     * Mark as watched externally (played in external player).
     */
    fun markAsExternal(context: Context, id: Int, type: String) {
        try {
            val history = loadHistory(context).toMutableList()
            val index = history.indexOfFirst { it.id == id && it.type == type }
            if (index >= 0) {
                val existing = history[index]
                history[index] = existing.copy(
                    status = ContentStatus.EXTERNAL,
                    watchedAt = System.currentTimeMillis()
                )
                val entry = history.removeAt(index)
                history.add(0, entry)
                saveHistory(context, history)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error marking as external", e)
        }
    }

    /**
     * Mark as downloaded.
     */
    fun markAsDownloaded(context: Context, id: Int, type: String) {
        try {
            val history = loadHistory(context).toMutableList()
            val index = history.indexOfFirst { it.id == id && it.type == type }
            if (index >= 0) {
                val existing = history[index]
                if (existing.status == ContentStatus.ADDED) {
                    history[index] = existing.copy(status = ContentStatus.DOWNLOADED)
                    saveHistory(context, history)
                }
            } else {
                // Add as downloaded
                val entry = ViewEntry(
                    id = id,
                    type = type,
                    title = "",
                    image = "",
                    genres = emptyList(),
                    status = ContentStatus.DOWNLOADED
                )
                history.add(0, entry)
                saveHistory(context, history.take(MAX_HISTORY))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error marking as downloaded", e)
        }
    }

    /**
     * Mark as no longer playing (when leaving the player).
     */
    fun markAsNotPlaying(context: Context, id: Int, type: String, episodeId: Int = -1) {
        try {
            val history = loadHistory(context).toMutableList()
            val index = history.indexOfFirst {
                it.id == id && it.type == type &&
                (episodeId == -1 || it.episodeId == episodeId)
            }
            if (index >= 0) {
                val existing = history[index]
                history[index] = existing.copy(isPlayingNow = false)
                saveHistory(context, history)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error marking as not playing", e)
        }
    }

    /**
     * Get the last playback position for resume.
     */
    fun getLastPosition(context: Context, id: Int, type: String, episodeId: Int = -1): Long {
        val history = loadHistory(context)
        val entry = history.find {
            it.id == id && it.type == type &&
            (episodeId == -1 || it.episodeId == episodeId)
        }
        return entry?.lastPositionMs ?: 0L
    }

    /**
     * Get all entries for Continue Watching (ordered by most recent).
     */
    fun getContinueWatching(context: Context, limit: Int = 30): List<ViewEntry> {
        return loadHistory(context).take(limit)
    }

    fun getRecentlyWatched(context: Context, limit: Int = 20): List<ViewEntry> {
        return loadHistory(context).take(limit)
    }

    fun getInProgress(context: Context): List<ViewEntry> {
        return loadHistory(context).filter {
            !it.isFinished && it.progress > 0.05f && it.status != ContentStatus.ADDED
        }
    }

    fun getFinished(context: Context): List<ViewEntry> {
        return loadHistory(context).filter { it.isFinished }
    }

    fun getTopGenres(context: Context, limit: Int = 5): List<Pair<String, Int>> {
        val history = loadHistory(context)
        val genreCounts = mutableMapOf<String, Int>()
        history.forEach { entry ->
            entry.genres.forEach { genre ->
                genreCounts[genre] = (genreCounts[genre] ?: 0) + 1
            }
        }
        return genreCounts.entries
            .sortedByDescending { it.value }
            .take(limit)
            .map { it.key to it.value }
    }

    fun getTotalWatchTime(context: Context): Long {
        return loadHistory(context).sumOf { it.watchDurationMs }
    }

    fun getStats(context: Context): WatchStats {
        val history = loadHistory(context)
        val totalWatched = history.size
        val moviesWatched = history.count { it.type == "movie" }
        val seriesWatched = history.count { it.type == "series" }
        val totalTime = history.sumOf { it.watchDurationMs }
        val avgSessionMs = if (totalWatched > 0) totalTime / totalWatched else 0L
        val topGenres = getTopGenres(context, 3).map { it.first }
        val watchingNow = history.count { it.status == ContentStatus.WATCHING }
        val watched = history.count { it.status == ContentStatus.WATCHED }

        return WatchStats(
            totalContentWatched = totalWatched,
            moviesWatched = moviesWatched,
            seriesWatched = seriesWatched,
            totalWatchTimeMs = totalTime,
            avgSessionMs = avgSessionMs,
            topGenres = topGenres,
            watchingNow = watchingNow,
            watchedCount = watched
        )
    }

    fun clearHistory(context: Context) {
        try {
            File(context.filesDir, FILE_NAME).delete()
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing history", e)
        }
    }

    private fun loadHistory(context: Context): List<ViewEntry> {
        return try {
            val file = File(context.filesDir, FILE_NAME)
            if (file.exists()) {
                Json.decodeFromString<List<ViewEntry>>(file.readText())
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading history", e)
            emptyList()
        }
    }

    private fun saveHistory(context: Context, history: List<ViewEntry>) {
        try {
            val jsonString = Json.encodeToString(history)
            File(context.filesDir, FILE_NAME).writeText(jsonString)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving history", e)
        }
    }
}

@Serializable
data class WatchStats(
    val totalContentWatched: Int,
    val moviesWatched: Int,
    val seriesWatched: Int,
    val totalWatchTimeMs: Long,
    val avgSessionMs: Long,
    val topGenres: List<String>,
    val watchingNow: Int = 0,
    val watchedCount: Int = 0
) {
    val totalWatchTimeHours: Float
        get() = totalWatchTimeMs / (1000f * 60f * 60f)

    val formattedTotalTime: String
        get() {
            val hours = totalWatchTimeMs / (1000 * 60 * 60)
            val minutes = (totalWatchTimeMs % (1000 * 60 * 60)) / (1000 * 60)
            return "${hours}h ${minutes}m"
        }
}

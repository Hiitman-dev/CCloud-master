package com.pira.ccloud.data.repository

import com.pira.ccloud.data.model.FavoriteItem
import com.pira.ccloud.data.model.WatchedEpisode
import com.pira.ccloud.utils.ViewHistoryManager
import com.pira.ccloud.utils.WatchStats

interface IHistoryRepository {
    // View history (Continue Watching)
    suspend fun addToContinueWatching(
        id: Int,
        type: String,
        title: String,
        image: String,
        genres: List<String>,
        episodeId: Int = -1,
        seasonId: Int = -1
    )
    suspend fun markAsWatching(id: Int, type: String, episodeId: Int = -1)
    suspend fun updateProgress(
        id: Int,
        type: String,
        watchDurationMs: Long,
        totalDurationMs: Long,
        lastPositionMs: Long = 0,
        episodeId: Int = -1
    )
    suspend fun markAsExternal(id: Int, type: String)
    suspend fun markAsDownloaded(id: Int, type: String)
    suspend fun markAsNotPlaying(id: Int, type: String, episodeId: Int = -1)
    suspend fun getLastPosition(id: Int, type: String, episodeId: Int = -1): Long
    suspend fun getContinueWatching(limit: Int = 30): List<ViewHistoryManager.ViewEntry>
    suspend fun getRecentlyWatched(limit: Int = 20): List<ViewHistoryManager.ViewEntry>
    suspend fun getInProgress(): List<ViewHistoryManager.ViewEntry>
    suspend fun getFinished(): List<ViewHistoryManager.ViewEntry>
    suspend fun getTopGenres(limit: Int = 5): List<Pair<String, Int>>
    suspend fun getTotalWatchTime(): Long
    suspend fun getStats(): WatchStats
    suspend fun clearHistory()

    // Watched episodes (series tracking)
    suspend fun saveWatchedEpisode(watchedEpisode: WatchedEpisode)
    suspend fun removeWatchedEpisode(seriesId: Int, seasonId: Int, episodeId: Int)
    suspend fun isEpisodeWatched(seriesId: Int, seasonId: Int, episodeId: Int): Boolean
    suspend fun loadAllWatchedEpisodes(): List<WatchedEpisode>
    suspend fun clearAllWatchedEpisodes()

    // Recently viewed
    suspend fun saveRecentlyViewed(item: FavoriteItem)
    suspend fun loadRecentlyViewed(): List<FavoriteItem>
}

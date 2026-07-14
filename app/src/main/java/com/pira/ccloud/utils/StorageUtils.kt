package com.pira.ccloud.utils

import android.content.Context
import android.util.Log
import com.pira.ccloud.data.model.FavoriteGroup
import com.pira.ccloud.data.model.FavoriteItem
import com.pira.ccloud.data.model.Movie
import com.pira.ccloud.data.model.Series
import com.pira.ccloud.data.model.SubtitleSettings
import com.pira.ccloud.data.model.VideoPlayerSettings
import com.pira.ccloud.data.model.FontSettings
import com.pira.ccloud.data.model.WatchedEpisode
import com.pira.ccloud.data.repository.IContentCacheRepository
import com.pira.ccloud.data.repository.IFavoritesRepository
import com.pira.ccloud.data.repository.IHistoryRepository
import com.pira.ccloud.data.repository.ISettingsRepository
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.util.Date

/**
 * @deprecated Use the injected repositories instead:
 * - [IFavoritesRepository] for favorites and favorite groups
 * - [ISettingsRepository] for subtitle, video player, font settings, and welcome state
 * - [IHistoryRepository] for view history, watched episodes, and recently viewed
 * - [IContentCacheRepository] for movie and series caching
 *
 * This object is kept as a facade for backward compatibility. All methods now delegate
 * to the new repositories when available, falling back to direct file I/O when not.
 */
@Deprecated(
    message = "Use injected repositories instead of StorageUtils",
    replaceWith = ReplaceWith(
        "IFavoritesRepository",
        "com.pira.ccloud.data.repository.IFavoritesRepository"
    )
)
object StorageUtils {
    private const val TAG = "StorageUtils"

    // Repository references (set by Application after Hilt initialization)
    private var favoritesRepo: IFavoritesRepository? = null
    private var settingsRepo: ISettingsRepository? = null
    private var historyRepo: IHistoryRepository? = null
    private var contentCacheRepo: IContentCacheRepository? = null

    /**
     * Initialize with injected repositories. Called by CCloudApplication after Hilt setup.
     */
    fun initialize(
        favoritesRepository: IFavoritesRepository,
        settingsRepository: ISettingsRepository,
        historyRepository: IHistoryRepository,
        contentCacheRepository: IContentCacheRepository
    ) {
        favoritesRepo = favoritesRepository
        settingsRepo = settingsRepository
        historyRepo = historyRepository
        contentCacheRepo = contentCacheRepository
    }

    // ── Favorites ───────────────────────────────────────────

    fun saveFavorite(context: Context, favorite: FavoriteItem) {
        favoritesRepo?.let { repo ->
            kotlinx.coroutines.runBlocking { repo.saveFavorite(favorite) }
            return
        }
        // Fallback to direct file I/O
        saveFavoriteDirect(context, favorite)
    }

    fun removeFavorite(context: Context, id: Int, type: String) {
        favoritesRepo?.let { repo ->
            kotlinx.coroutines.runBlocking { repo.removeFavorite(id, type) }
            return
        }
        removeFavoriteDirect(context, id, type)
    }

    fun clearAllFavorites(context: Context) {
        favoritesRepo?.let { repo ->
            kotlinx.coroutines.runBlocking { repo.clearAllFavorites() }
            return
        }
        clearAllFavoritesDirect(context)
    }

    fun isFavorite(context: Context, id: Int, type: String): Boolean {
        favoritesRepo?.let { repo ->
            return kotlinx.coroutines.runBlocking { repo.isFavorite(id, type) }
        }
        return isFavoriteDirect(context, id, type)
    }

    fun loadAllFavorites(context: Context): List<FavoriteItem> {
        favoritesRepo?.let { repo ->
            return kotlinx.coroutines.runBlocking { repo.loadAllFavorites() }
        }
        return loadAllFavoritesDirect(context)
    }

    fun saveFavoriteToDatabase(context: Context, favorite: FavoriteItem) {
        favoritesRepo?.let { repo ->
            kotlinx.coroutines.runBlocking { repo.saveFavoriteToDatabase(favorite) }
            return
        }
        saveFavoriteToDatabaseDirect(context, favorite)
    }

    // ── Favorite Groups ─────────────────────────────────────

    fun saveFavoriteGroup(context: Context, group: FavoriteGroup) {
        favoritesRepo?.let { repo ->
            kotlinx.coroutines.runBlocking { repo.saveFavoriteGroup(group) }
            return
        }
        saveFavoriteGroupDirect(context, group)
    }

    fun removeFavoriteGroup(context: Context, groupId: String) {
        favoritesRepo?.let { repo ->
            kotlinx.coroutines.runBlocking { repo.removeFavoriteGroup(groupId) }
            return
        }
        removeFavoriteGroupDirect(context, groupId)
    }

    fun loadAllFavoriteGroups(context: Context): List<FavoriteGroup> {
        favoritesRepo?.let { repo ->
            return kotlinx.coroutines.runBlocking { repo.loadAllFavoriteGroups() }
        }
        return loadAllFavoriteGroupsDirect(context)
    }

    fun getDefaultGroup(context: Context): FavoriteGroup {
        favoritesRepo?.let { repo ->
            return kotlinx.coroutines.runBlocking { repo.getDefaultGroup() }
        }
        return getDefaultGroupDirect(context)
    }

    fun addFavoriteToGroup(context: Context, groupId: String, favoriteId: Int, type: String) {
        favoritesRepo?.let { repo ->
            kotlinx.coroutines.runBlocking { repo.addFavoriteToGroup(groupId, favoriteId, type) }
            return
        }
        addFavoriteToGroupDirect(context, groupId, favoriteId, type)
    }

    fun removeFavoriteFromGroup(context: Context, groupId: String, favoriteId: Int, type: String) {
        favoritesRepo?.let { repo ->
            kotlinx.coroutines.runBlocking { repo.removeFavoriteFromGroup(groupId, favoriteId, type) }
            return
        }
        removeFavoriteFromGroupDirect(context, groupId, favoriteId, type)
    }

    fun getGroupsForFavorite(context: Context, favoriteId: Int, type: String): List<FavoriteGroup> {
        favoritesRepo?.let { repo ->
            return kotlinx.coroutines.runBlocking { repo.getGroupsForFavorite(favoriteId, type) }
        }
        return getGroupsForFavoriteDirect(context, favoriteId, type)
    }

    fun isFavoriteInGroup(context: Context, groupId: String, favoriteId: Int, type: String): Boolean {
        favoritesRepo?.let { repo ->
            return kotlinx.coroutines.runBlocking { repo.isFavoriteInGroup(groupId, favoriteId, type) }
        }
        return isFavoriteInGroupDirect(context, groupId, favoriteId, type)
    }

    fun getFavoritesInGroup(context: Context, groupId: String): List<FavoriteItem> {
        favoritesRepo?.let { repo ->
            return kotlinx.coroutines.runBlocking { repo.getFavoritesInGroup(groupId) }
        }
        return getFavoritesInGroupDirect(context, groupId)
    }

    // ── Settings ────────────────────────────────────────────

    fun saveSubtitleSettings(context: Context, settings: SubtitleSettings) {
        settingsRepo?.let { repo ->
            kotlinx.coroutines.runBlocking { repo.saveSubtitleSettings(settings) }
            return
        }
        saveSubtitleSettingsDirect(context, settings)
    }

    fun loadSubtitleSettings(context: Context): SubtitleSettings {
        settingsRepo?.let { repo ->
            return kotlinx.coroutines.runBlocking { repo.loadSubtitleSettings() }
        }
        return loadSubtitleSettingsDirect(context)
    }

    fun saveVideoPlayerSettings(context: Context, settings: VideoPlayerSettings) {
        settingsRepo?.let { repo ->
            kotlinx.coroutines.runBlocking { repo.saveVideoPlayerSettings(settings) }
            return
        }
        saveVideoPlayerSettingsDirect(context, settings)
    }

    fun loadVideoPlayerSettings(context: Context): VideoPlayerSettings {
        settingsRepo?.let { repo ->
            return kotlinx.coroutines.runBlocking { repo.loadVideoPlayerSettings() }
        }
        return loadVideoPlayerSettingsDirect(context)
    }

    fun saveFontSettings(context: Context, settings: FontSettings) {
        settingsRepo?.let { repo ->
            kotlinx.coroutines.runBlocking { repo.saveFontSettings(settings) }
            return
        }
        saveFontSettingsDirect(context, settings)
    }

    fun loadFontSettings(context: Context): FontSettings {
        settingsRepo?.let { repo ->
            return kotlinx.coroutines.runBlocking { repo.loadFontSettings() }
        }
        return loadFontSettingsDirect(context)
    }

    fun saveWelcomeCompleted(context: Context) {
        settingsRepo?.let { repo ->
            kotlinx.coroutines.runBlocking { repo.saveWelcomeCompleted() }
            return
        }
        saveWelcomeCompletedDirect(context)
    }

    fun isWelcomeCompleted(context: Context): Boolean {
        settingsRepo?.let { repo ->
            return kotlinx.coroutines.runBlocking { repo.isWelcomeCompleted() }
        }
        return isWelcomeCompletedDirect(context)
    }

    // ── History ─────────────────────────────────────────────

    fun saveWatchedEpisode(context: Context, watchedEpisode: WatchedEpisode) {
        historyRepo?.let { repo ->
            kotlinx.coroutines.runBlocking { repo.saveWatchedEpisode(watchedEpisode) }
            return
        }
        saveWatchedEpisodeDirect(context, watchedEpisode)
    }

    fun removeWatchedEpisode(context: Context, seriesId: Int, seasonId: Int, episodeId: Int) {
        historyRepo?.let { repo ->
            kotlinx.coroutines.runBlocking { repo.removeWatchedEpisode(seriesId, seasonId, episodeId) }
            return
        }
        removeWatchedEpisodeDirect(context, seriesId, seasonId, episodeId)
    }

    fun isEpisodeWatched(context: Context, seriesId: Int, seasonId: Int, episodeId: Int): Boolean {
        historyRepo?.let { repo ->
            return kotlinx.coroutines.runBlocking { repo.isEpisodeWatched(seriesId, seasonId, episodeId) }
        }
        return isEpisodeWatchedDirect(context, seriesId, seasonId, episodeId)
    }

    fun loadAllWatchedEpisodes(context: Context): List<WatchedEpisode> {
        historyRepo?.let { repo ->
            return kotlinx.coroutines.runBlocking { repo.loadAllWatchedEpisodes() }
        }
        return loadAllWatchedEpisodesDirect(context)
    }

    fun clearAllWatchedEpisodes(context: Context) {
        historyRepo?.let { repo ->
            kotlinx.coroutines.runBlocking { repo.clearAllWatchedEpisodes() }
            return
        }
        clearAllWatchedEpisodesDirect(context)
    }

    fun saveRecentlyViewed(context: Context, item: FavoriteItem) {
        historyRepo?.let { repo ->
            kotlinx.coroutines.runBlocking { repo.saveRecentlyViewed(item) }
            return
        }
        saveRecentlyViewedDirect(context, item)
    }

    fun loadRecentlyViewed(context: Context): List<FavoriteItem> {
        historyRepo?.let { repo ->
            return kotlinx.coroutines.runBlocking { repo.loadRecentlyViewed() }
        }
        return loadRecentlyViewedDirect(context)
    }

    // ── Content Cache ───────────────────────────────────────

    fun saveMovieToFile(context: Context, movie: Movie) {
        contentCacheRepo?.let { repo ->
            kotlinx.coroutines.runBlocking { repo.saveMovie(movie) }
            return
        }
        saveMovieToFileDirect(context, movie)
    }

    fun loadMovieFromFile(context: Context, movieId: Int): Movie? {
        contentCacheRepo?.let { repo ->
            return kotlinx.coroutines.runBlocking { repo.loadMovie(movieId) }
        }
        return loadMovieFromFileDirect(context, movieId)
    }

    fun clearAllMovies(context: Context) {
        contentCacheRepo?.let { repo ->
            kotlinx.coroutines.runBlocking { repo.clearAllMovies() }
            return
        }
        clearAllMoviesDirect(context)
    }

    fun saveSeriesToFile(context: Context, series: Series) {
        contentCacheRepo?.let { repo ->
            kotlinx.coroutines.runBlocking { repo.saveSeries(series) }
            return
        }
        saveSeriesToFileDirect(context, series)
    }

    fun loadSeriesFromFile(context: Context, seriesId: Int): Series? {
        contentCacheRepo?.let { repo ->
            return kotlinx.coroutines.runBlocking { repo.loadSeries(seriesId) }
        }
        return loadSeriesFromFileDirect(context, seriesId)
    }

    fun clearAllSeries(context: Context) {
        contentCacheRepo?.let { repo ->
            kotlinx.coroutines.runBlocking { repo.clearAllSeries() }
            return
        }
        clearAllSeriesDirect(context)
    }

    // ═══════════════════════════════════════════════════════════
    // Direct file I/O fallback methods (original implementations)
    // ═══════════════════════════════════════════════════════════

    private fun saveFavoriteDirect(context: Context, favorite: FavoriteItem) {
        try {
            val favorites = loadAllFavoritesDirect(context).toMutableList()
            favorites.removeAll { it.id == favorite.id && it.type == favorite.type }
            favorites.add(0, favorite)
            val jsonString = Json.encodeToString(favorites)
            File(context.filesDir, "favorites.json").writeText(jsonString)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving favorite", e)
        }
    }

    private fun removeFavoriteDirect(context: Context, id: Int, type: String) {
        try {
            val favorites = loadAllFavoritesDirect(context).toMutableList()
            favorites.removeAll { it.id == id && it.type == type }
            val jsonString = Json.encodeToString(favorites)
            File(context.filesDir, "favorites.json").writeText(jsonString)
        } catch (e: Exception) {
            Log.e(TAG, "Error removing favorite", e)
        }
    }

    private fun clearAllFavoritesDirect(context: Context) {
        try {
            val file = File(context.filesDir, "favorites.json")
            if (file.exists()) file.delete()
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing all favorites", e)
        }
    }

    private fun isFavoriteDirect(context: Context, id: Int, type: String): Boolean {
        return try {
            loadAllFavoritesDirect(context).any { it.id == id && it.type == type }
        } catch (e: Exception) {
            false
        }
    }

    private fun loadAllFavoritesDirect(context: Context): List<FavoriteItem> {
        return try {
            val file = File(context.filesDir, "favorites.json")
            if (file.exists()) Json.decodeFromString<List<FavoriteItem>>(file.readText())
            else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun saveFavoriteToDatabaseDirect(context: Context, favorite: FavoriteItem) {
        try {
            when (favorite.type) {
                "movie" -> {
                    val movie = Movie(favorite.id, favorite.type, favorite.title, favorite.description,
                        favorite.year, favorite.imdb, favorite.rating, favorite.duration,
                        favorite.image, favorite.cover, favorite.genres, favorite.sources, favorite.country)
                    saveMovieToFileDirect(context, movie)
                }
                "series" -> {
                    val series = Series(favorite.id, favorite.type, favorite.title, favorite.description,
                        favorite.year, favorite.imdb, favorite.rating, favorite.duration,
                        favorite.image, favorite.cover, favorite.genres, favorite.country)
                    saveSeriesToFileDirect(context, series)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error saving favorite to database", e)
        }
    }

    private fun saveFavoriteGroupDirect(context: Context, group: FavoriteGroup) {
        try {
            val groups = loadAllFavoriteGroupsDirect(context).toMutableList()
            groups.removeAll { it.id == group.id }
            groups.add(group)
            val jsonString = Json.encodeToString(groups)
            File(context.filesDir, "favorite_groups.json").writeText(jsonString)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving favorite group", e)
        }
    }

    private fun removeFavoriteGroupDirect(context: Context, groupId: String) {
        try {
            val groups = loadAllFavoriteGroupsDirect(context).toMutableList()
            groups.removeAll { it.id == groupId && !it.isDefault }
            val jsonString = Json.encodeToString(groups)
            File(context.filesDir, "favorite_groups.json").writeText(jsonString)
        } catch (e: Exception) {
            Log.e(TAG, "Error removing favorite group", e)
        }
    }

    private fun loadAllFavoriteGroupsDirect(context: Context): List<FavoriteGroup> {
        return try {
            val file = File(context.filesDir, "favorite_groups.json")
            if (file.exists()) Json.decodeFromString<List<FavoriteGroup>>(file.readText())
            else listOf(FavoriteGroup(id = "default", name = "Favorites", isDefault = true))
        } catch (e: Exception) {
            listOf(FavoriteGroup(id = "default", name = "Favorites", isDefault = true))
        }
    }

    private fun getDefaultGroupDirect(context: Context): FavoriteGroup {
        val groups = loadAllFavoriteGroupsDirect(context)
        return groups.find { it.isDefault } ?: FavoriteGroup(id = "default", name = "Favorites", isDefault = true)
    }

    private fun addFavoriteToGroupDirect(context: Context, groupId: String, favoriteId: Int, type: String) {
        try {
            val groups = loadAllFavoriteGroupsDirect(context).toMutableList()
            val group = groups.find { it.id == groupId }
            if (group != null) {
                if (type == "movie") group.addMovie(favoriteId)
                else if (type == "series") group.addSeries(favoriteId)
                val jsonString = Json.encodeToString(groups)
                File(context.filesDir, "favorite_groups.json").writeText(jsonString)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error adding favorite to group", e)
        }
    }

    private fun removeFavoriteFromGroupDirect(context: Context, groupId: String, favoriteId: Int, type: String) {
        try {
            val groups = loadAllFavoriteGroupsDirect(context).toMutableList()
            val group = groups.find { it.id == groupId }
            if (group != null) {
                if (type == "movie") group.removeMovie(favoriteId)
                else if (type == "series") group.removeSeries(favoriteId)
                val jsonString = Json.encodeToString(groups)
                File(context.filesDir, "favorite_groups.json").writeText(jsonString)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error removing favorite from group", e)
        }
    }

    private fun getGroupsForFavoriteDirect(context: Context, favoriteId: Int, type: String): List<FavoriteGroup> {
        return try {
            loadAllFavoriteGroupsDirect(context).filter { group ->
                if (type == "movie") group.containsMovie(favoriteId)
                else if (type == "series") group.containsSeries(favoriteId)
                else false
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun isFavoriteInGroupDirect(context: Context, groupId: String, favoriteId: Int, type: String): Boolean {
        return try {
            val group = loadAllFavoriteGroupsDirect(context).find { it.id == groupId }
            when {
                group == null -> false
                type == "movie" -> group.containsMovie(favoriteId)
                type == "series" -> group.containsSeries(favoriteId)
                else -> false
            }
        } catch (e: Exception) {
            false
        }
    }

    private fun getFavoritesInGroupDirect(context: Context, groupId: String): List<FavoriteItem> {
        return try {
            val allFavorites = loadAllFavoritesDirect(context)
            val group = loadAllFavoriteGroupsDirect(context).find { it.id == groupId }
            if (group != null) {
                val movieFavorites = allFavorites.filter { it.type == "movie" && group.movieIds.contains(it.id) }
                val seriesFavorites = allFavorites.filter { it.type == "series" && group.seriesIds.contains(it.id) }
                (movieFavorites + seriesFavorites).sortedByDescending { it.id }
            } else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun saveSubtitleSettingsDirect(context: Context, settings: SubtitleSettings) {
        try {
            val jsonString = Json.encodeToString(settings)
            File(context.filesDir, "subtitle_settings.json").writeText(jsonString)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving subtitle settings", e)
        }
    }

    private fun loadSubtitleSettingsDirect(context: Context): SubtitleSettings {
        return try {
            val file = File(context.filesDir, "subtitle_settings.json")
            if (file.exists()) Json.decodeFromString<SubtitleSettings>(file.readText())
            else SubtitleSettings.getDefaultSettings(context)
        } catch (e: Exception) {
            SubtitleSettings.getDefaultSettings(context)
        }
    }

    private fun saveVideoPlayerSettingsDirect(context: Context, settings: VideoPlayerSettings) {
        try {
            val jsonString = Json.encodeToString(settings)
            File(context.filesDir, "video_player_settings.json").writeText(jsonString)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving video player settings", e)
        }
    }

    private fun loadVideoPlayerSettingsDirect(context: Context): VideoPlayerSettings {
        return try {
            val file = File(context.filesDir, "video_player_settings.json")
            if (file.exists()) Json.decodeFromString<VideoPlayerSettings>(file.readText())
            else VideoPlayerSettings.DEFAULT
        } catch (e: Exception) {
            VideoPlayerSettings.DEFAULT
        }
    }

    private fun saveFontSettingsDirect(context: Context, settings: FontSettings) {
        try {
            val jsonString = Json.encodeToString(settings)
            File(context.filesDir, "font_settings.json").writeText(jsonString)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving font settings", e)
        }
    }

    private fun loadFontSettingsDirect(context: Context): FontSettings {
        return try {
            val file = File(context.filesDir, "font_settings.json")
            if (file.exists()) Json.decodeFromString<FontSettings>(file.readText())
            else FontSettings.DEFAULT
        } catch (e: Exception) {
            FontSettings.DEFAULT
        }
    }

    private fun saveWelcomeCompletedDirect(context: Context) {
        try {
            File(context.filesDir, "welcome_completed.flag").writeText("completed")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving welcome completed state", e)
        }
    }

    private fun isWelcomeCompletedDirect(context: Context): Boolean {
        return try {
            File(context.filesDir, "welcome_completed.flag").exists()
        } catch (e: Exception) {
            false
        }
    }

    private fun saveWatchedEpisodeDirect(context: Context, watchedEpisode: WatchedEpisode) {
        try {
            val episodes = loadAllWatchedEpisodesDirect(context).toMutableList()
            episodes.removeAll {
                it.seriesId == watchedEpisode.seriesId &&
                it.seasonId == watchedEpisode.seasonId &&
                it.episodeId == watchedEpisode.episodeId
            }
            episodes.add(watchedEpisode)
            val jsonString = Json.encodeToString(episodes)
            File(context.filesDir, "watched_episodes.json").writeText(jsonString)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving watched episode", e)
        }
    }

    private fun removeWatchedEpisodeDirect(context: Context, seriesId: Int, seasonId: Int, episodeId: Int) {
        try {
            val episodes = loadAllWatchedEpisodesDirect(context).toMutableList()
            episodes.removeAll { it.seriesId == seriesId && it.seasonId == seasonId && it.episodeId == episodeId }
            val jsonString = Json.encodeToString(episodes)
            File(context.filesDir, "watched_episodes.json").writeText(jsonString)
        } catch (e: Exception) {
            Log.e(TAG, "Error removing watched episode", e)
        }
    }

    private fun isEpisodeWatchedDirect(context: Context, seriesId: Int, seasonId: Int, episodeId: Int): Boolean {
        return try {
            loadAllWatchedEpisodesDirect(context).any {
                it.seriesId == seriesId && it.seasonId == seasonId && it.episodeId == episodeId
            }
        } catch (e: Exception) {
            false
        }
    }

    private fun loadAllWatchedEpisodesDirect(context: Context): List<WatchedEpisode> {
        return try {
            val file = File(context.filesDir, "watched_episodes.json")
            if (file.exists()) Json.decodeFromString<List<WatchedEpisode>>(file.readText())
            else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun clearAllWatchedEpisodesDirect(context: Context) {
        try {
            val file = File(context.filesDir, "watched_episodes.json")
            if (file.exists()) file.delete()
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing all watched episodes", e)
        }
    }

    private const val MAX_RECENTLY_VIEWED = 20

    private fun saveRecentlyViewedDirect(context: Context, item: FavoriteItem) {
        try {
            val items = loadRecentlyViewedDirect(context).toMutableList()
            items.removeAll { it.id == item.id && it.type == item.type }
            items.add(0, item)
            val capped = items.take(MAX_RECENTLY_VIEWED)
            val jsonString = Json.encodeToString(capped)
            File(context.filesDir, "recently_viewed.json").writeText(jsonString)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving recently viewed item", e)
        }
    }

    private fun loadRecentlyViewedDirect(context: Context): List<FavoriteItem> {
        return try {
            val file = File(context.filesDir, "recently_viewed.json")
            if (file.exists()) Json.decodeFromString<List<FavoriteItem>>(file.readText())
            else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun saveMovieToFileDirect(context: Context, movie: Movie) {
        try {
            val jsonString = Json.encodeToString(movie)
            File(context.filesDir, "movie_${movie.id}.json").writeText(jsonString)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving movie to file", e)
        }
    }

    private fun loadMovieFromFileDirect(context: Context, movieId: Int): Movie? {
        return try {
            val file = File(context.filesDir, "movie_$movieId.json")
            if (file.exists()) Json.decodeFromString<Movie>(file.readText())
            else null
        } catch (e: Exception) {
            null
        }
    }

    private fun clearAllMoviesDirect(context: Context) {
        try {
            context.filesDir.listFiles { file ->
                file.name.startsWith("movie_") && file.name.endsWith(".json")
            }?.forEach { it.delete() }
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing all movies", e)
        }
    }

    private fun saveSeriesToFileDirect(context: Context, series: Series) {
        try {
            val jsonString = Json.encodeToString(series)
            File(context.filesDir, "series_${series.id}.json").writeText(jsonString)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving series to file", e)
        }
    }

    private fun loadSeriesFromFileDirect(context: Context, seriesId: Int): Series? {
        return try {
            val file = File(context.filesDir, "series_$seriesId.json")
            if (file.exists()) Json.decodeFromString<Series>(file.readText())
            else null
        } catch (e: Exception) {
            null
        }
    }

    private fun clearAllSeriesDirect(context: Context) {
        try {
            context.filesDir.listFiles { file ->
                file.name.startsWith("series_") && file.name.endsWith(".json")
            }?.forEach { it.delete() }
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing all series", e)
        }
    }
}

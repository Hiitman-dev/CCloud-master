package com.pira.ccloud.data.repository

import android.content.Context
import android.util.Log
import com.pira.ccloud.data.model.FavoriteGroup
import com.pira.ccloud.data.model.FavoriteItem
import com.pira.ccloud.data.model.Movie
import com.pira.ccloud.data.model.Series
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoritesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : IFavoritesRepository {

    companion object {
        private const val TAG = "FavoritesRepository"
        private const val FAVORITES_FILE = "favorites.json"
        private const val GROUPS_FILE = "favorite_groups.json"
    }

    // In-memory cache
    private var cachedFavorites: List<FavoriteItem>? = null
    private var cachedGroups: List<FavoriteGroup>? = null
    private val favoritesMutex = Mutex()
    private val groupsMutex = Mutex()

    // ── Favorite Items ──────────────────────────────────────

    override suspend fun saveFavorite(favorite: FavoriteItem): Unit = withContext(Dispatchers.IO) {
        favoritesMutex.withLock {
            try {
                val favorites = (cachedFavorites ?: loadFavoritesFromDisk()).toMutableList()
                favorites.removeAll { it.id == favorite.id && it.type == favorite.type }
                favorites.add(0, favorite)
                saveFavoritesToDisk(favorites)
                cachedFavorites = favorites
                Log.d(TAG, "Favorite saved: ${favorite.title}")
            } catch (e: Exception) {
                Log.e(TAG, "Error saving favorite", e)
            }
        }
    }

    override suspend fun removeFavorite(id: Int, type: String): Unit = withContext(Dispatchers.IO) {
        favoritesMutex.withLock {
            try {
                val favorites = (cachedFavorites ?: loadFavoritesFromDisk()).toMutableList()
                favorites.removeAll { it.id == id && it.type == type }
                saveFavoritesToDisk(favorites)
                cachedFavorites = favorites
                Log.d(TAG, "Favorite removed: $id ($type)")
            } catch (e: Exception) {
                Log.e(TAG, "Error removing favorite", e)
            }
        }
    }

    override suspend fun clearAllFavorites(): Unit = withContext(Dispatchers.IO) {
        favoritesMutex.withLock {
            try {
                val file = File(context.filesDir, FAVORITES_FILE)
                if (file.exists()) file.delete()
                cachedFavorites = emptyList()
                Log.d(TAG, "All favorites cleared")
            } catch (e: Exception) {
                Log.e(TAG, "Error clearing all favorites", e)
            }
        }
    }

    override suspend fun isFavorite(id: Int, type: String): Boolean {
        val favorites = loadAllFavorites()
        return favorites.any { it.id == id && it.type == type }
    }

    override suspend fun loadAllFavorites(): List<FavoriteItem> {
        return favoritesMutex.withLock {
            cachedFavorites ?: loadFavoritesFromDisk().also { cachedFavorites = it }
        }
    }

    override suspend fun saveFavoriteToDatabase(favorite: FavoriteItem): Unit = withContext(Dispatchers.IO) {
        try {
            when (favorite.type) {
                "movie" -> {
                    val movie = Movie(
                        id = favorite.id, type = favorite.type, title = favorite.title,
                        description = favorite.description, year = favorite.year,
                        imdb = favorite.imdb, rating = favorite.rating, duration = favorite.duration,
                        image = favorite.image, cover = favorite.cover, genres = favorite.genres,
                        sources = favorite.sources, country = favorite.country
                    )
                    val jsonString = Json.encodeToString(movie)
                    File(context.filesDir, "movie_${movie.id}.json").writeText(jsonString)
                }
                "series" -> {
                    val series = Series(
                        id = favorite.id, type = favorite.type, title = favorite.title,
                        description = favorite.description, year = favorite.year,
                        imdb = favorite.imdb, rating = favorite.rating, duration = favorite.duration,
                        image = favorite.image, cover = favorite.cover, genres = favorite.genres,
                        country = favorite.country
                    )
                    val jsonString = Json.encodeToString(series)
                    File(context.filesDir, "series_${series.id}.json").writeText(jsonString)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error saving favorite to database", e)
        }
    }

    // ── Favorite Groups ─────────────────────────────────────

    override suspend fun saveFavoriteGroup(group: FavoriteGroup): Unit = withContext(Dispatchers.IO) {
        groupsMutex.withLock {
            try {
                val groups = (cachedGroups ?: loadGroupsFromDisk()).toMutableList()
                groups.removeAll { it.id == group.id }
                groups.add(group)
                saveGroupsToDisk(groups)
                cachedGroups = groups
                Log.d(TAG, "Favorite group saved: ${group.name}")
            } catch (e: Exception) {
                Log.e(TAG, "Error saving favorite group", e)
            }
        }
    }

    override suspend fun removeFavoriteGroup(groupId: String): Unit = withContext(Dispatchers.IO) {
        groupsMutex.withLock {
            try {
                val groups = (cachedGroups ?: loadGroupsFromDisk()).toMutableList()
                groups.removeAll { it.id == groupId && !it.isDefault }
                saveGroupsToDisk(groups)
                cachedGroups = groups
                Log.d(TAG, "Favorite group removed: $groupId")
            } catch (e: Exception) {
                Log.e(TAG, "Error removing favorite group", e)
            }
        }
    }

    override suspend fun loadAllFavoriteGroups(): List<FavoriteGroup> {
        return groupsMutex.withLock {
            cachedGroups ?: loadGroupsFromDisk().also { cachedGroups = it }
        }
    }

    override suspend fun getDefaultGroup(): FavoriteGroup {
        val groups = loadAllFavoriteGroups()
        return groups.find { it.isDefault }
            ?: FavoriteGroup(id = "default", name = " Favorites", isDefault = true)
    }

    override suspend fun addFavoriteToGroup(groupId: String, favoriteId: Int, type: String): Unit =
        withContext(Dispatchers.IO) {
            groupsMutex.withLock {
                try {
                    val groups = (cachedGroups ?: loadGroupsFromDisk()).toMutableList()
                    val group = groups.find { it.id == groupId }
                    if (group != null) {
                        if (type == "movie") group.addMovie(favoriteId)
                        else if (type == "series") group.addSeries(favoriteId)
                        saveGroupsToDisk(groups)
                        cachedGroups = groups
                        Log.d(TAG, "Favorite added to group: $groupId")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error adding favorite to group", e)
                }
            }
        }

    override suspend fun removeFavoriteFromGroup(groupId: String, favoriteId: Int, type: String): Unit =
        withContext(Dispatchers.IO) {
            groupsMutex.withLock {
                try {
                    val groups = (cachedGroups ?: loadGroupsFromDisk()).toMutableList()
                    val group = groups.find { it.id == groupId }
                    if (group != null) {
                        if (type == "movie") group.removeMovie(favoriteId)
                        else if (type == "series") group.removeSeries(favoriteId)
                        saveGroupsToDisk(groups)
                        cachedGroups = groups
                        Log.d(TAG, "Favorite removed from group: $groupId")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error removing favorite from group", e)
                }
            }
        }

    override suspend fun getGroupsForFavorite(favoriteId: Int, type: String): List<FavoriteGroup> {
        return try {
            val groups = loadAllFavoriteGroups()
            groups.filter { group ->
                if (type == "movie") group.containsMovie(favoriteId)
                else if (type == "series") group.containsSeries(favoriteId)
                else false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting groups for favorite", e)
            emptyList()
        }
    }

    override suspend fun isFavoriteInGroup(groupId: String, favoriteId: Int, type: String): Boolean {
        return try {
            val groups = loadAllFavoriteGroups()
            val group = groups.find { it.id == groupId }
            when {
                group == null -> false
                type == "movie" -> group.containsMovie(favoriteId)
                type == "series" -> group.containsSeries(favoriteId)
                else -> false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking if favorite is in group", e)
            false
        }
    }

    override suspend fun getFavoritesInGroup(groupId: String): List<FavoriteItem> {
        return try {
            val allFavorites = loadAllFavorites()
            val groups = loadAllFavoriteGroups()
            val group = groups.find { it.id == groupId }

            if (group != null) {
                val movieFavorites = allFavorites.filter { it.type == "movie" && group.movieIds.contains(it.id) }
                val seriesFavorites = allFavorites.filter { it.type == "series" && group.seriesIds.contains(it.id) }
                (movieFavorites + seriesFavorites).sortedByDescending { it.id }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting favorites in group", e)
            emptyList()
        }
    }

    // ── Private disk I/O ────────────────────────────────────

    private fun loadFavoritesFromDisk(): List<FavoriteItem> {
        return try {
            val file = File(context.filesDir, FAVORITES_FILE)
            if (file.exists()) {
                Json.decodeFromString<List<FavoriteItem>>(file.readText())
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading favorites from disk", e)
            emptyList()
        }
    }

    private fun saveFavoritesToDisk(favorites: List<FavoriteItem>) {
        val jsonString = Json.encodeToString(favorites)
        File(context.filesDir, FAVORITES_FILE).writeText(jsonString)
    }

    private fun loadGroupsFromDisk(): List<FavoriteGroup> {
        return try {
            val file = File(context.filesDir, GROUPS_FILE)
            if (file.exists()) {
                Json.decodeFromString<List<FavoriteGroup>>(file.readText())
            } else {
                listOf(FavoriteGroup(id = "default", name = "Favorites", isDefault = true))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading groups from disk", e)
            listOf(FavoriteGroup(id = "default", name = "Favorites", isDefault = true))
        }
    }

    private fun saveGroupsToDisk(groups: List<FavoriteGroup>) {
        val jsonString = Json.encodeToString(groups)
        File(context.filesDir, GROUPS_FILE).writeText(jsonString)
    }
}

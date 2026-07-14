package com.pira.ccloud.data.repository

import com.pira.ccloud.data.model.FavoriteGroup
import com.pira.ccloud.data.model.FavoriteItem

interface IFavoritesRepository {
    // Favorite items
    suspend fun saveFavorite(favorite: FavoriteItem)
    suspend fun removeFavorite(id: Int, type: String)
    suspend fun clearAllFavorites()
    suspend fun isFavorite(id: Int, type: String): Boolean
    suspend fun loadAllFavorites(): List<FavoriteItem>
    suspend fun saveFavoriteToDatabase(favorite: FavoriteItem)

    // Favorite groups
    suspend fun saveFavoriteGroup(group: FavoriteGroup)
    suspend fun removeFavoriteGroup(groupId: String)
    suspend fun loadAllFavoriteGroups(): List<FavoriteGroup>
    suspend fun getDefaultGroup(): FavoriteGroup
    suspend fun addFavoriteToGroup(groupId: String, favoriteId: Int, type: String)
    suspend fun removeFavoriteFromGroup(groupId: String, favoriteId: Int, type: String)
    suspend fun getGroupsForFavorite(favoriteId: Int, type: String): List<FavoriteGroup>
    suspend fun isFavoriteInGroup(groupId: String, favoriteId: Int, type: String): Boolean
    suspend fun getFavoritesInGroup(groupId: String): List<FavoriteItem>
}

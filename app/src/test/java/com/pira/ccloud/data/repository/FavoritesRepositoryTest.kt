package com.pira.ccloud.data.repository

import com.pira.ccloud.data.model.FavoriteItem
import com.pira.ccloud.data.model.Genre
import com.pira.ccloud.data.model.Country
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class FavoritesRepositoryTest {

    private lateinit var repository: FavoritesRepository

    @Before
    fun setup() {
        // Note: This test requires a context. In a real project, you'd use
        // Robolectric or a test-specific context. For now, we'll test the
        // in-memory operations only.
        // repository = FavoritesRepository(testContext)
    }

    @Test
    fun `FavoriteItem creation with required fields`() {
        val favorite = FavoriteItem(
            id = 1,
            type = "movie",
            title = "Test Movie",
            description = "A test movie",
            year = 2024,
            imdb = 8.5,
            rating = 8.5,
            duration = "120 min",
            image = "https://example.com/image.jpg",
            cover = "https://example.com/cover.jpg",
            genres = listOf(Genre(1, "Action")),
            country = listOf(Country(1, "USA", "https://example.com/flag.png"))
        )

        assertEquals(1, favorite.id)
        assertEquals("movie", favorite.type)
        assertEquals("Test Movie", favorite.title)
        assertEquals(2024, favorite.year)
        assertEquals(8.5, favorite.imdb, 0.01)
    }

    @Test
    fun `FavoriteItem with empty sources`() {
        val favorite = FavoriteItem(
            id = 2,
            type = "series",
            title = "Test Series",
            description = "A test series",
            year = 2024,
            imdb = 9.0,
            rating = 9.0,
            duration = null,
            image = "https://example.com/image.jpg",
            cover = "https://example.com/cover.jpg",
            genres = emptyList(),
            country = emptyList(),
            sources = emptyList()
        )

        assertTrue(favorite.sources.isEmpty())
    }

    @Test
    fun `FavoriteGroup creation`() {
        val group = com.pira.ccloud.data.model.FavoriteGroup(
            id = "test-id",
            name = "My Favorites",
            isDefault = false
        )

        assertEquals("test-id", group.id)
        assertEquals("My Favorites", group.name)
        assertFalse(group.isDefault)
    }

    @Test
    fun `FavoriteGroup default group`() {
        val group = com.pira.ccloud.data.model.FavoriteGroup(
            id = "default",
            name = "Favorites",
            isDefault = true
        )

        assertTrue(group.isDefault)
    }

    @Test
    fun `FavoriteGroup containsMovie`() {
        val group = com.pira.ccloud.data.model.FavoriteGroup(
            id = "test-id",
            name = "My Favorites",
            isDefault = false,
            movieIds = mutableListOf(1, 2, 3)
        )

        assertTrue(group.containsMovie(1))
        assertTrue(group.containsMovie(2))
        assertTrue(group.containsMovie(3))
        assertFalse(group.containsMovie(4))
    }

    @Test
    fun `FavoriteGroup addMovie`() {
        val group = com.pira.ccloud.data.model.FavoriteGroup(
            id = "test-id",
            name = "My Favorites",
            isDefault = false
        )

        group.addMovie(1)
        group.addMovie(2)

        assertTrue(group.containsMovie(1))
        assertTrue(group.containsMovie(2))
        assertEquals(2, group.movieIds.size)
    }

    @Test
    fun `FavoriteGroup removeMovie`() {
        val group = com.pira.ccloud.data.model.FavoriteGroup(
            id = "test-id",
            name = "My Favorites",
            isDefault = false,
            movieIds = mutableListOf(1, 2, 3)
        )

        group.removeMovie(2)

        assertTrue(group.containsMovie(1))
        assertFalse(group.containsMovie(2))
        assertTrue(group.containsMovie(3))
        assertEquals(2, group.movieIds.size)
    }
}

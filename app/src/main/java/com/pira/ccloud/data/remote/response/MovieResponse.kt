package com.pira.ccloud.data.remote.response

import com.pira.ccloud.data.model.Country
import com.pira.ccloud.data.model.Genre
import com.pira.ccloud.data.model.Movie
import com.pira.ccloud.data.model.Source
import kotlinx.serialization.Serializable

/**
 * Typed response for movie API endpoints.
 * Used as a transitional step before full Retrofit migration.
 */
@Serializable
data class MovieResponse(
    val id: Int = 0,
    val type: String = "",
    val title: String = "",
    val description: String = "",
    val year: Int = 0,
    val imdb: Double = 0.0,
    val rating: Double = 0.0,
    val duration: String? = null,
    val image: String = "",
    val cover: String = "",
    val genres: List<Genre> = emptyList(),
    val sources: List<Source> = emptyList(),
    val country: List<Country> = emptyList()
) {
    fun toMovie(): Movie = Movie(
        id = id,
        type = type,
        title = title.ifEmpty { "Unknown Title" },
        description = description.ifEmpty { "No description available" },
        year = year,
        imdb = imdb,
        rating = rating,
        duration = duration?.takeIf { it != "null" && it != "N/A" },
        image = image,
        cover = cover,
        genres = genres,
        sources = sources,
        country = country
    )
}

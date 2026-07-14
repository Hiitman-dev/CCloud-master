package com.pira.ccloud.data.remote.response

import com.pira.ccloud.data.model.Country
import com.pira.ccloud.data.model.Genre
import com.pira.ccloud.data.model.Poster
import com.pira.ccloud.data.model.Source
import kotlinx.serialization.Serializable

@Serializable
data class SearchResponse(
    val posters: List<PosterResponse> = emptyList()
) {
    fun toSearchResult() = com.pira.ccloud.data.model.SearchResult(
        posters = posters.map { it.toPoster() }
    )
}

@Serializable
data class PosterResponse(
    val id: Int = 0,
    val title: String = "",
    val type: String = "",
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
    fun toPoster(): Poster = Poster(
        id = id,
        title = title.ifEmpty { "Unknown Title" },
        type = type,
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

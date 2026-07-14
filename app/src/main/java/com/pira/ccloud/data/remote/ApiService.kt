package com.pira.ccloud.data.remote

import com.pira.ccloud.data.remote.response.MovieResponse
import com.pira.ccloud.data.remote.response.PosterResponse
import com.pira.ccloud.data.remote.response.SearchResponse
import com.pira.ccloud.data.remote.response.SeriesResponse
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Retrofit API service interface.
 * Prepared for Phase 4 data layer migration.
 * Currently not used by repositories (which still use OkHttp directly).
 * Will be activated when repositories are migrated to Retrofit.
 */
interface ApiService {

    @GET("api/movie/by/filtres/{genreId}/{filterType}/{page}/{apiKey}")
    suspend fun getMovies(
        @Path("genreId") genreId: Int,
        @Path("filterType") filterType: String,
        @Path("page") page: Int,
        @Path("apiKey") apiKey: String
    ): List<MovieResponse>

    @GET("api/serie/by/filtres/{genreId}/{filterType}/{page}/{apiKey}")
    suspend fun getSeries(
        @Path("genreId") genreId: Int,
        @Path("filterType") filterType: String,
        @Path("page") page: Int,
        @Path("apiKey") apiKey: String
    ): List<SeriesResponse>

    @GET("api/search/{query}/{apiKey}")
    suspend fun search(
        @Path("query") query: String,
        @Path("apiKey") apiKey: String
    ): SearchResponse

    @GET("api/genre/all/{apiKey}")
    suspend fun getGenres(
        @Path("apiKey") apiKey: String
    ): List<GenreResponse>

    @GET("api/country/all/{apiKey}")
    suspend fun getCountries(
        @Path("apiKey") apiKey: String
    ): List<CountryResponse>

    @GET("api/poster/by/filtres/0/{countryId}/{filterType}/{page}/{apiKey}")
    suspend fun getPostersByCountry(
        @Path("countryId") countryId: Int,
        @Path("filterType") filterType: String,
        @Path("page") page: Int,
        @Path("apiKey") apiKey: String
    ): List<PosterResponse>

    @GET("api/season/by/serie/{seriesId}/{apiKey}")
    suspend fun getSeasons(
        @Path("seriesId") seriesId: Int,
        @Path("apiKey") apiKey: String
    ): List<SeasonResponse>
}

@Serializable
data class GenreResponse(
    val id: Int = 0,
    val title: String = ""
)

@Serializable
data class CountryResponse(
    val id: Int = 0,
    val title: String = "",
    val image: String = ""
)

@Serializable
data class SeasonResponse(
    val id: Int = 0,
    val title: String = "",
    val episodes: List<EpisodeResponse> = emptyList()
)

@Serializable
data class EpisodeResponse(
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val duration: String? = null,
    val image: String = "",
    val sources: List<com.pira.ccloud.data.model.Source> = emptyList()
)

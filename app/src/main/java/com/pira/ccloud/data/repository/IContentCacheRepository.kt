package com.pira.ccloud.data.repository

import com.pira.ccloud.data.model.Movie
import com.pira.ccloud.data.model.Series

interface IContentCacheRepository {
    // Movie caching
    suspend fun saveMovie(movie: Movie)
    suspend fun loadMovie(movieId: Int): Movie?
    suspend fun clearAllMovies()

    // Series caching
    suspend fun saveSeries(series: Series)
    suspend fun loadSeries(seriesId: Int): Series?
    suspend fun clearAllSeries()
}

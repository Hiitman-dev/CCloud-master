package com.pira.ccloud.data.repository

import com.pira.ccloud.data.model.FilterType
import com.pira.ccloud.data.model.Movie
import com.pira.ccloud.util.Result

interface IMovieRepository {
    suspend fun getMovies(page: Int = 0, genreId: Int = 0, filterType: FilterType = FilterType.DEFAULT): Result<List<Movie>>
}

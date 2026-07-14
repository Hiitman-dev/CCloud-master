package com.pira.ccloud.data.repository

import com.pira.ccloud.data.model.Genre
import com.pira.ccloud.util.Result

interface IGenreRepository {
    suspend fun getGenres(): Result<List<Genre>>
}

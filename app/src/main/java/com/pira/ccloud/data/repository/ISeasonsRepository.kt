package com.pira.ccloud.data.repository

import com.pira.ccloud.data.model.Season
import com.pira.ccloud.util.Result

interface ISeasonsRepository {
    suspend fun getSeasons(seriesId: Int): Result<List<Season>>
}

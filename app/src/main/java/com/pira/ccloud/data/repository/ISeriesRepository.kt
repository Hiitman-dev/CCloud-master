package com.pira.ccloud.data.repository

import com.pira.ccloud.data.model.FilterType
import com.pira.ccloud.data.model.Series
import com.pira.ccloud.util.Result

interface ISeriesRepository {
    suspend fun getSeries(page: Int = 0, genreId: Int = 0, filterType: FilterType = FilterType.DEFAULT): Result<List<Series>>
}

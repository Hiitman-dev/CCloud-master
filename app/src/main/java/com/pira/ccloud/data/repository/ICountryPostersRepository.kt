package com.pira.ccloud.data.repository

import com.pira.ccloud.data.model.FilterType
import com.pira.ccloud.data.model.Poster
import com.pira.ccloud.util.Result

interface ICountryPostersRepository {
    suspend fun getPostersByCountry(countryId: Int, page: Int = 0, filterType: FilterType = FilterType.DEFAULT): Result<List<Poster>>
}

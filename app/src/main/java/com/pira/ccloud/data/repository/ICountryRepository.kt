package com.pira.ccloud.data.repository

import com.pira.ccloud.data.model.Country
import com.pira.ccloud.util.Result

interface ICountryRepository {
    suspend fun getAllCountries(): Result<List<Country>>
}

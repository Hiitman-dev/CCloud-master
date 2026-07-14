package com.pira.ccloud.data.repository

import com.pira.ccloud.data.model.SearchResult
import com.pira.ccloud.util.Result

interface ISearchRepository {
    suspend fun search(query: String): Result<SearchResult>
}

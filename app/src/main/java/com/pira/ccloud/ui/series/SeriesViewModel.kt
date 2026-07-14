package com.pira.ccloud.ui.series

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pira.ccloud.data.model.FilterType
import com.pira.ccloud.data.model.Genre
import com.pira.ccloud.data.model.Series
import com.pira.ccloud.data.repository.IGenreRepository
import com.pira.ccloud.data.repository.ISeriesRepository
import com.pira.ccloud.utils.LanguageUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SeriesViewModel @Inject constructor(
    private val repository: ISeriesRepository,
    private val genreRepository: IGenreRepository
) : ViewModel() {

    var series by mutableStateOf<List<Series>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var isLoadingMore by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var currentPage by mutableStateOf(0)
        private set

    var canLoadMore by mutableStateOf(true)
        private set

    var genres by mutableStateOf<List<Genre>>(emptyList())
        private set

    var selectedGenreId by mutableStateOf(0)
        private set

    var selectedFilterType by mutableStateOf(FilterType.DEFAULT)
        private set

    init {
        loadGenres()
        loadSeries()
    }

    fun loadGenres() {
        viewModelScope.launch {
            genreRepository.getGenres().let { result ->
                if (result.isSuccess) {
                    genres = result.getOrNull() ?: emptyList()
                } else {
                    errorMessage = result.exceptionOrNull()?.message
                }
            }
        }
    }

    fun selectGenre(genreId: Int) {
        selectedGenreId = genreId
        refresh()
    }

    fun selectFilterType(filterType: FilterType) {
        selectedFilterType = filterType
        refresh()
    }

    fun loadSeries(page: Int = 0) {
        viewModelScope.launch {
            if (page == 0) isLoading = true else isLoadingMore = true
            errorMessage = null

            repository.getSeries(page, selectedGenreId, selectedFilterType).let { result ->
                if (result.isSuccess) {
                    val newSeries = result.getOrNull() ?: emptyList()
                    val filteredSeries = newSeries.filter { LanguageUtils.shouldDisplayTitle(it.title) }
                    canLoadMore = filteredSeries.isNotEmpty()
                    if (page == 0) {
                        series = filteredSeries
                    } else {
                        series = series + filteredSeries
                    }
                    currentPage = page
                } else {
                    errorMessage = result.exceptionOrNull()?.message
                }
            }

            isLoading = false
            isLoadingMore = false
        }
    }

    fun loadMoreSeries() {
        if (!isLoading && !isLoadingMore && canLoadMore) {
            loadSeries(currentPage + 1)
        }
    }

    fun retry() {
        loadSeries(currentPage)
    }

    fun refresh() {
        loadSeries(0)
    }
}

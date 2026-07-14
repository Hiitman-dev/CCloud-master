package com.pira.ccloud.ui.country

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pira.ccloud.data.model.FilterType
import com.pira.ccloud.data.model.Poster
import com.pira.ccloud.data.repository.ICountryPostersRepository
import com.pira.ccloud.data.repository.ICountryRepository
import com.pira.ccloud.utils.LanguageUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CountryViewModel @Inject constructor(
    private val postersRepository: ICountryPostersRepository,
    private val countryRepository: ICountryRepository
) : ViewModel() {

    var posters by mutableStateOf<List<Poster>>(emptyList())
        private set

    var countryName by mutableStateOf<String>("")
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

    var countryId by mutableStateOf<Int?>(null)
        private set

    var selectedFilterType by mutableStateOf(FilterType.DEFAULT)
        private set

    fun setCountryId(id: Int) {
        if (countryId != id) {
            countryId = id
            loadCountryName()
            refresh()
        }
    }

    fun selectFilterType(filterType: FilterType) {
        if (selectedFilterType != filterType) {
            selectedFilterType = filterType
            refresh()
        }
    }

    private fun loadCountryName() {
        viewModelScope.launch {
            countryRepository.getAllCountries().let { result ->
                if (result.isSuccess) {
                    val countries = result.getOrNull() ?: emptyList()
                    val country = countries.find { it.id == countryId }
                    countryName = country?.title ?: "Country"
                } else {
                    countryName = "Country"
                }
            }
        }
    }

    fun loadPosters(page: Int = 0) {
        val currentCountryId = countryId ?: return

        viewModelScope.launch {
            if (page == 0) isLoading = true else isLoadingMore = true
            errorMessage = null

            postersRepository.getPostersByCountry(currentCountryId, page, selectedFilterType).let { result ->
                if (result.isSuccess) {
                    val newPosters = result.getOrNull() ?: emptyList()
                    val filteredPosters = newPosters.filter { LanguageUtils.shouldDisplayTitle(it.title) }
                    canLoadMore = filteredPosters.isNotEmpty()
                    if (page == 0) {
                        posters = filteredPosters
                    } else {
                        posters = posters + filteredPosters
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

    fun loadMorePosters() {
        if (!isLoading && !isLoadingMore && canLoadMore) {
            loadPosters(currentPage + 1)
        }
    }

    fun retry() {
        loadPosters(currentPage)
    }

    fun refresh() {
        loadPosters(0)
    }
}

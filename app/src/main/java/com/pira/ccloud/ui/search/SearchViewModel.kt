package com.pira.ccloud.ui.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pira.ccloud.data.model.Country
import com.pira.ccloud.data.model.Poster
import com.pira.ccloud.data.repository.ICountryRepository
import com.pira.ccloud.data.repository.ISearchRepository
import com.pira.ccloud.utils.LanguageUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: ISearchRepository,
    private val countryRepository: ICountryRepository
) : ViewModel() {

    var searchResults by mutableStateOf<List<Poster>>(emptyList())
        private set

    var countries by mutableStateOf<List<Country>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var isCountriesLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var searchQuery by mutableStateOf("")
        private set

    // New state to track if a search has been performed
    var hasSearched by mutableStateOf(false)

    // Advanced search filters
    var selectedTypeFilter by mutableStateOf("All")
    var minRatingFilter by mutableStateOf(0f)

    private var searchJob: Job? = null

    init {
        loadCountries()
    }

    fun updateSearchQuery(query: String) {
        searchQuery = query
        // Don't auto-search anymore - only search when explicitly triggered
        // Reset search state when query changes
        if (!hasSearched) {
            searchResults = emptyList()
        }
    }

    // New function to explicitly trigger search
    fun triggerSearch() {
        if (searchQuery.isNotEmpty()) {
            hasSearched = true
            search(searchQuery)
        }
    }

    private fun search(query: String) {
        // Cancel any existing search job (debounce)
        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            // Debounce: wait 300ms before executing search
            kotlinx.coroutines.delay(300)

            isLoading = true
            errorMessage = null

            repository.search(query).let { result ->
                if (result.isSuccess) {
                    val searchResult = result.getOrNull()
                    searchResults = searchResult?.posters?.filter { poster ->
                        LanguageUtils.shouldDisplayTitle(poster.title)
                    } ?: emptyList()
                } else {
                    errorMessage = result.exceptionOrNull()?.message
                    searchResults = emptyList()
                }
            }

            isLoading = false
        }
    }

    private fun loadCountries() {
        viewModelScope.launch {
            isCountriesLoading = true
            countryRepository.getAllCountries().let { result ->
                countries = if (result.isSuccess) {
                    result.getOrNull() ?: emptyList()
                } else {
                    emptyList()
                }
            }
            isCountriesLoading = false
        }
    }

    fun clearSearch() {
        searchQuery = ""
        searchResults = emptyList()
        errorMessage = null
        hasSearched = false
    }
}

package com.pira.ccloud.ui.movies

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pira.ccloud.data.model.FilterType
import com.pira.ccloud.data.model.Genre
import com.pira.ccloud.data.model.Movie
import com.pira.ccloud.data.repository.IGenreRepository
import com.pira.ccloud.data.repository.IMovieRepository
import com.pira.ccloud.utils.LanguageUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val repository: IMovieRepository,
    private val genreRepository: IGenreRepository
) : ViewModel() {

    var movies by mutableStateOf<List<Movie>>(emptyList())
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
        loadMovies()
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

    fun loadMovies(page: Int = 0) {
        viewModelScope.launch {
            if (page == 0) isLoading = true else isLoadingMore = true
            errorMessage = null

            repository.getMovies(page, selectedGenreId, selectedFilterType).let { result ->
                if (result.isSuccess) {
                    val newMovies = result.getOrNull() ?: emptyList()
                    val filteredMovies = newMovies.filter { LanguageUtils.shouldDisplayTitle(it.title) }
                    canLoadMore = filteredMovies.isNotEmpty()
                    if (page == 0) {
                        movies = filteredMovies
                    } else {
                        movies = movies + filteredMovies
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

    fun loadMoreMovies() {
        if (!isLoading && !isLoadingMore && canLoadMore) {
            loadMovies(currentPage + 1)
        }
    }

    fun retry() {
        loadMovies(currentPage)
    }

    fun refresh() {
        loadMovies(0)
    }
}

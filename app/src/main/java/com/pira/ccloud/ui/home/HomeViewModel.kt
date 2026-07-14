package com.pira.ccloud.ui.home

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pira.ccloud.data.model.FavoriteItem
import com.pira.ccloud.data.model.FilterType
import com.pira.ccloud.data.model.Genre
import com.pira.ccloud.data.model.Movie
import com.pira.ccloud.data.model.Series
import com.pira.ccloud.data.repository.IGenreRepository
import com.pira.ccloud.data.repository.IMovieRepository
import com.pira.ccloud.data.repository.ISeriesRepository
import com.pira.ccloud.utils.LanguageUtils
import com.pira.ccloud.utils.StorageUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val movieRepository: IMovieRepository,
    private val seriesRepository: ISeriesRepository,
    private val genreRepository: IGenreRepository
) : ViewModel() {

    // Continue Watching
    var recentlyViewed by mutableStateOf<List<FavoriteItem>>(emptyList())
        private set

    // Today's Updates
    var todayMovies by mutableStateOf<List<Movie>>(emptyList())
        private set
    var todaySeries by mutableStateOf<List<Series>>(emptyList())
        private set

    // New Cinema Releases
    var newReleases by mutableStateOf<List<Movie>>(emptyList())
        private set

    // Movies (by year)
    var movies by mutableStateOf<List<Movie>>(emptyList())
        private set
    var genres by mutableStateOf<List<Genre>>(emptyList())
        private set
    var selectedGenreId by mutableStateOf(0)
        private set
    var selectedFilterType by mutableStateOf(FilterType.BY_YEAR)
        private set

    // Series
    var series by mutableStateOf<List<Series>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set
    var isLoadingMore by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    // Pagination for movies
    var currentMoviePage by mutableStateOf(0)
        private set
    var canLoadMoreMovies by mutableStateOf(true)
        private set

    // Pagination for series
    var currentSeriesPage by mutableStateOf(0)
        private set
    var canLoadMoreSeries by mutableStateOf(true)
        private set

    init {
        loadGenres()
        loadAllSections()
    }

    private fun loadGenres() {
        viewModelScope.launch {
            genreRepository.getGenres().let { result ->
                if (result.isSuccess) {
                    genres = result.getOrNull() ?: emptyList()
                }
                // Non-critical - silently ignore genre load failures
            }
        }
    }

    fun loadAllSections() {
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            try {
                loadRecentlyViewed()
                loadTodayUpdates()
                loadNewReleases()
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    private fun loadRecentlyViewed() {
        recentlyViewed = StorageUtils.loadRecentlyViewed(context)
    }

    private suspend fun loadTodayUpdates() {
        movieRepository.getMovies(0, 0, FilterType.DEFAULT).let { result ->
            todayMovies = if (result.isSuccess) {
                (result.getOrNull() ?: emptyList()).filter { LanguageUtils.shouldDisplayTitle(it.title) }.take(20)
            } else emptyList()
        }
        seriesRepository.getSeries(0, 0, FilterType.DEFAULT).let { result ->
            todaySeries = if (result.isSuccess) {
                (result.getOrNull() ?: emptyList()).filter { LanguageUtils.shouldDisplayTitle(it.title) }.take(20)
            } else emptyList()
        }
    }

    private suspend fun loadNewReleases() {
        movieRepository.getMovies(0, 0, FilterType.BY_YEAR).let { result ->
            newReleases = if (result.isSuccess) {
                (result.getOrNull() ?: emptyList()).filter { LanguageUtils.shouldDisplayTitle(it.title) }.take(20)
            } else emptyList()
        }
    }

    fun loadMovies(page: Int = 0) {
        viewModelScope.launch {
            if (page == 0) isLoading = true else isLoadingMore = true
            errorMessage = null

            movieRepository.getMovies(page, selectedGenreId, selectedFilterType).let { result ->
                if (result.isSuccess) {
                    val newMovies = result.getOrNull() ?: emptyList()
                    val filtered = newMovies.filter { LanguageUtils.shouldDisplayTitle(it.title) }
                    canLoadMoreMovies = filtered.isNotEmpty()
                    if (page == 0) {
                        movies = filtered
                    } else {
                        movies = movies + filtered
                    }
                    currentMoviePage = page
                } else {
                    errorMessage = result.exceptionOrNull()?.message
                }
            }

            isLoading = false
            isLoadingMore = false
        }
    }

    fun loadMoreMovies() {
        val shouldLoad = !isLoading && !isLoadingMore && canLoadMoreMovies
        if (shouldLoad) {
            loadMovies(currentMoviePage + 1)
        }
    }

    fun loadSeries(page: Int = 0) {
        viewModelScope.launch {
            if (page == 0) isLoading = true else isLoadingMore = true
            errorMessage = null

            seriesRepository.getSeries(page, selectedGenreId, selectedFilterType).let { result ->
                if (result.isSuccess) {
                    val newSeries = result.getOrNull() ?: emptyList()
                    val filtered = newSeries.filter { LanguageUtils.shouldDisplayTitle(it.title) }
                    canLoadMoreSeries = filtered.isNotEmpty()
                    if (page == 0) {
                        series = filtered
                    } else {
                        series = series + filtered
                    }
                    currentSeriesPage = page
                } else {
                    errorMessage = result.exceptionOrNull()?.message
                }
            }

            isLoading = false
            isLoadingMore = false
        }
    }

    fun loadMoreSeries() {
        val shouldLoad = !isLoading && !isLoadingMore && canLoadMoreSeries
        if (shouldLoad) {
            loadSeries(currentSeriesPage + 1)
        }
    }

    fun selectGenre(genreId: Int) {
        selectedGenreId = genreId
        refreshMovies()
    }

    fun selectFilterType(filterType: FilterType) {
        selectedFilterType = filterType
        refreshMovies()
    }

    fun refreshMovies() {
        currentMoviePage = 0
        canLoadMoreMovies = true
        loadMovies(0)
    }

    fun refreshSeries() {
        currentSeriesPage = 0
        canLoadMoreSeries = true
        loadSeries(0)
    }

    fun refresh() {
        loadAllSections()
    }
}

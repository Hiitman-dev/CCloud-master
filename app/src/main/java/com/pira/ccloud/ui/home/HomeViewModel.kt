package com.pira.ccloud.ui.home

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pira.ccloud.data.model.FavoriteItem
import com.pira.ccloud.data.model.FilterType
import com.pira.ccloud.data.model.Genre
import com.pira.ccloud.data.model.Movie
import com.pira.ccloud.data.model.Series
import com.pira.ccloud.data.repository.GenreRepository
import com.pira.ccloud.data.repository.MovieRepository
import com.pira.ccloud.data.repository.SeriesRepository
import com.pira.ccloud.utils.LanguageUtils
import com.pira.ccloud.utils.StorageUtils
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val movieRepository = MovieRepository()
    private val seriesRepository = SeriesRepository()
    private val genreRepository = GenreRepository()

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
            try {
                genres = genreRepository.getGenres()
            } catch (_: Exception) {
                // Non-critical
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
        val context = getApplication<Application>()
        recentlyViewed = StorageUtils.loadRecentlyViewed(context)
    }

    private suspend fun loadTodayUpdates() {
        try {
            val fetchedMovies = movieRepository.getMovies(0, 0, FilterType.DEFAULT)
            todayMovies = fetchedMovies.filter { LanguageUtils.shouldDisplayTitle(it.title) }.take(20)
        } catch (_: Exception) {
            todayMovies = emptyList()
        }
        try {
            val fetchedSeries = seriesRepository.getSeries(0, 0, FilterType.DEFAULT)
            todaySeries = fetchedSeries.filter { LanguageUtils.shouldDisplayTitle(it.title) }.take(20)
        } catch (_: Exception) {
            todaySeries = emptyList()
        }
    }

    private suspend fun loadNewReleases() {
        try {
            val fetchedMovies = movieRepository.getMovies(0, 0, FilterType.BY_YEAR)
            newReleases = fetchedMovies.filter { LanguageUtils.shouldDisplayTitle(it.title) }.take(20)
        } catch (_: Exception) {
            newReleases = emptyList()
        }
    }

    fun loadMovies(page: Int = 0) {
        viewModelScope.launch {
            try {
                if (page == 0) isLoading = true else isLoadingMore = true
                errorMessage = null

                val newMovies = movieRepository.getMovies(page, selectedGenreId, selectedFilterType)
                val filtered = newMovies.filter { LanguageUtils.shouldDisplayTitle(it.title) }
                canLoadMoreMovies = filtered.isNotEmpty()

                if (page == 0) {
                    movies = filtered
                } else {
                    val currentMovies = movies
                    movies = currentMovies + filtered
                }
                currentMoviePage = page
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
                isLoadingMore = false
            }
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
            try {
                if (page == 0) isLoading = true else isLoadingMore = true
                errorMessage = null

                val newSeries = seriesRepository.getSeries(page, selectedGenreId, selectedFilterType)
                val filtered = newSeries.filter { LanguageUtils.shouldDisplayTitle(it.title) }
                canLoadMoreSeries = filtered.isNotEmpty()

                if (page == 0) {
                    series = filtered
                } else {
                    val currentSeries = series
                    series = currentSeries + filtered
                }
                currentSeriesPage = page
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
                isLoadingMore = false
            }
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

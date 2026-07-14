package com.pira.ccloud.ui.movies

import com.pira.ccloud.data.model.FilterType
import com.pira.ccloud.data.model.Genre
import com.pira.ccloud.data.model.Movie
import com.pira.ccloud.data.repository.IGenreRepository
import com.pira.ccloud.data.repository.IMovieRepository
import com.pira.ccloud.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MoviesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: MoviesViewModel
    private lateinit var movieRepository: FakeMovieRepository
    private lateinit var genreRepository: FakeGenreRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        movieRepository = FakeMovieRepository()
        genreRepository = FakeGenreRepository()
        viewModel = MoviesViewModel(movieRepository, genreRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is empty`() = runTest {
        assertEquals(emptyList<Movie>(), viewModel.movies)
        assertFalse(viewModel.isLoading)
        assertNull(viewModel.errorMessage)
    }

    @Test
    fun `loadMovies updates movies list on success`() = runTest {
        val testMovies = listOf(
            Movie(1, "movie", "Test Movie 1", "Description", 2024, 8.0, 8.0, null, "", "", emptyList(), emptyList(), emptyList()),
            Movie(2, "movie", "Test Movie 2", "Description", 2024, 7.5, 7.5, null, "", "", emptyList(), emptyList(), emptyList())
        )
        movieRepository.setMoviesResult(Result.success(testMovies))

        viewModel.loadMovies()
        advanceUntilIdle()

        assertEquals(2, viewModel.movies.size)
        assertEquals("Test Movie 1", viewModel.movies[0].title)
        assertNull(viewModel.errorMessage)
    }

    @Test
    fun `loadMovies sets error message on failure`() = runTest {
        movieRepository.setMoviesResult(Result.error(com.pira.ccloud.util.ApiException.NetworkError(null)))

        viewModel.loadMovies()
        advanceUntilIdle()

        assertTrue(viewModel.errorMessage != null)
        assertTrue(viewModel.movies.isEmpty())
    }

    @Test
    fun `loadMoreMovies appends to existing list`() = runTest {
        val page1Movies = listOf(
            Movie(1, "movie", "Movie 1", "Description", 2024, 8.0, 8.0, null, "", "", emptyList(), emptyList(), emptyList())
        )
        val page2Movies = listOf(
            Movie(2, "movie", "Movie 2", "Description", 2024, 7.5, 7.5, null, "", "", emptyList(), emptyList(), emptyList())
        )
        movieRepository.setMoviesResult(Result.success(page1Movies))

        viewModel.loadMovies(0)
        advanceUntilIdle()

        assertEquals(1, viewModel.movies.size)

        movieRepository.setMoviesResult(Result.success(page2Movies))
        viewModel.loadMoreMovies()
        advanceUntilIdle()

        assertEquals(2, viewModel.movies.size)
    }

    @Test
    fun `selectGenre triggers refresh`() = runTest {
        val testMovies = listOf(
            Movie(1, "movie", "Action Movie", "Description", 2024, 8.0, 8.0, null, "", "", emptyList(), emptyList(), emptyList())
        )
        movieRepository.setMoviesResult(Result.success(testMovies))

        viewModel.selectGenre(1)
        advanceUntilIdle()

        assertEquals(1, viewModel.selectedGenreId)
        assertEquals(1, viewModel.movies.size)
    }

    @Test
    fun `refresh resets page and reloads`() = runTest {
        val testMovies = listOf(
            Movie(1, "movie", "Movie 1", "Description", 2024, 8.0, 8.0, null, "", "", emptyList(), emptyList(), emptyList())
        )
        movieRepository.setMoviesResult(Result.success(testMovies))

        viewModel.loadMovies(2)
        advanceUntilIdle()

        assertEquals(2, viewModel.currentPage)

        viewModel.refresh()
        advanceUntilIdle()

        assertEquals(0, viewModel.currentPage)
    }
}

// Fake implementations for testing
class FakeMovieRepository : IMovieRepository {
    private var moviesResult: Result<List<Movie>> = Result.success(emptyList())

    fun setMoviesResult(result: Result<List<Movie>>) {
        moviesResult = result
    }

    override suspend fun getMovies(page: Int, genreId: Int, filterType: FilterType): Result<List<Movie>> {
        return moviesResult
    }
}

class FakeGenreRepository : IGenreRepository {
    private var genresResult: Result<List<Genre>> = Result.success(emptyList())

    fun setGenresResult(result: Result<List<Genre>>) {
        genresResult = result
    }

    override suspend fun getGenres(): Result<List<Genre>> {
        return genresResult
    }
}

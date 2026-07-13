package com.pira.ccloud.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pira.ccloud.components.GenreFilterSection
import com.pira.ccloud.components.PosterCard
import com.pira.ccloud.data.model.Genre
import com.pira.ccloud.data.model.Movie
import com.pira.ccloud.ui.movies.MoviesViewModel
import com.pira.ccloud.utils.DeviceUtils
import com.pira.ccloud.utils.StorageUtils

/**
 * Small top breathing room above the filter row. The floating "Movies"
 * title chip that used to live here has been removed, so this is just a
 * bit of spacing under the status bar rather than a full title-bar
 * clearance - the system top inset itself is already consumed upstream by
 * MainScreen's Scaffold.
 */
private val TOP_BAR_CLEARANCE = 8.dp

/**
 * Extra bottom space added to the grid's content padding so the last row of
 * posters can scroll up past the floating glass bottom nav bar instead of
 * ending up permanently stuck underneath it (the bar overlays content
 * directly now, with no solid backing panel of its own). Matches the
 * clearance already used on the Home screen for the same reason.
 */
private val BOTTOM_BAR_CLEARANCE = 100.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviesScreen(
    viewModel: MoviesViewModel = viewModel(),
    navController: NavController? = null
) {
    val movies: List<Movie> = viewModel.movies
    val genres: List<Genre> = viewModel.genres
    val isLoading: Boolean = viewModel.isLoading
    val isLoadingMore: Boolean = viewModel.isLoadingMore
    val errorMessage: String? = viewModel.errorMessage
    val context = LocalContext.current
    val gridState = rememberLazyGridState()

    LaunchedEffect(Unit) {
        if (movies.isEmpty()) {
            viewModel.loadMovies()
        }
    }

    // Whenever the genre or sort filter changes, jump back to the top of
    // the grid instead of leaving the user scrolled into the middle of a
    // now-different list.
    LaunchedEffect(viewModel.selectedGenreId, viewModel.selectedFilterType) {
        gridState.scrollToItem(0)
    }

    // Infinite scroll: request the next page once the user is close to the
    // bottom of the currently loaded list.
    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = gridState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            totalItems > 0 && lastVisible >= totalItems - 6
        }
    }
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            viewModel.loadMoreMovies()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Small breathing room under the status bar - the floating
            // "Movies" title chip that used to sit here has been removed.
            Spacer(modifier = Modifier.height(TOP_BAR_CLEARANCE))

            // Single, unified filter control (sort type + genre) that opens
            // a bottom sheet - replaces the old overlapping tab row.
            GenreFilterSection(
                genres = genres,
                selectedGenreId = viewModel.selectedGenreId,
                selectedFilterType = viewModel.selectedFilterType,
                onGenreSelected = { viewModel.selectGenre(it) },
                onFilterTypeSelected = { viewModel.selectFilterType(it) },
                onSearchClick = {
                    navController?.navigate("search") {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )

            // Movies grid
            when {
                isLoading && movies.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
                errorMessage != null && movies.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                movies.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No movies found",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                else -> {
                    val columns = DeviceUtils.getGridColumns(context.resources)
                    LazyVerticalGrid(
                        state = gridState,
                        columns = GridCells.Fixed(columns),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = 12.dp,
                            bottom = 12.dp + BOTTOM_BAR_CLEARANCE
                        ),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = movies,
                            key = { movie: Movie -> movie.id }
                        ) { movie: Movie ->
                            PosterCard(
                                image = movie.image,
                                title = movie.title,
                                year = movie.year,
                                imdb = movie.imdb,
                                subtitle = movie.genres.firstOrNull()?.title,
                                onClick = {
                                    StorageUtils.saveMovieToFile(context, movie)
                                    navController?.navigate("single_movie/${movie.id}")
                                }
                            )
                        }

                        if (isLoadingMore) {
                            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

package com.pira.ccloud.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.pira.ccloud.components.GenreFilterSection
import com.pira.ccloud.components.PosterCard
import com.pira.ccloud.data.model.Genre
import com.pira.ccloud.data.model.Series
import com.pira.ccloud.ui.series.SeriesViewModel
import com.pira.ccloud.utils.DeviceUtils
import com.pira.ccloud.utils.StorageUtils

/**
 * Small top breathing room above the filter row. The floating "Series"
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
fun SeriesScreen(
    viewModel: SeriesViewModel = hiltViewModel(),
    navController: NavController? = null
) {
    val seriesList: List<Series> = viewModel.series
    val genres: List<Genre> = viewModel.genres
    val isLoading: Boolean = viewModel.isLoading
    val isLoadingMore: Boolean = viewModel.isLoadingMore
    val errorMessage: String? = viewModel.errorMessage
    val context = LocalContext.current
    val gridState = rememberLazyGridState()

    LaunchedEffect(Unit) {
        if (seriesList.isEmpty()) {
            viewModel.loadSeries()
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
            viewModel.loadMoreSeries()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Small breathing room under the status bar - the floating
            // "Series" title chip that used to sit here has been removed.
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

            // Series grid
            when {
                isLoading && seriesList.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
                errorMessage != null && seriesList.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = errorMessage ?: "An error occurred",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            androidx.compose.material3.Button(
                                onClick = { viewModel.retry() },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                }
                seriesList.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No series found",
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
                            items = seriesList,
                            key = { series: Series -> series.id }
                        ) { series: Series ->
                            PosterCard(
                                image = series.image,
                                title = series.title,
                                year = series.year,
                                imdb = series.imdb,
                                subtitle = series.genres.firstOrNull()?.title,
                                onClick = {
                                    // Persist the series so SingleSeriesScreen can
                                    // load it - without this the details screen
                                    // stays blank forever.
                                    StorageUtils.saveSeriesToFile(context, series)
                                    navController?.navigate("single_series/${series.id}")
                                }
                            )
                        }

                        if (isLoadingMore) {
                            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier
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

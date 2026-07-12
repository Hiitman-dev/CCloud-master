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
<<<<<<< HEAD
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
=======
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
>>>>>>> b37905b39637561788e731dede0e9b4306cad5e0
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
<<<<<<< HEAD
=======
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
>>>>>>> b37905b39637561788e731dede0e9b4306cad5e0
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
<<<<<<< HEAD
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pira.ccloud.components.FloatingTitleBar
import com.pira.ccloud.components.GenreFilterSection
import com.pira.ccloud.components.PosterCard
=======
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pira.ccloud.components.FloatingTopBar
import com.pira.ccloud.components.PosterCard
import com.pira.ccloud.data.model.FilterType
>>>>>>> b37905b39637561788e731dede0e9b4306cad5e0
import com.pira.ccloud.data.model.Genre
import com.pira.ccloud.data.model.Movie
import com.pira.ccloud.ui.movies.MoviesViewModel
import com.pira.ccloud.utils.DeviceUtils
import com.pira.ccloud.utils.StorageUtils

<<<<<<< HEAD
/**
 * Height reserved above the scrollable content so it never sits underneath
 * the floating glass title bar. Must stay in sync with FloatingTitleBar's
 * real rendered height (see FloatingTopBar.kt) plus its top offset.
 */
private val TOP_BAR_CLEARANCE = 96.dp

=======
>>>>>>> b37905b39637561788e731dede0e9b4306cad5e0
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
<<<<<<< HEAD
    val gridState = rememberLazyGridState()
=======
    var showFilterSheet by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) }

    val filterTypes = listOf("Default", "By Year", "By IMDB")
>>>>>>> b37905b39637561788e731dede0e9b4306cad5e0

    LaunchedEffect(Unit) {
        if (movies.isEmpty()) {
            viewModel.loadMovies()
        }
<<<<<<< HEAD
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
=======
>>>>>>> b37905b39637561788e731dede0e9b4306cad5e0
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
<<<<<<< HEAD
            // Reserve space so the floating title bar never overlaps the
            // filter row or the grid below it, and so nothing under it is
            // accidentally untappable.
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
=======
            Spacer(modifier = Modifier.height(70.dp))

            // Filter chips row
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    if (selectedTab < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            height = 3.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                divider = {}
            ) {
                filterTypes.forEachIndexed { index, label ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = {
                            selectedTab = index
                            val filterType = when (index) {
                                0 -> FilterType.DEFAULT
                                1 -> FilterType.BY_YEAR
                                2 -> FilterType.BY_IMDB
                                else -> FilterType.DEFAULT
                            }
                            viewModel.selectFilterType(filterType)
                        },
                        text = {
                            Text(
                                text = label,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }
>>>>>>> b37905b39637561788e731dede0e9b4306cad5e0

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
<<<<<<< HEAD
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
=======
                else -> {
                    val columns = DeviceUtils.getGridColumns(context.resources)
                    LazyVerticalGrid(
>>>>>>> b37905b39637561788e731dede0e9b4306cad5e0
                        columns = GridCells.Fixed(columns),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
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
<<<<<<< HEAD
                            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = MaterialTheme.colorScheme.primary
=======
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(16.dp)
>>>>>>> b37905b39637561788e731dede0e9b4306cad5e0
                                    )
                                }
                            }
                        }
<<<<<<< HEAD
=======
                    }
                }
            }
        }

        // Floating top bar
        val selectedGenreName = genres.find { it.id == viewModel.selectedGenreId }?.title ?: "All"
        FloatingTopBar(
            title = "Movies",
            filterText = selectedGenreName,
            onSearchClick = {
                navController?.navigate("search") {
                    launchSingleTop = true
                    restoreState = true
                }
            },
            onFilterClick = { showFilterSheet = true },
            modifier = Modifier.padding(top = 40.dp)
        )
    }

    // Genre filter bottom sheet
    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Select Genre",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // All genres option
                FilterChip(
                    selected = viewModel.selectedGenreId == 0,
                    onClick = {
                        viewModel.selectGenre(0)
                        showFilterSheet = false
                    },
                    label = { Text("All Genres") },
                    modifier = Modifier.padding(bottom = 8.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )

                // Genre chips
                genres.chunked(2).forEach { rowGenres ->
                    rowGenres.forEach { genre ->
                        FilterChip(
                            selected = viewModel.selectedGenreId == genre.id,
                            onClick = {
                                viewModel.selectGenre(genre.id)
                                showFilterSheet = false
                            },
                            label = { Text(genre.title) },
                            modifier = Modifier.padding(bottom = 8.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
>>>>>>> b37905b39637561788e731dede0e9b4306cad5e0
                    }
                }
            }
        }
<<<<<<< HEAD

        // Floating title bar - purely branding/navigation, no filter logic
        // baked in, so it never fights with the filter row below it.
        FloatingTitleBar(
            title = "Movies",
            modifier = Modifier.padding(top = 20.dp)
        )
=======
>>>>>>> b37905b39637561788e731dede0e9b4306cad5e0
    }
}

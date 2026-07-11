package com.pira.ccloud.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.pira.ccloud.data.model.FavoriteItem
import com.pira.ccloud.data.model.Movie
import com.pira.ccloud.data.model.Series
import com.pira.ccloud.ui.home.HomeViewModel
import com.pira.ccloud.ui.theme.GlassCorners
import com.pira.ccloud.ui.theme.rememberGlassTint
import com.pira.ccloud.ui.theme.subtleGlassSurface
import com.pira.ccloud.utils.DeviceUtils
import com.pira.ccloud.utils.StorageUtils
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween

private enum class HomeTab(val label: String) {
    CONTINUE_WATCHING("Continue Watching"),
    TODAY_UPDATES("Today's Updates"),
    NEW_RELEASES("New Cinema Releases"),
    MOVIES("Movies"),
    SERIES("Series")
}

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    navController: NavController? = null
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showFilterSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (viewModel.todayMovies.isEmpty() && viewModel.todaySeries.isEmpty()) {
            viewModel.loadAllSections()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(120.dp))

            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 16.dp,
                containerColor = Color.Transparent,
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
                HomeTab.entries.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = tab.label,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
            }

            when (HomeTab.entries[selectedTab]) {
                HomeTab.CONTINUE_WATCHING -> ContinueWatchingTab(viewModel, navController)
                HomeTab.TODAY_UPDATES -> TodayUpdatesTab(viewModel, navController)
                HomeTab.NEW_RELEASES -> NewReleasesTab(viewModel, navController)
                HomeTab.MOVIES -> MoviesTab(viewModel, navController)
                HomeTab.SERIES -> SeriesTab(viewModel, navController)
            }
        }

        FloatingTopBar(
            onSearchClick = {
                navController?.navigate("search") {
                    launchSingleTop = true
                    restoreState = true
                }
            },
            onFilterClick = { showFilterSheet = true }
        )
    }

    if (showFilterSheet) {
        com.pira.ccloud.components.GenreFilterBottomSheet(
            genres = viewModel.genres,
            selectedGenreId = viewModel.selectedGenreId,
            selectedFilterType = viewModel.selectedFilterType,
            onGenreSelected = { genreId -> viewModel.selectGenre(genreId) },
            onFilterTypeSelected = { filterType -> viewModel.selectFilterType(filterType) },
            onDismiss = { showFilterSheet = false }
        )
    }
}

@Composable
private fun FloatingTopBar(
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit
) {
    val glassTint = rememberGlassTint()
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .zIndex(10f)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(GlassCorners.Navigation),
                ambientColor = if (isDark) Color.Black.copy(alpha = 0.4f) else Color.Black.copy(alpha = 0.08f),
                spotColor = if (isDark) Color.Black.copy(alpha = 0.3f) else Color.Black.copy(alpha = 0.06f)
            )
            .clip(RoundedCornerShape(GlassCorners.Navigation))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        glassTint.copy(alpha = if (isDark) 0.35f else 0.45f),
                        glassTint.copy(alpha = if (isDark) 0.22f else 0.35f)
                    )
                )
            )
            .border(
                width = 0.5.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = if (isDark) 0.12f else 0.5f),
                        Color.White.copy(alpha = if (isDark) 0.05f else 0.2f)
                    )
                ),
                shape = RoundedCornerShape(GlassCorners.Navigation)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "CCloud",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlassCircleButton(
                    icon = Icons.Default.FilterList,
                    contentDescription = "Filter",
                    onClick = onFilterClick,
                    isDark = isDark
                )
                GlassCircleButton(
                    icon = Icons.Default.Search,
                    contentDescription = "Search",
                    onClick = onSearchClick,
                    isDark = isDark
                )
            }
        }
    }
}

@Composable
private fun GlassCircleButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    isDark: Boolean
) {
    val glassTint = rememberGlassTint()
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(42.dp)
            .shadow(
                elevation = 6.dp,
                shape = CircleShape,
                ambientColor = if (isDark) Color.Black.copy(alpha = 0.3f) else Color.Black.copy(alpha = 0.06f),
                spotColor = if (isDark) Color.Black.copy(alpha = 0.2f) else Color.Black.copy(alpha = 0.04f)
            )
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        glassTint.copy(alpha = if (isDark) 0.3f else 0.5f),
                        glassTint.copy(alpha = if (isDark) 0.15f else 0.3f)
                    )
                )
            )
            .border(
                width = 0.5.dp,
                color = Color.White.copy(alpha = if (isDark) 0.1f else 0.4f),
                shape = CircleShape
            )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
    }
}

// ---- Tab Content ----

@Composable
private fun ContinueWatchingTab(viewModel: HomeViewModel, navController: NavController?) {
    val itemsList: List<FavoriteItem> = viewModel.recentlyViewed
    val context = LocalContext.current

    if (itemsList.isEmpty()) {
        EmptyState("No recently viewed content", "Start watching movies or series to see them here")
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(DeviceUtils.getGridColumns(LocalContext.current.resources)),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = itemsList,
                key = { item: FavoriteItem -> "${item.type}_${item.id}" }
            ) { item: FavoriteItem ->
                FavoriteContentCard(
                    item = item,
                    onClick = {
                        StorageUtils.saveRecentlyViewed(context, item)
                        if (item.type == "movie") {
                            navController?.navigate("single_movie/${item.id}")
                        } else {
                            navController?.navigate("single_series/${item.id}")
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun TodayUpdatesTab(viewModel: HomeViewModel, navController: NavController?) {
    val moviesList: List<Movie> = viewModel.todayMovies
    val seriesList: List<Series> = viewModel.todaySeries

    if (moviesList.isEmpty() && seriesList.isEmpty() && viewModel.isLoading) {
        LoadingState()
    } else if (moviesList.isEmpty() && seriesList.isEmpty()) {
        EmptyState("No updates today", "Check back later for new content")
    } else {
        val columns: Int = DeviceUtils.getGridColumns(LocalContext.current.resources)
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (moviesList.isNotEmpty()) {
                item(span = { GridItemSpan(columns) }) {
                    SectionHeader("Movies Updated Today")
                }
                items(
                    items = moviesList,
                    key = { movie: Movie -> movie.id }
                ) { movie: Movie ->
                    MovieCard(movie = movie, onClick = {
                        StorageUtils.saveMovieToFile(LocalContext.current, movie)
                        navController?.navigate("single_movie/${movie.id}")
                    })
                }
            }
            if (seriesList.isNotEmpty()) {
                item(span = { GridItemSpan(columns) }) {
                    SectionHeader("Series Updated Today")
                }
                items(
                    items = seriesList,
                    key = { series: Series -> series.id }
                ) { series: Series ->
                    SeriesCard(series = series, onClick = {
                        navController?.navigate("single_series/${series.id}")
                    })
                }
            }
        }
    }
}

@Composable
private fun NewReleasesTab(viewModel: HomeViewModel, navController: NavController?) {
    val moviesList: List<Movie> = viewModel.newReleases

    if (moviesList.isEmpty() && viewModel.isLoading) {
        LoadingState()
    } else if (moviesList.isEmpty()) {
        EmptyState("No new releases", "Check back soon for new cinema content")
    } else {
        val columns: Int = DeviceUtils.getGridColumns(LocalContext.current.resources)
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = moviesList,
                key = { movie: Movie -> movie.id }
            ) { movie: Movie ->
                MovieCard(movie = movie, onClick = {
                    StorageUtils.saveMovieToFile(LocalContext.current, movie)
                    navController?.navigate("single_movie/${movie.id}")
                })
            }
        }
    }
}

@Composable
private fun MoviesTab(viewModel: HomeViewModel, navController: NavController?) {
    val moviesList: List<Movie> = viewModel.movies
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (moviesList.isEmpty()) {
            viewModel.loadMovies()
        }
    }

    when {
        viewModel.isLoading && moviesList.isEmpty() -> {
            LoadingState()
        }
        viewModel.errorMessage != null && moviesList.isEmpty() -> {
            val msg: String = viewModel.errorMessage ?: "Unknown error"
            ErrorState(
                message = msg,
                onRetry = { viewModel.refreshMovies() }
            )
        }
        else -> {
            val columns: Int = DeviceUtils.getGridColumns(context.resources)
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(
                    items = moviesList,
                    key = { index: Int, movie: Movie -> movie.id }
                ) { index: Int, movie: Movie ->
                    MovieCard(movie = movie, onClick = {
                        StorageUtils.saveMovieToFile(context, movie)
                        navController?.navigate("single_movie/${movie.id}")
                    })
                    if (index >= moviesList.size - 3) {
                        LaunchedEffect(Unit) {
                            viewModel.loadMoreMovies()
                        }
                    }
                }
                if (viewModel.isLoadingMore) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            HomeProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SeriesTab(viewModel: HomeViewModel, navController: NavController?) {
    val seriesList: List<Series> = viewModel.series
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (seriesList.isEmpty()) {
            viewModel.loadSeries()
        }
    }

    when {
        viewModel.isLoading && seriesList.isEmpty() -> {
            LoadingState()
        }
        viewModel.errorMessage != null && seriesList.isEmpty() -> {
            val msg: String = viewModel.errorMessage ?: "Unknown error"
            ErrorState(
                message = msg,
                onRetry = { viewModel.refreshSeries() }
            )
        }
        else -> {
            val columns: Int = DeviceUtils.getGridColumns(context.resources)
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(
                    items = seriesList,
                    key = { index: Int, series: Series -> series.id }
                ) { index: Int, series: Series ->
                    SeriesCard(series = series, onClick = {
                        navController?.navigate("single_series/${series.id}")
                    })
                    if (index >= seriesList.size - 3) {
                        LaunchedEffect(Unit) {
                            viewModel.loadMoreSeries()
                        }
                    }
                }
                if (viewModel.isLoadingMore) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            HomeProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}

// ---- Reusable UI Components ----

@Composable
private fun HomeProgressIndicator() {
    val transition = rememberInfiniteTransition(label = "progress")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing)
        ), label = "progress_anim"
    )
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing)
        ), label = "rotation_anim"
    )
    CircularProgressIndicator(
        progress = { progress },
        modifier = Modifier.size(48.dp).rotate(rotation),
        strokeWidth = 4.dp,
        trackColor = MaterialTheme.colorScheme.surfaceVariant,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
private fun FavoriteContentCard(item: FavoriteItem, onClick: () -> Unit) {
    val cardTint = rememberGlassTint()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .subtleGlassSurface(shape = RoundedCornerShape(16.dp), tint = cardTint)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column {
            Box {
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(item.image)
                            .crossfade(true)
                            .build()
                    ),
                    contentDescription = item.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop
                )
                Card(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp),
                    shape = RoundedCornerShape(50.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
                    )
                ) {
                    Text(
                        text = if (item.type == "movie") "Movie" else "Series",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                Card(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp),
                    shape = RoundedCornerShape(50.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.7f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Color.Yellow,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = String.format("%.1f", item.imdb),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.year.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun MovieCard(movie: Movie, onClick: () -> Unit) {
    val cardTint = rememberGlassTint()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(310.dp)
            .subtleGlassSurface(shape = RoundedCornerShape(20.dp), tint = cardTint)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp)
        ) {
            Box {
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(movie.image)
                            .crossfade(true)
                            .build()
                    ),
                    contentDescription = movie.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Card(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp),
                    shape = RoundedCornerShape(50.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.7f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Color.Yellow,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = String.format("%.1f", movie.imdb),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = movie.year.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (movie.genres.isNotEmpty()) {
                    Text(
                        text = movie.genres.joinToString(", ") { it.title },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun SeriesCard(series: Series, onClick: () -> Unit) {
    val cardTint = rememberGlassTint()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(310.dp)
            .subtleGlassSurface(shape = RoundedCornerShape(20.dp), tint = cardTint)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp)
        ) {
            Box {
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(series.image)
                            .crossfade(true)
                            .build()
                    ),
                    contentDescription = series.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Card(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp),
                    shape = RoundedCornerShape(50.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.7f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Color.Yellow,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = String.format("%.1f", series.imdb),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = series.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = series.year.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (series.genres.isNotEmpty()) {
                    Text(
                        text = series.genres.joinToString(", ") { it.title },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyState(title: String, subtitle: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun LoadingState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Loading...",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(16.dp),
            fontWeight = FontWeight.Medium
        )
        HomeProgressIndicator()
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Something went wrong",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Button(onClick = onRetry) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Retry",
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Retry")
        }
    }
}

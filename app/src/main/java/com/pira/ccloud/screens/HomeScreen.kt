package com.pira.ccloud.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pira.ccloud.components.ContentCarousel
import com.pira.ccloud.components.FloatingTopBar
import com.pira.ccloud.components.HeroCard
import com.pira.ccloud.components.PosterCard
import com.pira.ccloud.data.model.FavoriteItem
import com.pira.ccloud.data.model.Movie
import com.pira.ccloud.data.model.Series
import com.pira.ccloud.ui.home.HomeViewModel
import com.pira.ccloud.ui.theme.rememberGlassTint
import com.pira.ccloud.utils.StorageUtils
import com.pira.ccloud.utils.ViewHistoryManager
import com.pira.ccloud.utils.WatchStats

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    navController: NavController? = null
) {
    val context = LocalContext.current
    var heroIndex by remember { mutableIntStateOf(0) }
    val watchStats = remember { ViewHistoryManager.getStats(context) }

    LaunchedEffect(Unit) {
        if (viewModel.todayMovies.isEmpty() && viewModel.todaySeries.isEmpty()) {
            viewModel.loadAllSections()
        }
    }

    // Rotate hero every 5 seconds
    LaunchedEffect(viewModel.todayMovies) {
        if (viewModel.todayMovies.isNotEmpty()) {
            while (true) {
                kotlinx.coroutines.delay(5000)
                heroIndex = (heroIndex + 1) % viewModel.todayMovies.size
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Scrollable content
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Spacer for floating top bar
            item { Spacer(modifier = Modifier.height(70.dp)) }

            // Watch Analytics Summary
            if (watchStats.totalContentWatched > 0) {
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    WatchAnalyticsCard(stats = watchStats)
                }
            }

            // Hero section
            val heroMovies: List<Movie> = viewModel.todayMovies
            if (heroMovies.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    val hero = heroMovies[heroIndex % heroMovies.size]
                    HeroCard(
                        image = hero.image,
                        cover = hero.cover,
                        title = hero.title,
                        description = hero.description,
                        year = hero.year,
                        imdb = hero.imdb,
                        genres = hero.genres.map { it.title },
                        onClick = {
                            StorageUtils.saveMovieToFile(context, hero)
                            navController?.navigate("single_movie/${hero.id}")
                        },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            // Continue Watching (from view history)
            val continueWatching = ViewHistoryManager.getContinueWatching(context)
            if (continueWatching.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(28.dp))
                    ContentCarousel(
                        title = "Continue Watching",
                        items = continueWatching
                    ) { entry ->
                        val subtitleText = when (entry.status) {
                            ViewHistoryManager.ContentStatus.WATCHING -> "Watching Now"
                            ViewHistoryManager.ContentStatus.WATCHED -> "Watched"
                            ViewHistoryManager.ContentStatus.DOWNLOADED -> "Downloaded"
                            ViewHistoryManager.ContentStatus.EXTERNAL -> "External Player"
                            ViewHistoryManager.ContentStatus.ADDED -> "Tap to watch"
                        }
                        PosterCard(
                            image = entry.image,
                            title = entry.title,
                            year = 2024,
                            imdb = 0.0,
                            subtitle = subtitleText,
                            onClick = {
                                if (entry.type == "movie") {
                                    navController?.navigate("single_movie/${entry.id}")
                                } else {
                                    navController?.navigate("single_series/${entry.id}")
                                }
                            }
                        )
                    }
                }
            }

            // Recently Viewed
            val recentlyViewed: List<FavoriteItem> = viewModel.recentlyViewed
            if (recentlyViewed.isNotEmpty() && recentlyWatched.isEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(28.dp))
                    ContentCarousel(
                        title = "Recently Viewed",
                        items = recentlyViewed
                    ) { item: FavoriteItem ->
                        PosterCard(
                            image = item.image,
                            title = item.title,
                            year = item.year,
                            imdb = item.imdb,
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

            // Today's Movies
            val todayMovies: List<Movie> = viewModel.todayMovies
            if (todayMovies.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(28.dp))
                    ContentCarousel(
                        title = "Today's Updates",
                        items = todayMovies
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
                }
            }

            // Today's Series
            val todaySeries: List<Series> = viewModel.todaySeries
            if (todaySeries.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(28.dp))
                    ContentCarousel(
                        title = "Series Updates",
                        items = todaySeries
                    ) { series: Series ->
                        PosterCard(
                            image = series.image,
                            title = series.title,
                            year = series.year,
                            imdb = series.imdb,
                            subtitle = series.genres.firstOrNull()?.title,
                            onClick = {
                                navController?.navigate("single_series/${series.id}")
                            }
                        )
                    }
                }
            }

            // New Cinema Releases
            val newReleases: List<Movie> = viewModel.newReleases
            if (newReleases.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(28.dp))
                    ContentCarousel(
                        title = "New Cinema Releases",
                        items = newReleases
                    ) { movie: Movie ->
                        PosterCard(
                            image = movie.image,
                            title = movie.title,
                            year = movie.year,
                            imdb = movie.imdb,
                            onClick = {
                                StorageUtils.saveMovieToFile(context, movie)
                                navController?.navigate("single_movie/${movie.id}")
                            }
                        )
                    }
                }
            }

            // Loading indicator at bottom
            if (viewModel.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Floating top bar
        FloatingTopBar(
            title = "CCloud",
            filterText = "Home",
            onSearchClick = {
                navController?.navigate("search") {
                    launchSingleTop = true
                    restoreState = true
                }
            },
            onFilterClick = { /* No-op on home */ },
            modifier = Modifier.padding(top = 40.dp)
        )
    }
}

/**
 * Compact watch analytics card showing user's viewing stats.
 */
@Composable
private fun WatchAnalyticsCard(stats: WatchStats) {
    val glassTint = rememberGlassTint()
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = if (isDark) 0.12f else 0.08f),
                        MaterialTheme.colorScheme.primary.copy(alpha = if (isDark) 0.06f else 0.04f)
                    )
                )
            )
            .border(
                width = 0.5.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(16.dp)
    ) {
        Text(
            text = "Your Watch Stats",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                icon = Icons.Default.Favorite,
                value = "${stats.totalContentWatched}",
                label = "Watched",
                tint = MaterialTheme.colorScheme.primary
            )
            StatItem(
                icon = Icons.Default.Movie,
                value = "${stats.moviesWatched}",
                label = "Movies",
                tint = Color(0xFFFF6B6B)
            )
            StatItem(
                icon = Icons.Default.Tv,
                value = "${stats.seriesWatched}",
                label = "Series",
                tint = Color(0xFF4ECDC4)
            )
            StatItem(
                icon = Icons.Default.Star,
                value = stats.formattedTotalTime,
                label = "Total",
                tint = Color(0xFFFFC107)
            )
        }
        if (stats.topGenres.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Top genres: ${stats.topGenres.joinToString(", ")}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    tint: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

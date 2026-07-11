package com.pira.ccloud.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
import com.pira.ccloud.utils.StorageUtils

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    navController: NavController? = null
) {
    val context = LocalContext.current
    var heroIndex by remember { mutableIntStateOf(0) }

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

            // Hero section
            val heroMovies = viewModel.todayMovies
            if (heroMovies.isNotEmpty()) {
                item {
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

            // Continue Watching
            val recentlyViewed: List<FavoriteItem> = viewModel.recentlyViewed
            if (recentlyViewed.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    ContentCarousel(
                        title = "Continue Watching",
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
                    Spacer(modifier = Modifier.height(24.dp))
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
                    Spacer(modifier = Modifier.height(24.dp))
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
                    Spacer(modifier = Modifier.height(24.dp))
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
            onSearchClick = {
                navController?.navigate("search") {
                    launchSingleTop = true
                    restoreState = true
                }
            },
            modifier = Modifier.padding(top = 40.dp)
        )
    }
}

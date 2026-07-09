package com.pira.ccloud.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.pira.ccloud.data.model.FavoriteItem
import com.pira.ccloud.data.model.Movie
import com.pira.ccloud.data.model.Series
import com.pira.ccloud.ui.theme.GlassCorners
import com.pira.ccloud.ui.theme.subtleGlassSurface
import com.pira.ccloud.utils.StorageUtils

/**
 * Home screen header content: "Continue Watching" (recently opened
 * movies/series), "Today's Updates" (latest series), and "New & Hot" (latest
 * movies). Rendered as a single full-width grid item above the regular
 * movie grid.
 */
@Composable
fun HomeHeaderSections(
    recentlyViewed: List<FavoriteItem>,
    newAndHot: List<Movie>,
    todaysUpdates: List<Series> = emptyList(),
    navController: NavController?
) {
    val context = LocalContext.current

    Column {
        if (recentlyViewed.isNotEmpty()) {
            SectionHeader(title = "Continue Watching")
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                items(recentlyViewed) { item ->
                    ContinueWatchingCard(item = item, navController = navController)
                }
            }
        }

        if (todaysUpdates.isNotEmpty()) {
            SectionHeader(title = "Today's Updates")
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                items(todaysUpdates) { series ->
                    NewAndHotCard(
                        movie = series.toMovie(),
                        badgeLabel = "NEW EP",
                        onClick = {
                            StorageUtils.saveSeriesToFile(context, series)
                            navController?.navigate("single_series/${series.id}")
                        }
                    )
                }
            }
        }

        if (newAndHot.isNotEmpty()) {
            SectionHeader(title = "New & Hot")
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                items(newAndHot) { movie ->
                    NewAndHotCard(
                        movie = movie,
                        badgeLabel = "NEW",
                        onClick = {
                            StorageUtils.saveMovieToFile(context, movie)
                            navController?.navigate("single_movie/${movie.id}")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(bottom = 10.dp)
    )
}

@Composable
private fun ContinueWatchingCard(
    item: FavoriteItem,
    navController: NavController?
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .width(220.dp)
            .height(124.dp)
            .clip(RoundedCornerShape(GlassCorners.Card))
            .clickable {
                val route = if (item.type == "series") "single_series/${item.id}" else "single_movie/${item.id}"
                navController?.navigate(route)
            }
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(context)
                    .data(item.image)
                    .crossfade(true)
                    .build()
            ),
            contentDescription = item.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        // Soft gradient so the title stays readable over any artwork.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f)),
                        startY = 40f
                    )
                )
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(10.dp)
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.labelLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .subtleGlassSurface(shape = RoundedCornerShape(50), tint = Color.White)
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Resume",
                tint = Color.White
            )
        }
    }
}

@Composable
fun NewAndHotCard(
    movie: Movie,
    badgeLabel: String = "NEW",
    onClick: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .width(130.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .width(130.dp)
                .height(180.dp)
                .clip(RoundedCornerShape(GlassCorners.Card))
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(context)
                        .data(movie.image)
                        .crossfade(true)
                        .build()
                ),
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Badge (e.g. "NEW" or "NEW EP")
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(6.dp)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(GlassCorners.Tag)
                    )
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = badgeLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Text(
            text = movie.title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            modifier = Modifier.padding(top = 6.dp)
        )
    }
}

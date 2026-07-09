package com.pira.ccloud.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import com.pira.ccloud.ui.theme.GlassCorners
import com.pira.ccloud.ui.theme.glassSurface
import com.pira.ccloud.ui.theme.subtleGlassSurface
import com.pira.ccloud.ui.theme.rememberGlassTint
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.pira.ccloud.data.model.FilterType
import com.pira.ccloud.data.model.Poster
import com.pira.ccloud.ui.country.CountryViewModel
import com.pira.ccloud.utils.DeviceUtils
import com.pira.ccloud.utils.StorageUtils

@Composable
fun CountryScreen(
    countryId: Int,
    viewModel: CountryViewModel = viewModel(),
    navController: NavController? = null
) {
    LaunchedEffect(countryId) { viewModel.setCountryId(countryId) }
    val posters = viewModel.posters; val countryName = viewModel.countryName
    val isLoading = viewModel.isLoading; val isLoadingMore = viewModel.isLoadingMore
    val errorMessage = viewModel.errorMessage; val selectedFilterType = viewModel.selectedFilterType

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Row(modifier = Modifier.fillMaxWidth().padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController?.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
            Text(countryName, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.weight(1f).padding(start = 16.dp))
            var filterMenuExpanded by remember { mutableStateOf(false) }
            Box {
                IconButton(onClick = { filterMenuExpanded = true }) { Icon(Icons.Default.FilterList, contentDescription = "Filter", modifier = Modifier.size(24.dp)) }
                DropdownMenu(expanded = filterMenuExpanded, onDismissRequest = { filterMenuExpanded = false }) {
                    DropdownMenuItem(text = { Text("Default") }, onClick = { viewModel.selectFilterType(FilterType.DEFAULT); filterMenuExpanded = false })
                    DropdownMenuItem(text = { Text("By Year") }, onClick = { viewModel.selectFilterType(FilterType.BY_YEAR); filterMenuExpanded = false })
                    DropdownMenuItem(text = { Text("By IMDB") }, onClick = { viewModel.selectFilterType(FilterType.BY_IMDB); filterMenuExpanded = false })
                }
            }
        }

        if (selectedFilterType != FilterType.DEFAULT) {
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp), horizontalArrangement = Arrangement.End) {
                Text(when (selectedFilterType) { FilterType.DEFAULT -> ""; FilterType.BY_YEAR -> "Sorted by Year"; FilterType.BY_IMDB -> "Sorted by IMDB" }, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
            }
        }

        when {
            isLoading && posters.isEmpty() -> CountryLoadingScreen()
            errorMessage != null && posters.isEmpty() -> CountryErrorScreen(errorMessage) { viewModel.retry() }
            else -> CountryPosterGrid(posters, isLoading, isLoadingMore, errorMessage, { viewModel.retry() }, { viewModel.refresh() }, { viewModel.loadMorePosters() }, navController)
        }
    }
}

@Composable
fun CountryLoadingScreen() {
    val shimmerColor = MaterialTheme.colorScheme.surfaceVariant
    val shimmerColorShade = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Loading Content…", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(24.dp), fontWeight = FontWeight.Medium)
        AnimatedVisibility(visible = true, enter = fadeIn(initialAlpha = 0.3f), exit = fadeOut()) {
            val columns = DeviceUtils.getGridColumns(LocalContext.current.resources)
            LazyVerticalGrid(columns = GridCells.Fixed(columns), modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(24.dp), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(6) { CountryShimmerPosterItem(shimmerColor, shimmerColorShade) }
            }
        }
    }
}

@Composable
fun CountryShimmerPosterItem(shimmerColor: Color, shimmerColorShade: Color) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(initialValue = -1000f, targetValue = 1000f, animationSpec = infiniteRepeatable(tween(1200)), label = "shimmer_translate")
    val brush = Brush.linearGradient(listOf(shimmerColor, shimmerColorShade, shimmerColor, shimmerColorShade, shimmerColor), start = Offset.Zero, end = Offset(translateAnim, translateAnim))
    val countryShimmerGlassTint = rememberGlassTint()
    Card(modifier = Modifier.fillMaxWidth().subtleGlassSurface(shape = RoundedCornerShape(GlassCorners.Card), tint = countryShimmerGlassTint), shape = RoundedCornerShape(GlassCorners.Card), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp), colors = CardDefaults.cardColors(containerColor = Color.Transparent)) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            Box(modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(GlassCorners.Poster)).background(brush))
            Spacer(modifier = Modifier.height(12.dp)); Box(modifier = Modifier.fillMaxWidth(0.8f).height(20.dp).background(brush))
            Spacer(modifier = Modifier.height(8.dp)); Box(modifier = Modifier.width(60.dp).height(16.dp).background(brush))
            Spacer(modifier = Modifier.height(8.dp)); Box(modifier = Modifier.fillMaxWidth(0.6f).height(16.dp).background(brush))
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) { Box(modifier = Modifier.size(16.dp).background(brush)); Spacer(modifier = Modifier.width(8.dp)); Box(modifier = Modifier.width(40.dp).height(16.dp).background(brush)) }
        }
    }
}

@Composable
fun CountryPosterGrid(posters: List<Poster>, isLoading: Boolean, isLoadingMore: Boolean, errorMessage: String?, onRetry: () -> Unit, onRefresh: () -> Unit, onLoadMore: () -> Unit, navController: NavController? = null) {
    val postersList = posters.toList(); val context = LocalContext.current
    val columns = DeviceUtils.getGridColumns(LocalContext.current.resources)
    LazyVerticalGrid(columns = GridCells.Fixed(columns), modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(24.dp), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        itemsIndexed(postersList) { index, poster ->
            CountryPosterItem(poster = poster, onClick = {
                if (poster.isMovie()) { StorageUtils.saveMovieToFile(context, poster.toMovie()); navController?.navigate("single_movie/${poster.id}") }
                else if (poster.isSeries()) { StorageUtils.saveSeriesToFile(context, poster.toSeries()); navController?.navigate("single_series/${poster.id}") }
            })
            if (index >= postersList.size - 3) LaunchedEffect(Unit) { onLoadMore() }
        }
        if (isLoadingMore) { item { Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(modifier = Modifier.size(48.dp), strokeWidth = 3.dp, trackColor = MaterialTheme.colorScheme.surfaceVariant, color = MaterialTheme.colorScheme.primary) } } }
        if (errorMessage != null) { item { CountryErrorItem(errorMessage, onRetry) } }
        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}

@Composable
fun CountryPosterItem(poster: Poster, onClick: () -> Unit) {
    val countryPosterGlassTint = rememberGlassTint()
    Card(modifier = Modifier.fillMaxWidth().height(310.dp).subtleGlassSurface(shape = RoundedCornerShape(GlassCorners.Card), tint = countryPosterGlassTint).clickable { onClick() }, shape = RoundedCornerShape(GlassCorners.Card), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp), colors = CardDefaults.cardColors(containerColor = Color.Transparent)) {
        Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            Box {
                Image(painter = rememberAsyncImagePainter(ImageRequest.Builder(LocalContext.current).data(poster.image).crossfade(true).build()), contentDescription = poster.title, modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(GlassCorners.Poster)), contentScale = ContentScale.Crop)
                Card(modifier = Modifier.align(Alignment.TopEnd).padding(8.dp), shape = RoundedCornerShape(GlassCorners.Tag), colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.7f))) {
                    Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Star, contentDescription = "Rating", tint = Color.Yellow, modifier = Modifier.size(14.dp)); Spacer(modifier = Modifier.width(4.dp)); Text(String.format("%.1f", poster.imdb), style = MaterialTheme.typography.bodySmall, color = Color.White, fontWeight = FontWeight.Medium) }
                }
                Card(modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp), shape = RoundedCornerShape(GlassCorners.Tag), colors = CardDefaults.cardColors(containerColor = if (poster.isMovie()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary)) {
                    Box(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) { Text(if (poster.isMovie()) "Movie" else "Series", style = MaterialTheme.typography.bodySmall, color = if (poster.isMovie()) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary, fontWeight = FontWeight.Medium) }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(poster.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium, maxLines = 2, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(4.dp)); Text(poster.year.toString(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))
                if (poster.genres.isNotEmpty()) Text(poster.genres.joinToString(", ") { it.title }, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
fun CountryErrorScreen(errorMessage: String, onRetry: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("Failed to load content", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 8.dp))
        Text(errorMessage, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 16.dp))
        Button(onClick = onRetry, modifier = Modifier.padding(top = 8.dp)) { Icon(Icons.Default.Refresh, contentDescription = "Retry", modifier = Modifier.size(16.dp)); Spacer(modifier = Modifier.width(8.dp)); Text("Retry") }
    }
}

@Composable
fun CountryErrorItem(errorMessage: String, onRetry: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), shape = RoundedCornerShape(GlassCorners.Card), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Failed to load more content", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.padding(bottom = 8.dp))
            Text(errorMessage, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.padding(bottom = 16.dp))
            Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onErrorContainer, contentColor = MaterialTheme.colorScheme.errorContainer)) { Icon(Icons.Default.Refresh, contentDescription = "Retry", modifier = Modifier.size(16.dp)); Spacer(modifier = Modifier.width(8.dp)); Text("Retry") }
        }
    }
}

package com.pira.ccloud.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import com.pira.ccloud.ui.theme.GlassCorners
import com.pira.ccloud.ui.theme.GlassAlertDialog
import com.pira.ccloud.ui.theme.glassSurface
import com.pira.ccloud.ui.theme.rememberGlassTint
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.pira.ccloud.VideoPlayerActivity
import com.pira.ccloud.components.CopySeasonLinksButton
import com.pira.ccloud.components.DownloadOptionsDialog
import com.pira.ccloud.components.EpisodeCard
import com.pira.ccloud.components.ExpandableText
import com.pira.ccloud.data.model.Episode
import com.pira.ccloud.data.model.FavoriteItem
import com.pira.ccloud.data.model.Season
import com.pira.ccloud.data.model.Series
import com.pira.ccloud.data.model.Source
import com.pira.ccloud.data.model.WatchedEpisode
import com.pira.ccloud.ui.series.SeasonsViewModel
import com.pira.ccloud.utils.DownloadUtils
import com.pira.ccloud.utils.StorageUtils

@Composable
fun SingleSeriesScreen(
    seriesId: Int,
    navController: NavController
) {
    var series by remember { mutableStateOf<Series?>(null) }
    val context = LocalContext.current
    val seasonsViewModel: SeasonsViewModel = viewModel()
    var selectedEpisode by remember { mutableStateOf<Episode?>(null) }
    var showSourceDialog by remember { mutableStateOf(false) }
    var showDownloadMenu by remember { mutableStateOf(false) }
    var downloadSources by remember { mutableStateOf<List<Source>>(emptyList()) }
    var selectedSeasonIndex by remember { mutableStateOf(0) }

    LaunchedEffect(seriesId) {
        series = StorageUtils.loadSeriesFromFile(context, seriesId)
        seasonsViewModel.loadSeasons(seriesId)
        series?.let {
            StorageUtils.saveRecentlyViewed(
                context,
                FavoriteItem(it.id, it.type, it.title, it.description, it.year, it.imdb, it.rating, it.duration, it.image, it.cover, it.genres, it.country)
            )
        }
    }

    // Source selection dialog
    if (showSourceDialog && selectedEpisode != null && series != null) {
        SourceOptionsDialog(
            episode = selectedEpisode!!,
            series = series!!,
            season = seasonsViewModel.seasons.getOrNull(selectedSeasonIndex),
            onDismiss = { showSourceDialog = false },
            onDownload = { source -> showSourceDialog = false; DownloadUtils.openUrl(context, source.url) },
            onPlay = { source ->
                showSourceDialog = false
                val selectedSeason = seasonsViewModel.seasons.getOrNull(selectedSeasonIndex)
                if (selectedSeason != null) {
                    VideoPlayerActivity.startWithEpisodeInfo(context, source.url, series!!.id, selectedSeason.id, selectedEpisode!!.id)
                } else {
                    VideoPlayerActivity.start(context, source.url)
                }
            }
        )
    }

    // Download menu
    if (showDownloadMenu && downloadSources.isNotEmpty()) {
        DownloadMenu(
            sources = downloadSources,
            onDismiss = { showDownloadMenu = false },
            onDownload = { source -> showDownloadMenu = false; DownloadUtils.openUrl(context, source.url) }
        )
    }

    if (series != null) {
        SeriesDetailsContent(
            series = series!!,
            seasonsViewModel = seasonsViewModel,
            onBackClick = { navController.popBackStack() },
            onEpisodeClick = { episode ->
                if (episode.sources.isNotEmpty() && series != null) {
                    if (episode.sources.size > 1) {
                        selectedEpisode = episode; showSourceDialog = true
                    } else {
                        val selectedSeason = seasonsViewModel.seasons.getOrNull(selectedSeasonIndex)
                        if (selectedSeason != null) {
                            VideoPlayerActivity.startWithEpisodeInfo(context, episode.sources[0].url, series!!.id, selectedSeason.id, episode.id)
                        } else {
                            VideoPlayerActivity.start(context, episode.sources[0].url)
                        }
                    }
                }
            },
            onDownloadClick = { episode ->
                if (episode.sources.isNotEmpty()) { downloadSources = episode.sources; showDownloadMenu = true }
            },
            modifier = Modifier.fillMaxSize()
        )
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                Text("Series not found", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(top = 16.dp))
            }
        }
    }
}

@Composable
fun SourceOptionsDialog(
    episode: Episode,
    series: Series,
    season: Season?,
    onDismiss: () -> Unit,
    onDownload: (Source) -> Unit,
    onPlay: (Source) -> Unit
) {
    val context = LocalContext.current
    var selectedSource by remember { mutableStateOf<Source?>(null) }
    var showDownloadOptions by remember { mutableStateOf(false) }

    if (showDownloadOptions && selectedSource != null) {
        DownloadOptionsDialog(
            source = selectedSource!!,
            onDismiss = { showDownloadOptions = false },
            onCopyLink = { DownloadUtils.copyToClipboard(context, selectedSource!!.url) },
            onDownloadWithBrowser = { DownloadUtils.openUrl(context, selectedSource!!.url) },
            onDownloadWithADM = { DownloadUtils.openWithADM(context, selectedSource!!.url) },
            onOpenInVLC = { DownloadUtils.openWithVLC(context, selectedSource!!.url) },
            onOpenInMXPlayer = { DownloadUtils.openWithMXPlayer(context, selectedSource!!.url) },
            onOpenInKMPlayer = { DownloadUtils.openWithKMPlayer(context, selectedSource!!.url) }
        )
    }

    GlassAlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Episode: ${episode.title}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Medium) },
        text = { Text("Choose quality to play", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
        confirmButton = {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                episode.sources.forEach { source ->
                    Button(
                        onClick = { onPlay(source) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(GlassCorners.Button),
                        elevation = ButtonDefaults.elevatedButtonElevation()
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Play", modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Play ${source.quality}")
                    }
                }
                TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(GlassCorners.Button)) { Text("Cancel") }
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(GlassCorners.Dialog),
        tonalElevation = 0.dp
    )
}

@Composable
fun DownloadMenu(
    sources: List<Source>,
    onDismiss: () -> Unit,
    onDownload: (Source) -> Unit
) {
    val context = LocalContext.current
    var selectedSource by remember { mutableStateOf<Source?>(null) }
    var showDownloadOptions by remember { mutableStateOf(false) }

    LaunchedEffect(sources) {
        if (sources.size == 1) { selectedSource = sources[0]; showDownloadOptions = true }
    }

    if (showDownloadOptions && selectedSource != null) {
        DownloadOptionsDialog(
            source = selectedSource!!,
            onDismiss = {
                showDownloadOptions = false
                if (sources.size == 1) onDismiss()
            },
            onCopyLink = { DownloadUtils.copyToClipboard(context, selectedSource!!.url) },
            onDownloadWithBrowser = { DownloadUtils.openUrl(context, selectedSource!!.url) },
            onDownloadWithADM = { DownloadUtils.openWithADM(context, selectedSource!!.url) },
            onOpenInVLC = { DownloadUtils.openWithVLC(context, selectedSource!!.url) },
            onOpenInMXPlayer = { DownloadUtils.openWithMXPlayer(context, selectedSource!!.url) },
            onOpenInKMPlayer = { DownloadUtils.openWithKMPlayer(context, selectedSource!!.url) }
        )
    }

    if (sources.size > 1) {
        GlassAlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Select Quality to Download", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Medium) },
            text = { Text("Choose a quality option for download", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
            confirmButton = {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    sources.forEach { source ->
                        Button(
                            onClick = { selectedSource = source; showDownloadOptions = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(GlassCorners.Button),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer),
                            elevation = ButtonDefaults.elevatedButtonElevation()
                        ) {
                            Icon(Icons.Default.Download, contentDescription = "Download", modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("${source.quality}")
                        }
                    }
                    TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(GlassCorners.Button)) { Text("Cancel") }
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(GlassCorners.Dialog),
            tonalElevation = 0.dp
        )
    }
}

@Composable
fun SeriesDetailsContent(
    series: Series,
    seasonsViewModel: SeasonsViewModel,
    onBackClick: () -> Unit,
    onEpisodeClick: (Episode) -> Unit,
    onDownloadClick: (Episode) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedSeasonIndex by remember { mutableStateOf(0) }
    var showEpisodeImageDialog by remember { mutableStateOf(false) }
    var episodeImageUrl by remember { mutableStateOf("") }

    LazyColumn(modifier = modifier.fillMaxSize()) {
        // ─── Header ──────────────────────────────────────────────
        item {
            Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
                Image(
                    painter = rememberAsyncImagePainter(ImageRequest.Builder(LocalContext.current).data(series.cover).crossfade(true).build()),
                    contentDescription = null, modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface), contentScale = ContentScale.Crop
                )
                Box(modifier = Modifier.fillMaxSize().background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(listOf(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f), MaterialTheme.colorScheme.surface))
                ))
                Row(modifier = Modifier.fillMaxSize().padding(24.dp), verticalAlignment = Alignment.Bottom) {
                    var showImageDialog by remember { mutableStateOf(false) }
                    Image(
                        painter = rememberAsyncImagePainter(ImageRequest.Builder(LocalContext.current).data(series.image).crossfade(true).build()),
                        contentDescription = series.title, modifier = Modifier.height(200.dp).clip(RoundedCornerShape(GlassCorners.Poster)).clickable { showImageDialog = true }, contentScale = ContentScale.Fit
                    )
                    if (showImageDialog) {
                        GlassAlertDialog(
                            onDismissRequest = { showImageDialog = false }, title = { Text("Image Options") }, text = { Text("Choose an action for this image") },
                            confirmButton = { TextButton(onClick = { DownloadUtils.copyToClipboard(context, series.image); showImageDialog = false }) { Text("Copy Image URL") } },
                            dismissButton = { TextButton(onClick = { showImageDialog = false }) { Text("Cancel") } }
                        )
                    }
                    Column(modifier = Modifier.padding(start = 16.dp).weight(1f)) {
                        Text(series.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Medium)
                        val countryText = if (series.country.isNotEmpty()) "${series.country.joinToString(", ") { it.title }} (${series.year})" else "(${series.year})"
                        Text(countryText, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = "Rating", tint = Color.Yellow, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(String.format("%.1f", series.imdb), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Medium)
                        }
                    }
                }
                IconButton(onClick = onBackClick, modifier = Modifier.padding(24.dp).align(Alignment.TopStart)) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface) }
                var isFavorite by remember { mutableStateOf(false) }
                LaunchedEffect(series.id) { isFavorite = StorageUtils.isFavorite(context, series.id, "series") }
                var showRemoveDialog by remember { mutableStateOf(false) }
                if (showRemoveDialog) {
                    GlassAlertDialog(
                        onDismissRequest = { showRemoveDialog = false }, title = { Text("Remove from Favorites") }, text = { Text("Remove this series from your favorites?") },
                        confirmButton = { TextButton(onClick = { StorageUtils.removeFavorite(context, series.id, "series"); isFavorite = false; showRemoveDialog = false; android.widget.Toast.makeText(context, "Removed from favorites", android.widget.Toast.LENGTH_SHORT).show() }) { Text("Remove") } },
                        dismissButton = { TextButton(onClick = { showRemoveDialog = false }) { Text("Cancel") } }
                    )
                }
                IconButton(
                    onClick = {
                        if (isFavorite) showRemoveDialog = true
                        else {
                            StorageUtils.saveFavorite(context, FavoriteItem(series.id, "series", series.title, series.description, series.year, series.imdb, series.rating, series.duration, series.image, series.cover, series.genres, series.country))
                            isFavorite = true
                            android.widget.Toast.makeText(context, "Added to favorites", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    }, modifier = Modifier.padding(24.dp).align(Alignment.TopEnd)
                ) { Icon(if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder, contentDescription = "Favorite", tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface) }
            }
        }

        // ─── Genres ──────────────────────────────────────────────
        item {
            if (series.genres.isNotEmpty()) {
                Text("Genres", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(start = 24.dp, top = 16.dp, end = 24.dp))
                LazyRow(modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 8.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(series.genres) { genre ->
                        val genreChipGlassTint = rememberGlassTint()
                        Card(colors = CardDefaults.cardColors(containerColor = Color.Transparent), shape = RoundedCornerShape(GlassCorners.Tag), modifier = Modifier.height(32.dp).glassSurface(shape = RoundedCornerShape(GlassCorners.Tag), tint = genreChipGlassTint)) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp)) { Text(genre.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, maxLines = 1) }
                        }
                    }
                }
            }
        }

        // ─── Description ─────────────────────────────────────────
        item { Text("Description", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(start = 24.dp, top = 16.dp, end = 24.dp)) }
        item { CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) { ExpandableText(text = series.description, modifier = Modifier.padding(start = 24.dp, end = 24.dp).fillMaxWidth()) } }

        // ─── Seasons selector ────────────────────────────────────
        if (seasonsViewModel.seasons.isNotEmpty()) {
            item { Text("Seasons", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(start = 24.dp, top = 16.dp, end = 24.dp)) }
            item {
                LazyRow(modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 8.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(seasonsViewModel.seasons.size) { index ->
                        val season = seasonsViewModel.seasons[index]
                        val seasonChipGlassTint = rememberGlassTint()
                        Card(
                            modifier = Modifier.clickable { selectedSeasonIndex = index }
                                .then(if (selectedSeasonIndex != index) Modifier.glassSurface(shape = RoundedCornerShape(GlassCorners.Button), tint = seasonChipGlassTint) else Modifier),
                            colors = CardDefaults.cardColors(containerColor = if (selectedSeasonIndex == index) MaterialTheme.colorScheme.primary else Color.Transparent),
                            shape = RoundedCornerShape(GlassCorners.Button)
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                                Text(season.title, style = MaterialTheme.typography.bodyMedium, color = if (selectedSeasonIndex == index) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }
        }

        // ─── Episodes ────────────────────────────────────────────
        item {
            if (seasonsViewModel.isLoading) {
                Text("Loading seasons…", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(24.dp))
            } else if (seasonsViewModel.errorMessage != null) {
                Column(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Error loading seasons: ${seasonsViewModel.errorMessage}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 16.dp))
                    Button(onClick = { seasonsViewModel.loadSeasons(series.id) }) { Text("Retry") }
                }
            } else if (seasonsViewModel.seasons.isNotEmpty()) {
                val selectedSeason = seasonsViewModel.seasons[selectedSeasonIndex]
                Column {
                    Row(modifier = Modifier.fillMaxWidth().padding(start = 24.dp, top = 16.dp, end = 24.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(selectedSeason.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f))
                        if (selectedSeason.episodes.any { it.sources.isNotEmpty() }) { CopySeasonLinksButton(episodes = selectedSeason.episodes) }
                    }
                    selectedSeason.episodes.forEach { episode ->
                        val isWatched = StorageUtils.isEpisodeWatched(context, series.id, selectedSeason.id, episode.id)
                        EpisodeCard(
                            episode = episode, isWatched = isWatched,
                            onPlayClick = { onEpisodeClick(episode) },
                            onDownloadClick = { onDownloadClick(episode) },
                            onImageClick = { episodeImageUrl = it; showEpisodeImageDialog = true }
                        )
                    }
                }
            } else {
                Text("No seasons available", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(24.dp))
            }
        }

        // ─── Image Dialog ────────────────────────────────────────
        if (showEpisodeImageDialog) {
            item {
                GlassAlertDialog(
                    onDismissRequest = { showEpisodeImageDialog = false },
                    title = { Text("Episode Image") },
                    text = { Text("Choose an action for this image") },
                    confirmButton = {
                        TextButton(onClick = { DownloadUtils.copyToClipboard(context, episodeImageUrl); showEpisodeImageDialog = false }) { Text("Copy Image URL") }
                    },
                    dismissButton = { TextButton(onClick = { showEpisodeImageDialog = false }) { Text("Cancel") } }
                )
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

fun openUrlSeries(context: Context, url: String) { DownloadUtils.openUrl(context, url) }

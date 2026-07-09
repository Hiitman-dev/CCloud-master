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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
<<<<<<< HEAD
=======
import com.pira.ccloud.ui.theme.glassSurface
import com.pira.ccloud.ui.theme.matteOverlay
import com.pira.ccloud.ui.theme.rememberGlassTint
import androidx.compose.material3.Scaffold
>>>>>>> 16bb46ea3318e8f7e2ba73e2f974008e3b01c44d
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
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.pira.ccloud.VideoPlayerActivity
import com.pira.ccloud.components.DownloadOptionsDialog
import com.pira.ccloud.components.CopyLinksButton
import com.pira.ccloud.components.ExpandableText
import com.pira.ccloud.data.model.FavoriteItem
import com.pira.ccloud.data.model.Movie
import com.pira.ccloud.data.model.Source
import com.pira.ccloud.utils.DownloadUtils
import com.pira.ccloud.utils.StorageUtils

@Composable
fun SingleMovieScreen(
    movieId: Int,
    navController: NavController
) {
    var movie by remember { mutableStateOf<Movie?>(null) }
    val context = LocalContext.current

    LaunchedEffect(movieId) {
        movie = StorageUtils.loadMovieFromFile(context, movieId)
    }

    if (movie != null) {
        LaunchedEffect(movieId) {
            StorageUtils.saveRecentlyViewed(
                context,
                FavoriteItem(
                    movie!!.id, movie!!.type, movie!!.title, movie!!.description,
                    movie!!.year, movie!!.imdb, movie!!.rating, movie!!.duration,
                    movie!!.image, movie!!.cover, movie!!.genres, movie!!.country,
                    movie!!.sources
                )
            )
        }
        MovieDetailsContent(
            movie = movie!!,
            onBackClick = { navController.popBackStack() },
<<<<<<< HEAD
            onPlayClick = { source -> VideoPlayerActivity.start(context, source.url) },
            modifier = Modifier.fillMaxSize()
=======
            onPlayClick = { source ->
                // Launch video player activity
                VideoPlayerActivity.start(context, source.url)
            },
            // Apply matte overlay effect for the entire screen
            modifier = Modifier
                .fillMaxSize()
                .matteOverlay()
>>>>>>> 16bb46ea3318e8f7e2ba73e2f974008e3b01c44d
        )
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = "Movie not found",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}

@Composable
fun MovieDetailsContent(
    movie: Movie,
    onBackClick: () -> Unit,
    onPlayClick: (Source) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedSource by remember { mutableStateOf<Source?>(null) }
    var showSourceDialog by remember { mutableStateOf(false) }

    if (showSourceDialog && selectedSource != null) {
        SourceOptionsDialog(
            source = selectedSource!!,
            onDismiss = { showSourceDialog = false },
            onPlay = { source -> showSourceDialog = false; onPlayClick(source) }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ─── Movie Header ────────────────────────────────────────
        Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
            // Background cover image
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current).data(movie.cover).crossfade(true).build()
                ),
                contentDescription = null,
                modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface),
                contentScale = ContentScale.Crop
            )
            // Gradient overlay
            Box(
                modifier = Modifier.fillMaxSize().background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
            )
            // Poster + details row
            Row(modifier = Modifier.fillMaxSize().padding(24.dp), verticalAlignment = Alignment.Bottom) {
                var showImageDialog by remember { mutableStateOf(false) }
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current).data(movie.image).crossfade(true).build()
                    ),
                    contentDescription = movie.title,
                    modifier = Modifier.height(200.dp)
                        .clip(RoundedCornerShape(GlassCorners.Poster))
                        .clickable { showImageDialog = true },
                    contentScale = ContentScale.Fit
                )
                if (showImageDialog) {
                    GlassAlertDialog(
                        onDismissRequest = { showImageDialog = false },
                        title = { Text("Image Options") },
                        text = { Text("Choose an action for this image") },
                        confirmButton = {
                            TextButton(onClick = { DownloadUtils.copyToClipboard(context, movie.image); showImageDialog = false }) { Text("Copy Image URL") }
                        },
                        dismissButton = { TextButton(onClick = { showImageDialog = false }) { Text("Cancel") } }
                    )
                }
                Column(modifier = Modifier.padding(start = 16.dp).weight(1f)) {
                    Text(movie.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                    val countryText = if (movie.country.isNotEmpty()) "${movie.country.joinToString(", ") { it.title }} (${movie.year})" else "(${movie.year})"
                    Text(countryText, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = "Rating", tint = Color.Yellow, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(String.format("%.1f", movie.imdb), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
            // Back button
            IconButton(onClick = onBackClick, modifier = Modifier.padding(24.dp).align(Alignment.TopStart)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
            }
            // Favorite button
            var isFavorite by remember { mutableStateOf(false) }
            val movieId = movie.id
            LaunchedEffect(movieId) { isFavorite = StorageUtils.isFavorite(context, movieId, "movie") }
            var showRemoveFavoriteDialog by remember { mutableStateOf(false) }
            if (showRemoveFavoriteDialog) {
                GlassAlertDialog(
                    onDismissRequest = { showRemoveFavoriteDialog = false },
                    title = { Text("Remove from Favorites") },
                    text = { Text("Are you sure you want to remove this movie from your favorites?") },
                    confirmButton = {
                        TextButton(onClick = {
                            StorageUtils.removeFavorite(context, movieId, "movie")
                            isFavorite = false; showRemoveFavoriteDialog = false
                            android.widget.Toast.makeText(context, "Removed from favorites", android.widget.Toast.LENGTH_SHORT).show()
                        }) { Text("Remove") }
                    },
                    dismissButton = { TextButton(onClick = { showRemoveFavoriteDialog = false }) { Text("Cancel") } }
                )
            }
            IconButton(
                onClick = {
                    if (isFavorite) { showRemoveFavoriteDialog = true }
                    else {
                        StorageUtils.saveFavorite(
                            context,
                            FavoriteItem(movie.id, "movie", movie.title, movie.description, movie.year, movie.imdb, movie.rating, movie.duration, movie.image, movie.cover, movie.genres, movie.country, movie.sources)
                        )
                        isFavorite = true
                        android.widget.Toast.makeText(context, "Added to favorites", android.widget.Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.padding(24.dp).align(Alignment.TopEnd)
            ) {
                Icon(
                    if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // ─── Genres ──────────────────────────────────────────────
        if (movie.genres.isNotEmpty()) {
            Text("Genres", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(start = 24.dp, top = 16.dp, end = 24.dp))
            LazyRow(modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 8.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(movie.genres) { genre ->
                    val genreChipGlassTint = rememberGlassTint()
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                        shape = RoundedCornerShape(GlassCorners.Tag),
                        modifier = Modifier.height(32.dp).glassSurface(shape = RoundedCornerShape(GlassCorners.Tag), tint = genreChipGlassTint)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp)) {
                            Text(genre.title, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium, maxLines = 1)
                        }
                    }
                }
            }
        }

        // ─── Description ─────────────────────────────────────────
        Text("Description", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(start = 24.dp, top = 16.dp, end = 24.dp))
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            ExpandableText(text = movie.description, modifier = Modifier.padding(start = 24.dp, end = 24.dp).fillMaxWidth())
        }

        // ─── Sources ─────────────────────────────────────────────
        if (movie.sources.isNotEmpty()) {
            Row(modifier = Modifier.fillMaxWidth().padding(start = 24.dp, top = 16.dp, end = 24.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Available Qualities", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f))
                CopyLinksButton(sources = movie.sources)
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 8.dp)) {
                val qualityGlassTint = rememberGlassTint()
                movie.sources.forEach { source ->
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .glassSurface(shape = RoundedCornerShape(GlassCorners.Button), tint = qualityGlassTint)
                            .clickable { selectedSource = source; showSourceDialog = true }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(source.quality, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                        Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun SourceOptionsDialog(
    source: Source,
    onDismiss: () -> Unit,
    onPlay: (Source) -> Unit
) {
    val context = LocalContext.current
    var showDownloadOptions by remember { mutableStateOf(false) }

    if (showDownloadOptions) {
        DownloadOptionsDialog(
            source = source,
            onDismiss = { showDownloadOptions = false },
            onCopyLink = { DownloadUtils.copyToClipboard(context, source.url) },
            onDownloadWithBrowser = { DownloadUtils.openUrl(context, source.url) },
            onDownloadWithADM = { DownloadUtils.openWithADM(context, source.url) },
            onOpenInVLC = { DownloadUtils.openWithVLC(context, source.url) },
            onOpenInMXPlayer = { DownloadUtils.openWithMXPlayer(context, source.url) },
            onOpenInKMPlayer = { DownloadUtils.openWithKMPlayer(context, source.url) }
        )
    }

    GlassAlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(source.quality, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Medium)
        },
        text = {
            Text("Choose an action for this video quality", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        },
        confirmButton = {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { showDownloadOptions = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(GlassCorners.Button),
                    elevation = ButtonDefaults.elevatedButtonElevation()
                ) {
                    Icon(Icons.Default.Download, contentDescription = "Download", modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp)); Text("Download Options")
                }
                Button(
                    onClick = { onPlay(source) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(GlassCorners.Button),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer),
                    elevation = ButtonDefaults.elevatedButtonElevation()
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Play", modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp)); Text("Play in CCloud")
                }
                TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(GlassCorners.Button)) { Text("Cancel") }
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(GlassCorners.Dialog),
        tonalElevation = 0.dp
    )
}

fun openUrl(context: Context, url: String) { DownloadUtils.openUrl(context, url) }

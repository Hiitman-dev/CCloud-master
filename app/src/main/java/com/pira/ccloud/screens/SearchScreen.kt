package com.pira.ccloud.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import com.pira.ccloud.ui.theme.glassSurface
import com.pira.ccloud.ui.theme.subtleGlassSurface
import com.pira.ccloud.ui.theme.rememberGlassTint
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.pira.ccloud.components.HomeAmbientBackground
import com.pira.ccloud.data.model.Country
import com.pira.ccloud.data.model.Poster
import com.pira.ccloud.ui.search.SearchViewModel
import com.pira.ccloud.utils.DeviceUtils
import com.pira.ccloud.utils.StorageUtils

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = viewModel(),
    navController: NavController? = null
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    val recentSearchesPrefs = remember {
        context.getSharedPreferences("ccloud_recent_searches", Context.MODE_PRIVATE)
    }
    var recentSearches by remember {
        mutableStateOf(loadRecentSearches(recentSearchesPrefs))
    }

    fun performSearch(query: String) {
        if (query.isNotEmpty()) {
            recentSearches = addRecentSearch(recentSearchesPrefs, recentSearches, query)
            viewModel.updateSearchQuery(query)
            viewModel.triggerSearch()
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        HomeAmbientBackground(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // ── Back button + Search bar row ─────────────────────────
            val searchGlassTint = rememberGlassTint()

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Back arrow — frosted glass circle
                IconButton(
                    onClick = { navController?.popBackStack() },
                    modifier = Modifier
                        .size(44.dp)
                        .glassSurface(shape = CircleShape, tint = searchGlassTint, tintAlpha = 0.4f, borderAlpha = 0.28f)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Search field
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .glassSurface(
                            shape = RoundedCornerShape(24.dp),
                            tint = searchGlassTint,
                            tintAlpha = 0.45f,
                            borderAlpha = 0.32f
                        )
                ) {
                    TextField(
                        value = viewModel.searchQuery,
                        onValueChange = { viewModel.updateSearchQuery(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                            .clickable { focusRequester.requestFocus() },
                        placeholder = {
                            Text(
                                text = "Search movies & series...",
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        },
                        leadingIcon = {
                            IconButton(onClick = {
                                if (viewModel.searchQuery.isNotEmpty()) {
                                    performSearch(viewModel.searchQuery)
                                    focusManager.clearFocus()
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        trailingIcon = {
                            if (viewModel.searchQuery.isNotEmpty()) {
                                IconButton(onClick = {
                                    viewModel.clearSearch()
                                    focusManager.clearFocus()
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                if (viewModel.searchQuery.isNotEmpty()) {
                                    performSearch(viewModel.searchQuery)
                                }
                                focusManager.clearFocus()
                            }
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                        ),
                        shape = RoundedCornerShape(24.dp),
                        singleLine = true
                    )
                }
            }

            // ── Country stories row (pre-search only) ─────────────────
            if (!viewModel.hasSearched) {
                if (viewModel.isCountriesLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                } else if (viewModel.countries.isNotEmpty()) {
                    // Section title
                    Text(
                        text = "Browse by Country",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
                    )
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(viewModel.countries) { country ->
                            CountryStoryItem(
                                country = country,
                                onClick = { navController?.navigate("country/${country.id}") }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Search results / empty / loading states ───────────────
            when {
                viewModel.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        SearchLoadingIndicator()
                    }
                }
                viewModel.errorMessage != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .glassSurface(
                                    shape = RoundedCornerShape(28.dp),
                                    tint = searchGlassTint,
                                    tintAlpha = 0.35f,
                                    borderAlpha = 0.25f
                                )
                                .padding(32.dp)
                        ) {
                            Text(
                                text = "Something went wrong",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = viewModel.errorMessage ?: "Unknown error",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.triggerSearch() }) {
                                Text("Retry")
                            }
                        }
                    }
                }
                viewModel.searchResults.isNotEmpty() -> {
                    SearchResultsGrid(
                        posters = viewModel.searchResults,
                        navController = navController,
                        context = context
                    )
                }
                viewModel.hasSearched && viewModel.searchQuery.isNotEmpty() && !viewModel.isLoading -> {
                    // No results — glass card
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .glassSurface(
                                    shape = RoundedCornerShape(28.dp),
                                    tint = searchGlassTint,
                                    tintAlpha = 0.35f,
                                    borderAlpha = 0.25f
                                )
                                .padding(horizontal = 40.dp, vertical = 32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No results found",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Try a different keyword",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                else -> {
                    // ── Idle state ─────────────────────────────────────
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Recent searches
                        if (viewModel.searchQuery.isEmpty() && recentSearches.isNotEmpty()) {
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Recent Searches",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    androidx.compose.material3.TextButton(
                                        onClick = {
                                            recentSearches = clearRecentSearches(recentSearchesPrefs)
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Clear history",
                                            modifier = Modifier.size(16.dp),
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Clear",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    contentPadding = PaddingValues(bottom = 8.dp)
                                ) {
                                    items(recentSearches) { recentQuery ->
                                        SuggestionChip(
                                            onClick = { performSearch(recentQuery) },
                                            label = { Text(recentQuery) },
                                            icon = {
                                                Icon(
                                                    imageVector = Icons.Default.History,
                                                    contentDescription = "Recent search",
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            },
                                            shape = RoundedCornerShape(14.dp),
                                            colors = SuggestionChipDefaults.suggestionChipColors(
                                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                                iconContentColor = MaterialTheme.colorScheme.primary
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        // Hero empty state
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .glassSurface(
                                        shape = RoundedCornerShape(32.dp),
                                        tint = searchGlassTint,
                                        tintAlpha = 0.22f,
                                        borderAlpha = 0.18f
                                    )
                                    .padding(horizontal = 48.dp, vertical = 40.dp)
                            ) {
                                // Glowing search icon
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .background(
                                            brush = Brush.radialGradient(
                                                colors = listOf(
                                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.0f)
                                                ),
                                                radius = 120f
                                            ),
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Search",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(20.dp))
                                Text(
                                    text = "Discover Content",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Search across movies, series,\nand explore by country",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── Loading indicator ──────────────────────────────────────────────────────

@Composable
private fun SearchLoadingIndicator() {
    val transition = rememberInfiniteTransition(label = "search_loading")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing)
        ),
        label = "progress"
    )
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing)
        ),
        label = "rotation"
    )
    CircularProgressIndicator(
        progress = { progress },
        modifier = Modifier
            .size(48.dp)
            .rotate(rotation),
        strokeWidth = 4.dp,
        color = MaterialTheme.colorScheme.primary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant
    )
}

// ── Country story item (frosted circle) ───────────────────────────────────

@Composable
fun CountryStoryItem(
    country: Country,
    onClick: () -> Unit
) {
    val tint = rememberGlassTint()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .glassSurface(shape = CircleShape, tint = tint, tintAlpha = 0.3f, borderAlpha = 0.22f),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .data(country.image)
                        .crossfade(true)
                        .build()
                ),
                contentDescription = country.title,
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = country.title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(72.dp),
            textAlign = TextAlign.Center
        )
    }
}

// ── Recent searches history helpers (SharedPreferences-backed) ─────────────

private const val RECENT_SEARCHES_KEY = "recent_queries"
private const val MAX_RECENT_SEARCHES = 5

private fun loadRecentSearches(prefs: android.content.SharedPreferences): List<String> {
    val raw = prefs.getString(RECENT_SEARCHES_KEY, null) ?: return emptyList()
    return try {
        val array = org.json.JSONArray(raw)
        (0 until array.length()).map { array.getString(it) }
    } catch (e: Exception) {
        emptyList()
    }
}

private fun addRecentSearch(
    prefs: android.content.SharedPreferences,
    current: List<String>,
    query: String
): List<String> {
    val trimmedQuery = query.trim()
    if (trimmedQuery.isEmpty()) return current

    val updated = listOf(trimmedQuery) + current.filterNot { it.equals(trimmedQuery, ignoreCase = true) }
    val capped = updated.take(MAX_RECENT_SEARCHES)

    val jsonArray = org.json.JSONArray()
    capped.forEach { jsonArray.put(it) }
    prefs.edit().putString(RECENT_SEARCHES_KEY, jsonArray.toString()).apply()

    return capped
}

private fun clearRecentSearches(prefs: android.content.SharedPreferences): List<String> {
    prefs.edit().remove(RECENT_SEARCHES_KEY).apply()
    return emptyList()
}

// ── Search results grid ────────────────────────────────────────────────────

@Composable
fun SearchResultsGrid(
    posters: List<Poster>,
    navController: NavController?,
    context: Context
) {
    val columns = DeviceUtils.getGridColumns(LocalContext.current.resources)
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(0.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(posters) { poster ->
            PosterItem(
                poster = poster,
                onClick = {
                    if (poster.isMovie()) {
                        StorageUtils.saveMovieToFile(context, poster.toMovie())
                        navController?.navigate("single_movie/${poster.id}")
                    } else if (poster.isSeries()) {
                        StorageUtils.saveSeriesToFile(context, poster.toSeries())
                        navController?.navigate("single_series/${poster.id}")
                    }
                }
            )
        }
    }
}

@Composable
fun PosterItem(
    poster: Poster,
    onClick: () -> Unit
) {
    val posterCardGlassTint = rememberGlassTint()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(310.dp)
            .subtleGlassSurface(shape = RoundedCornerShape(20.dp), tint = posterCardGlassTint)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Box {
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(poster.image)
                            .crossfade(true)
                            .build()
                    ),
                    contentDescription = poster.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

                // Rating badge
                Card(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp),
                    shape = RoundedCornerShape(50.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Black.copy(alpha = 0.7f)
                    )
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
                            text = String.format("%.1f", poster.imdb),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Type badge
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp),
                    shape = RoundedCornerShape(50.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (poster.isMovie())
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Box(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                        Text(
                            text = if (poster.isMovie()) "Movie" else "Series",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (poster.isMovie())
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.onSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = poster.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = poster.year.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (poster.genres.isNotEmpty()) {
                    Text(
                        text = poster.genres.joinToString(", ") { it.title },
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

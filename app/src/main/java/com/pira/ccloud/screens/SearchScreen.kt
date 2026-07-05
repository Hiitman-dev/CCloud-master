package com.pira.ccloud.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import com.pira.ccloud.ui.theme.glassSurface
import com.pira.ccloud.ui.theme.glassBackdrop
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.pira.ccloud.R
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

    // FEATURE: Recent Searches History - lightweight persistence via SharedPreferences.
    // Stored as an ordered JSON array, capped at 5 unique, most-recent-first entries.
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
    
    // Request focus when the screen is first displayed to ensure keyboard opens on TV
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .glassBackdrop()
    ) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        // Search bar
        val searchGlassTint = rememberGlassTint()
        TextField(
            value = viewModel.searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .glassSurface(
                    shape = RoundedCornerShape(24.dp),
                    tint = searchGlassTint
                )
                .clickable { 
                    // Ensure keyboard opens when clicking on the TextField on TV
                    focusRequester.requestFocus()
                },
            placeholder = { 
                Text(
                    text = "Search movies and series...",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ) 
            },
            leadingIcon = {
                IconButton(onClick = { 
                    // Trigger search when clicking search icon
                    if (viewModel.searchQuery.isNotEmpty()) {
                        performSearch(viewModel.searchQuery)
                        focusManager.clearFocus()
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            trailingIcon = {
                if (viewModel.searchQuery.isNotEmpty()) {
                    IconButton(onClick = { 
                        viewModel.clearSearch()
                        focusManager.clearFocus() // Dismiss keyboard when clearing search
                    }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    // Trigger search when pressing Enter
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
        
        // Country stories section - only visible when no search has been performed
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
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(viewModel.countries) { country ->
                        CountryStoryItem(
                            country = country,
                            onClick = {
                                // Navigate to country screen
                                navController?.navigate("country/${country.id}")
                            }
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Search results
        when {
            viewModel.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            viewModel.errorMessage != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error: ${viewModel.errorMessage}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Please try again",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.triggerSearch() },
                            modifier = Modifier.padding(16.dp)
                        ) {
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
            // Only show "No results found" after a search has been performed
            viewModel.hasSearched && viewModel.searchQuery.isNotEmpty() && !viewModel.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No results found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            else -> {
                // Initial / empty state - show recent search chips (if any) and instructions
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (viewModel.searchQuery.isEmpty() && recentSearches.isNotEmpty()) {
                        val recentDrawerTint = rememberGlassTint()
                        androidx.compose.animation.AnimatedVisibility(
                            visible = true,
                            enter = androidx.compose.animation.slideInVertically(
                                initialOffsetY = { -it / 3 },
                                animationSpec = tween(280)
                            ) + fadeIn(animationSpec = tween(280))
                        ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                                .glassSurface(shape = RoundedCornerShape(20.dp), tint = recentDrawerTint)
                                .padding(12.dp)
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
                                        shape = RoundedCornerShape(12.dp),
                                        colors = SuggestionChipDefaults.suggestionChipColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                            iconContentColor = MaterialTheme.colorScheme.primary
                                        )
                                    )
                                }
                            }
                        }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Search for movies and series",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Enter a keyword to start searching",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
    }
}

// --- Recent Searches History helpers (SharedPreferences-backed) ---

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

@Composable
fun CountryStoryItem(
    country: com.pira.ccloud.data.model.Country,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
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
                    .size(56.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = country.title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(70.dp),
            textAlign = TextAlign.Center
        )
    }
}

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
                        // Save movie to storage and navigate to single movie screen
                        StorageUtils.saveMovieToFile(context, poster.toMovie())
                        navController?.navigate("single_movie/${poster.id}")
                    } else if (poster.isSeries()) {
                        // Save series to storage and navigate to single series screen
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
            .height(310.dp) // Fixed height for all cards
            .glassSurface(shape = RoundedCornerShape(12.dp), tint = posterCardGlassTint)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // Poster image with rating overlay
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
                
                // Rating overlay at top-right corner
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
                
                // Type indicator at bottom-right corner
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp),
                    shape = RoundedCornerShape(50.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (poster.isMovie()) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.secondary
                        }
                    )
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (poster.isMovie()) "Movie" else "Series",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (poster.isMovie()) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSecondary
                            },
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Poster details with weight to fill remaining space
            Column(
                modifier = Modifier.weight(1f)
            ) {
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
                
                // Genres
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
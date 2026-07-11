package com.pira.ccloud.screens

import android.content.Context
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import com.pira.ccloud.ui.theme.GlassCorners
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
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
    val recentSearchesPrefs = remember { context.getSharedPreferences("ccloud_recent_searches", Context.MODE_PRIVATE) }
    var recentSearches by remember { mutableStateOf(loadRecentSearches(recentSearchesPrefs)) }

    fun performSearch(query: String) {
        if (query.isNotEmpty()) {
            recentSearches = addRecentSearch(recentSearchesPrefs, recentSearches, query)
            viewModel.updateSearchQuery(query)
            viewModel.triggerSearch()
        }
    }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        val searchGlassTint = rememberGlassTint()
        TextField(
            value = viewModel.searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .glassSurface(shape = RoundedCornerShape(GlassCorners.Search), tint = searchGlassTint)
                .clickable { focusRequester.requestFocus() },
            placeholder = { Text("Search movies and series…", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            leadingIcon = {
                IconButton(onClick = {
                    if (viewModel.searchQuery.isNotEmpty()) { performSearch(viewModel.searchQuery); focusManager.clearFocus() }
                }) { Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.onSurfaceVariant) }
            },
            trailingIcon = {
                if (viewModel.searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.clearSearch(); focusManager.clearFocus() }) { Icon(Icons.Default.Clear, contentDescription = "Clear", tint = MaterialTheme.colorScheme.onSurfaceVariant) }
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { if (viewModel.searchQuery.isNotEmpty()) performSearch(viewModel.searchQuery); focusManager.clearFocus() }),
            colors = TextFieldDefaults.colors(focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent, disabledIndicatorColor = Color.Transparent, focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent),
            shape = RoundedCornerShape(GlassCorners.Search),
            singleLine = true
        )

        if (!viewModel.hasSearched) {
            if (viewModel.isCountriesLoading) {
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.primary)
                }
            } else if (viewModel.countries.isNotEmpty()) {
                LazyRow(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(viewModel.countries) { country ->
                        CountryStoryItem(country = country, onClick = { navController?.navigate("country/${country.id}") })
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when {
            viewModel.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp), color = MaterialTheme.colorScheme.primary)
                }
            }
            viewModel.errorMessage != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Error: ${viewModel.errorMessage}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Please try again", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.triggerSearch() }, modifier = Modifier.padding(24.dp)) { Text("Retry") }
                    }
                }
            }
            viewModel.searchResults.isNotEmpty() -> {
                SearchResultsGrid(viewModel.searchResults, navController, context)
            }
            viewModel.hasSearched && viewModel.searchQuery.isNotEmpty() && !viewModel.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No results found", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            else -> {
                Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                    if (viewModel.searchQuery.isEmpty() && recentSearches.isNotEmpty()) {
                        Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("Recent Searches", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary)
                                androidx.compose.material3.TextButton(onClick = { recentSearches = clearRecentSearches(recentSearchesPrefs) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Clear history", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Clear", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.error)
                                }
                            }
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(bottom = 8.dp)) {
                                items(recentSearches) { recentQuery ->
                                    SuggestionChip(onClick = { performSearch(recentQuery) }, label = { Text(recentQuery) }, icon = { Icon(Icons.Default.History, contentDescription = "Recent", modifier = Modifier.size(16.dp)) }, shape = RoundedCornerShape(GlassCorners.Tag), colors = SuggestionChipDefaults.suggestionChipColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, labelColor = MaterialTheme.colorScheme.onSurfaceVariant, iconContentColor = MaterialTheme.colorScheme.primary))
                                }
                            }
                        }
                    }
                    Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Search for movies and series", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Enter a keyword to start searching", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

private const val RECENT_SEARCHES_KEY = "recent_queries"
private const val MAX_RECENT_SEARCHES = 5

private fun loadRecentSearches(prefs: android.content.SharedPreferences): List<String> {
    val raw = prefs.getString(RECENT_SEARCHES_KEY, null) ?: return emptyList()
    return try { val array = org.json.JSONArray(raw); (0 until array.length()).map { array.getString(it) } } catch (e: Exception) { emptyList() }
}

private fun addRecentSearch(prefs: android.content.SharedPreferences, current: List<String>, query: String): List<String> {
    val trimmed = query.trim(); if (trimmed.isEmpty()) return current
    val updated = listOf(trimmed) + current.filterNot { it.equals(trimmed, ignoreCase = true) }
    val capped = updated.take(MAX_RECENT_SEARCHES)
    val jsonArray = org.json.JSONArray(); capped.forEach { jsonArray.put(it) }
    prefs.edit().putString(RECENT_SEARCHES_KEY, jsonArray.toString()).apply(); return capped
}

private fun clearRecentSearches(prefs: android.content.SharedPreferences): List<String> { prefs.edit().remove(RECENT_SEARCHES_KEY).apply(); return emptyList() }

@Composable
fun CountryStoryItem(country: Country, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }) {
        Box(modifier = Modifier.size(60.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) {
            Image(painter = rememberAsyncImagePainter(ImageRequest.Builder(LocalContext.current).data(country.image).crossfade(true).build()), contentDescription = country.title, modifier = Modifier.size(56.dp).clip(CircleShape), contentScale = ContentScale.Crop)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(country.title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.width(70.dp), textAlign = TextAlign.Center)
    }
}

@Composable
fun SearchResultsGrid(posters: List<Poster>, navController: NavController?, context: Context) {
    val columns = DeviceUtils.getGridColumns(LocalContext.current.resources)
    LazyVerticalGrid(columns = GridCells.Fixed(columns), modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(0.dp), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        items(posters) { poster ->
            PosterItem(poster = poster, onClick = {
                if (poster.isMovie()) { StorageUtils.saveMovieToFile(context, poster.toMovie()); navController?.navigate("single_movie/${poster.id}") }
                else if (poster.isSeries()) { StorageUtils.saveSeriesToFile(context, poster.toSeries()); navController?.navigate("single_series/${poster.id}") }
            })
        }
    }
}

@Composable
fun PosterItem(poster: Poster, onClick: () -> Unit) {
    val tint = rememberGlassTint()
    Card(modifier = Modifier.fillMaxWidth().height(310.dp).subtleGlassSurface(shape = RoundedCornerShape(GlassCorners.Card), tint = tint).clickable { onClick() }, shape = RoundedCornerShape(GlassCorners.Card), elevation = CardDefaults.cardElevation(0.dp), colors = CardDefaults.cardColors(containerColor = Color.Transparent)) {
        Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            Box {
                Image(painter = rememberAsyncImagePainter(ImageRequest.Builder(LocalContext.current).data(poster.image).crossfade(true).build()), contentDescription = poster.title, modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(GlassCorners.Poster)), contentScale = ContentScale.Crop)
                Card(modifier = Modifier.align(Alignment.TopEnd).padding(8.dp), shape = RoundedCornerShape(GlassCorners.Tag), colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.7f))) {
                    Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = "Rating", tint = Color.Yellow, modifier = Modifier.size(14.dp)); Spacer(modifier = Modifier.width(4.dp)); Text(String.format("%.1f", poster.imdb), style = MaterialTheme.typography.bodySmall, color = Color.White, fontWeight = FontWeight.Medium)
                    }
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

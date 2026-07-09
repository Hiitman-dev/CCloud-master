package com.pira.ccloud.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pira.ccloud.data.model.FilterType
import com.pira.ccloud.data.model.Genre
import com.pira.ccloud.ui.theme.GlassCorners
<<<<<<< HEAD
import com.pira.ccloud.ui.theme.GlassIntensity
import com.pira.ccloud.ui.theme.GlassIconButton
import com.pira.ccloud.ui.theme.liquidGlass

/**
 * Floating Liquid Glass filter bar.
 *
 * Design:
 *  - Floats above scrollable content (not a static header)
 *  - Rounded pill / rounded-2xl shape with glassmorphism material
 *  - Search icon button on the left, filter trigger on the right
 *  - Content scrolls underneath and remains visible through the blur
 *  - Smooth expand animation on filter sheet
 *  - Same glass material as nav bar but slightly different opacity
=======
import com.pira.ccloud.ui.theme.glassSurface
import com.pira.ccloud.ui.theme.rememberGlassTint

/**
 * Compact "Filters" trigger. Tapping it raises a glass-styled bottom sheet
 * popup with the sort type and genre pickers.
>>>>>>> 03d9d8ea365ac7c4ed6ac59077927b3f93b49314
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenreFilterSection(
    genres: List<Genre>,
    selectedGenreId: Int,
    selectedFilterType: FilterType,
    onGenreSelected: (Int) -> Unit,
    onFilterTypeSelected: (FilterType) -> Unit,
    onSearchClick: (() -> Unit)? = null
) {
    var showFilterSheet by remember { mutableStateOf(false) }
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

    val selectedGenreTitle = if (selectedGenreId == 0) {
        "All Genres"
    } else {
        genres.find { it.id == selectedGenreId }?.title ?: "All Genres"
    }
    val filterLabel = when (selectedFilterType) {
        FilterType.DEFAULT -> "Default"
        FilterType.BY_YEAR -> "By Year"
        FilterType.BY_IMDB -> "By IMDB"
    }

    // Floating glass filter bar — positioned above content
    Box(
        modifier = Modifier
            .fillMaxWidth()
<<<<<<< HEAD
            .padding(horizontal = 16.dp, vertical = 8.dp)
=======
<<<<<<< HEAD
            .padding(start = 24.dp, end = 24.dp, top = 12.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onSearchClick != null) {
            com.pira.ccloud.ui.theme.GlassIconButton(
                icon = Icons.Default.Search,
                contentDescription = "Search",
                onClick = onSearchClick
=======
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                    )
                )
>>>>>>> 16bb46ea3318e8f7e2ba73e2f974008e3b01c44d
            )
            .border(
                width = 0.5.dp,
                color = glassTint.copy(alpha = 0.12f)
            )
>>>>>>> 03d9d8ea365ac7c4ed6ac59077927b3f93b49314
    ) {
        Row(
            modifier = Modifier
<<<<<<< HEAD
                .weight(1f)
                .glassSurface(
                    shape = RoundedCornerShape(GlassCorners.Search),
                    tint = glassTint
                )
                .clickable { showFilterSheet = true }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Filters",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.height(18.dp)
                )
                Text(
                    text = "Filters",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
            }
            Text(
                text = "$filterLabel  ·  $selectedGenreTitle",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
=======
                .fillMaxWidth()
                .liquidGlass(
                    shape = RoundedCornerShape(GlassCorners.Pill),
                    isDark = isDark,
                    intensity = GlassIntensity.Chrome
                )
                .padding(horizontal = 6.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Search icon button — circular glass bubble
            if (onSearchClick != null) {
                GlassIconButton(
                    icon = Icons.Default.Tune,
                    contentDescription = "Search",
                    onClick = onSearchClick,
                    size = 40.dp
                )
            }

            // Filter trigger — glass pill that expands
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clickable { showFilterSheet = true }
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Filters",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.height(18.dp)
                    )
                    Text(
                        text = "Filters",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "$filterLabel  •  $selectedGenreTitle",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
>>>>>>> 16bb46ea3318e8f7e2ba73e2f974008e3b01c44d
        }
    }

    if (showFilterSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            sheetState = sheetState,
            containerColor = Color.Transparent,
            dragHandle = null
        ) {
            FilterSheetContent(
                genres = genres,
                selectedGenreId = selectedGenreId,
                selectedFilterType = selectedFilterType,
                onGenreSelected = onGenreSelected,
                onFilterTypeSelected = onFilterTypeSelected
            )
        }
    }
}

@Composable
private fun FilterSheetContent(
    genres: List<Genre>,
    selectedGenreId: Int,
    selectedFilterType: FilterType,
    onGenreSelected: (Int) -> Unit,
    onFilterTypeSelected: (FilterType) -> Unit
) {
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

    // Frosted-glass bottom sheet with liquid glass material
    Column(
        modifier = Modifier
            .fillMaxWidth()
<<<<<<< HEAD
            .liquidGlass(
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                isDark = isDark,
                intensity = GlassIntensity.Chrome
=======
<<<<<<< HEAD
            .glassSurface(
                shape = RoundedCornerShape(
                    topStart = GlassCorners.BottomSheet,
                    topEnd = GlassCorners.BottomSheet
                ),
                tint = glassTint
=======
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.97f)
                    )
                ),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
            )
            .border(
                width = 1.dp,
                color = glassTint.copy(alpha = 0.2f),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
>>>>>>> 16bb46ea3318e8f7e2ba73e2f974008e3b01c44d
>>>>>>> 03d9d8ea365ac7c4ed6ac59077927b3f93b49314
            )
            .navigationBarsPadding()
            .padding(24.dp)
    ) {
        Text(
            text = "Sort By",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterType.entries.forEach { type ->
                val label = when (type) {
                    FilterType.DEFAULT -> "Default"
                    FilterType.BY_YEAR -> "By Year"
                    FilterType.BY_IMDB -> "By IMDB"
                }
                FilterChip(
                    selected = selectedFilterType == type,
                    onClick = { onFilterTypeSelected(type) },
                    label = { Text(label) },
                    shape = RoundedCornerShape(GlassCorners.Tag),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Genre",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(12.dp))

        val allGenreOption = Genre(id = 0, title = "All Genres")
        val genreOptions = listOf(allGenreOption) + genres

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 110.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 8.dp)
        ) {
            items(genreOptions) { genre ->
                FilterChip(
                    selected = selectedGenreId == genre.id,
                    onClick = { onGenreSelected(genre.id) },
                    label = {
                        Text(
                            text = genre.title,
                            maxLines = 1
                        )
                    },
                    shape = RoundedCornerShape(GlassCorners.Tag),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.secondary,
                        selectedLabelColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    modifier = Modifier.width(140.dp)
                )
            }
        }
    }
}

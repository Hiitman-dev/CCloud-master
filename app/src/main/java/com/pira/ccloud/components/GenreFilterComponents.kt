package com.pira.ccloud.components

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pira.ccloud.data.model.FilterType
import com.pira.ccloud.data.model.Genre
import com.pira.ccloud.ui.theme.GlassCorners
import com.pira.ccloud.ui.theme.glassSurface
import com.pira.ccloud.ui.theme.rememberGlassTint

/**
 * Compact "Filters" trigger. Tapping it raises a glass-styled bottom sheet
 * popup with the sort type and genre pickers, instead of two wide always-open
 * dropdown cards taking up permanent space on the screen.
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
    val glassTint = rememberGlassTint()

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

    // One flat, near-opaque bar holding three segments - a solid "Search"
    // pill, then "Sort" and "Genre" dropdown segments separated by hairline
    // dividers - mirroring the reference site's single filter row instead of
    // a lone "Filters" button. Kept at the same raised opacity already tuned
    // for this bar (0.92/0.28) so labels stay legible over scrolling content.
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .glassSurface(
                shape = RoundedCornerShape(GlassCorners.Search),
                tint = glassTint,
                tintAlpha = 0.92f,
                borderAlpha = 0.28f
            )
            .padding(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onSearchClick != null) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(GlassCorners.Tag))
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable { onSearchClick() }
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.height(18.dp)
                )
                Text(
                    text = "Search",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            FilterBarDivider()
        }

        Row(
            modifier = Modifier
                .weight(1f)
                .clickable { showFilterSheet = true }
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = filterLabel,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Sort",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.height(18.dp)
            )
        }

        FilterBarDivider()

        Row(
            modifier = Modifier
                .weight(1f)
                .clickable { showFilterSheet = true }
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedGenreTitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Genre",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.height(18.dp)
            )
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

/** Thin hairline separator between segments of the filter bar. */
@Composable
private fun FilterBarDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(22.dp)
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
    )
}

/**
 * Standalone filter bottom sheet that can be triggered from any floating button.
 * Used by the Home screen's floating glassmorphism bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenreFilterBottomSheet(
    genres: List<Genre>,
    selectedGenreId: Int,
    selectedFilterType: FilterType,
    onGenreSelected: (Int) -> Unit,
    onFilterTypeSelected: (FilterType) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
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

@Composable
private fun FilterSheetContent(
    genres: List<Genre>,
    selectedGenreId: Int,
    selectedFilterType: FilterType,
    onGenreSelected: (Int) -> Unit,
    onFilterTypeSelected: (FilterType) -> Unit
) {
    val glassTint = rememberGlassTint()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            // Matte, near-opaque sheet backdrop - a faint glass wash here
            // made the sort/genre labels hard to read against whatever was
            // visible behind the sheet.
            .glassSurface(
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                tint = glassTint,
                tintAlpha = 0.96f,
                borderAlpha = 0.24f
            )
            .navigationBarsPadding()
            .padding(20.dp)
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
                    shape = RoundedCornerShape(com.pira.ccloud.ui.theme.GlassCorners.Tag),
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
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
                    shape = RoundedCornerShape(com.pira.ccloud.ui.theme.GlassCorners.Tag),
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        selectedContainerColor = MaterialTheme.colorScheme.secondary,
                        selectedLabelColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    modifier = Modifier.width(140.dp)
                )
            }
        }
    }
}

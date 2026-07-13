package com.pira.ccloud.components

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pira.ccloud.data.model.FilterType
import com.pira.ccloud.data.model.Genre
import com.pira.ccloud.ui.theme.GlassCorners
import com.pira.ccloud.ui.theme.glassSurface
import com.pira.ccloud.ui.theme.rememberGlassTint

/**
 * Premium filter trigger backed by [PremiumTopNav].
 *
 * Tapping the filter bar raises a glass-styled bottom sheet with sort
 * type and genre pickers. The search button navigates via [onSearchClick].
 *
 * The two components (filter bar + search button) are completely separate
 * floating elements — they share a layout row but are independent composables.
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

    PremiumTopNav(
        selectedFilterName = filterLabel,
        selectedGenreName = selectedGenreTitle,
        onFilterClick = { showFilterSheet = true },
        onSearchClick = { onSearchClick?.invoke() }
    )

    if (showFilterSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            sheetState = sheetState,
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
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

/**
 * Standalone filter bottom sheet that can be triggered from any floating button.
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
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
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
            fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
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
            fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
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

package com.pira.ccloud.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.pira.ccloud.ui.theme.GlassCorners
import com.pira.ccloud.ui.theme.rememberGlassTint

/**
 * Floating glassmorphism top bar.
 * Layout: 80% filter trigger | 10% search icon | 10% spacing
 * The filter trigger shows current active filter as a tappable pill.
 */
@Composable
fun FloatingTopBar(
    title: String,
    filterText: String = "All",
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val glassTint = rememberGlassTint()
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .zIndex(10f)
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(GlassCorners.Navigation),
                ambientColor = if (isDark) Color.Black.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.1f),
                spotColor = if (isDark) Color.Black.copy(alpha = 0.4f) else Color.Black.copy(alpha = 0.08f)
            )
            .clip(RoundedCornerShape(GlassCorners.Navigation))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        glassTint.copy(alpha = if (isDark) 0.4f else 0.55f),
                        glassTint.copy(alpha = if (isDark) 0.25f else 0.4f)
                    )
                )
            )
            .border(
                width = 0.5.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = if (isDark) 0.15f else 0.6f),
                        Color.White.copy(alpha = if (isDark) 0.05f else 0.2f)
                    )
                ),
                shape = RoundedCornerShape(GlassCorners.Navigation)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 80% — Filter trigger pill
            Row(
                modifier = Modifier
                    .weight(0.80f)
                    .height(42.dp)
                    .shadow(4.dp, RoundedCornerShape(14.dp))
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = if (isDark) 0.15f else 0.12f),
                                MaterialTheme.colorScheme.primary.copy(alpha = if (isDark) 0.08f else 0.06f)
                            )
                        )
                    )
                    .border(
                        width = 0.5.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .clickable { onFilterClick() }
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filter",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = filterText,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.weight(0.02f))

            // 10% — Search icon
            SearchIconButton(
                onClick = onSearchClick,
                isDark = isDark,
                glassTint = glassTint
            )

            Spacer(modifier = Modifier.weight(0.08f))
        }
    }
}

@Composable
private fun SearchIconButton(
    onClick: () -> Unit,
    isDark: Boolean,
    glassTint: Color
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(42.dp)
            .shadow(
                elevation = 6.dp,
                shape = CircleShape,
                ambientColor = if (isDark) Color.Black.copy(alpha = 0.3f) else Color.Black.copy(alpha = 0.06f),
                spotColor = if (isDark) Color.Black.copy(alpha = 0.2f) else Color.Black.copy(alpha = 0.04f)
            )
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        glassTint.copy(alpha = if (isDark) 0.35f else 0.55f),
                        glassTint.copy(alpha = if (isDark) 0.18f else 0.35f)
                    )
                )
            )
            .border(
                width = 0.5.dp,
                color = Color.White.copy(alpha = if (isDark) 0.12f else 0.45f),
                shape = CircleShape
            )
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
    }
}

/**
 * Minimal floating top bar — title only, no filter.
 * Used for Search, Favorites, Settings screens.
 */
@Composable
fun FloatingTitleBar(
    title: String,
    modifier: Modifier = Modifier
) {
    val glassTint = rememberGlassTint()
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .zIndex(10f)
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(GlassCorners.Navigation),
                ambientColor = if (isDark) Color.Black.copy(alpha = 0.4f) else Color.Black.copy(alpha = 0.08f),
                spotColor = if (isDark) Color.Black.copy(alpha = 0.3f) else Color.Black.copy(alpha = 0.06f)
            )
            .clip(RoundedCornerShape(GlassCorners.Navigation))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        glassTint.copy(alpha = if (isDark) 0.35f else 0.45f),
                        glassTint.copy(alpha = if (isDark) 0.22f else 0.35f)
                    )
                )
            )
            .border(
                width = 0.5.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = if (isDark) 0.12f else 0.5f),
                        Color.White.copy(alpha = if (isDark) 0.05f else 0.2f)
                    )
                ),
                shape = RoundedCornerShape(GlassCorners.Navigation)
            )
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

package com.pira.ccloud.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pira.ccloud.ui.theme.AppColors
import com.pira.ccloud.ui.theme.glassSurface
import com.pira.ccloud.ui.theme.rememberGlassTint

/**
 * Premium floating filter bar — pill-shaped, 80% width.
 *
 * Matte frosted-glass surface with a Tune icon (warm orange), "Filters"
 * label, and the currently selected filter/genre on the right side.
 *
 * Design: Telegram Premium / Apple / Netflix / Material 3 inspired.
 * AMOLED-optimized, soft shadows, rounded pill corners.
 */
@Composable
fun PremiumFilterBar(
    selectedFilterName: String,
    selectedGenreName: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "filterBarScale"
    )

    val glassTint = rememberGlassTint()
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(spring(dampingRatio = 0.8f)) + scaleIn(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            initialScale = 0.92f
        )
    ) {
        Row(
            modifier = modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(999.dp),
                    ambientColor = if (isDark) Color.Black.copy(alpha = 0.35f) else Color.Black.copy(alpha = 0.10f),
                    spotColor = if (isDark) Color.Black.copy(alpha = 0.25f) else Color.Black.copy(alpha = 0.06f)
                )
                .glassSurface(
                    shape = RoundedCornerShape(999.dp),
                    tint = glassTint,
                    tintAlpha = 0.88f,
                    borderAlpha = 0.18f
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) { onClick() }
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tune icon — warm orange accent
            Icon(
                imageVector = Icons.Default.Tune,
                contentDescription = "Filters",
                tint = AppColors.current.filterBarAccent,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // "Filters" label — bold, primary text
            Text(
                text = "Filters",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.weight(1f))

            // Selected filter + genre — secondary text
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = selectedFilterName,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "\u00B7",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = selectedGenreName,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}

/**
 * Premium floating search button — perfect circle, 10% width.
 *
 * Matte frosted-glass surface matching the filter bar. Centered search
 * icon with ripple, spring animation on press, and soft shadow.
 */
@Composable
fun PremiumSearchButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "searchButtonScale"
    )

    val glassTint = rememberGlassTint()
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(spring(dampingRatio = 0.8f)) + scaleIn(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            initialScale = 0.85f
        )
    ) {
        Box(
            modifier = modifier
                .size(56.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .shadow(
                    elevation = 8.dp,
                    shape = CircleShape,
                    ambientColor = if (isDark) Color.Black.copy(alpha = 0.35f) else Color.Black.copy(alpha = 0.10f),
                    spotColor = if (isDark) Color.Black.copy(alpha = 0.25f) else Color.Black.copy(alpha = 0.06f)
                )
                .glassSurface(
                    shape = CircleShape,
                    tint = glassTint,
                    tintAlpha = 0.88f,
                    borderAlpha = 0.18f
                ),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = onClick,
                interactionSource = interactionSource
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

/**
 * Combined layout controller — places [PremiumFilterBar] (80%) and
 * [PremiumSearchButton] (10%) in a Row with a 10% gap between them.
 *
 * The two components are **never merged** into a single container.
 * They are separate composables placed side-by-side.
 */
@Composable
fun PremiumTopNav(
    selectedFilterName: String,
    selectedGenreName: String,
    onFilterClick: () -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // Filter bar — 80% of available width
        PremiumFilterBar(
            selectedFilterName = selectedFilterName,
            selectedGenreName = selectedGenreName,
            onClick = onFilterClick,
            modifier = Modifier.weight(0.80f)
        )

        // Empty space — 10% gap
        Spacer(modifier = Modifier.weight(0.10f))

        // Search button — 10% of available width
        PremiumSearchButton(
            onClick = onSearchClick,
            modifier = Modifier.weight(0.10f)
        )
    }
}

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.pira.ccloud.ui.theme.GlassCorners
import com.pira.ccloud.ui.theme.rememberGlassTint

@Composable
fun FloatingTopBar(
    title: String,
    onSearchClick: () -> Unit,
    onFilterClick: (() -> Unit)? = null,
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
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            Row {
                if (onFilterClick != null) {
                    GlassCircleButton(
                        icon = Icons.Default.FilterList,
                        contentDescription = "Filter",
                        onClick = onFilterClick,
                        isDark = isDark
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                GlassCircleButton(
                    icon = Icons.Default.Search,
                    contentDescription = "Search",
                    onClick = onSearchClick,
                    isDark = isDark
                )
            }
        }
    }
}

@Composable
private fun GlassCircleButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    isDark: Boolean
) {
    val glassTint = rememberGlassTint()

    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(40.dp)
            .shadow(
                elevation = 4.dp,
                shape = CircleShape,
                ambientColor = if (isDark) Color.Black.copy(alpha = 0.3f) else Color.Black.copy(alpha = 0.06f),
                spotColor = if (isDark) Color.Black.copy(alpha = 0.2f) else Color.Black.copy(alpha = 0.04f)
            )
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        glassTint.copy(alpha = if (isDark) 0.3f else 0.5f),
                        glassTint.copy(alpha = if (isDark) 0.15f else 0.3f)
                    )
                )
            )
            .border(
                width = 0.5.dp,
                color = Color.White.copy(alpha = if (isDark) 0.1f else 0.4f),
                shape = CircleShape
            )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
    }
}

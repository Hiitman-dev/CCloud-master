package com.pira.ccloud.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.pira.ccloud.ui.theme.GlassCorners
import com.pira.ccloud.ui.theme.glassSurface
import com.pira.ccloud.ui.theme.rememberGlassTint

/**
 * Minimal floating top bar — title only, no filter/genre pill.
 * Used for Movies, Series, Search, Favorites, Settings screens.
 *
 * Frosted matte-glass surface: flat, fairly opaque tint with a hairline
 * edge and a soft drop shadow for lift - no glossy gradient shine.
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
            .glassSurface(shape = RoundedCornerShape(GlassCorners.Navigation), tint = glassTint)
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

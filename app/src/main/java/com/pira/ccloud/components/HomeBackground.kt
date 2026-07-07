package com.pira.ccloud.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * A very soft, ambient background for the home screen: a couple of large,
 * heavily blurred color fields in the theme's own primary/tertiary colors at
 * low alpha, positioned near the corners. Meant to read as atmosphere -
 * never competing with the poster art in front of it. Degrades gracefully
 * (just no blur) on pre-API 31 devices, since Modifier.blur() is a no-op
 * there rather than crashing.
 */
@Composable
fun HomeAmbientBackground(modifier: Modifier = Modifier) {
    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary

    Box(modifier = modifier) {
        // Top-start soft glow
        Box(
            modifier = Modifier
                .offset(x = (-80).dp, y = (-100).dp)
                .size(280.dp)
                .blur(110.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(primary.copy(alpha = 0.14f), Color.Transparent)
                    )
                )
        )
        // Lower-end soft glow
        Box(
            modifier = Modifier
                .offset(x = 180.dp, y = 420.dp)
                .size(300.dp)
                .blur(120.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(tertiary.copy(alpha = 0.10f), Color.Transparent)
                    )
                )
        )
    }
}

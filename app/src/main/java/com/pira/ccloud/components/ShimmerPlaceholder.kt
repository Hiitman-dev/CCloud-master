package com.pira.ccloud.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pira.ccloud.ui.theme.GlassCorners
import com.pira.ccloud.ui.theme.LocalAppColors

/**
 * Shimmer loading placeholder with animated gradient.
 * Used for skeleton loading states.
 */
@Composable
fun ShimmerPlaceholder(
    modifier: Modifier = Modifier,
    height: Dp = 200.dp,
    cornerRadius: Dp = GlassCorners.Card
) {
    val shimmerColors = LocalAppColors.current
    val shimmerBase = shimmerColors.skeletonBase
    val shimmerHighlight = shimmerColors.skeletonShimmer

    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    val brush = Brush.linearGradient(
        colors = listOf(shimmerBase, shimmerHighlight, shimmerBase),
        start = Offset(translateAnim - 200f, 0f),
        end = Offset(translateAnim, 0f)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(cornerRadius))
            .background(brush)
    )
}

/**
 * Shimmer placeholder for hero card.
 */
@Composable
fun HeroShimmerPlaceholder(modifier: Modifier = Modifier) {
    ShimmerPlaceholder(
        modifier = modifier,
        height = 280.dp,
        cornerRadius = GlassCorners.Card
    )
}

/**
 * Shimmer placeholder for poster card.
 */
@Composable
fun PosterShimmerPlaceholder(modifier: Modifier = Modifier) {
    ShimmerPlaceholder(
        modifier = modifier,
        height = 260.dp,
        cornerRadius = GlassCorners.Card
    )
}

/**
 * Shimmer placeholder for carousel section.
 */
@Composable
fun CarouselShimmerPlaceholder(
    modifier: Modifier = Modifier,
    itemCount: Int = 5
) {
    androidx.compose.foundation.lazy.LazyRow(
        modifier = modifier,
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp)
    ) {
        items(itemCount) {
            PosterShimmerPlaceholder(
                modifier = Modifier
                    .width(150.dp)
                    .height(260.dp)
            )
        }
    }
}

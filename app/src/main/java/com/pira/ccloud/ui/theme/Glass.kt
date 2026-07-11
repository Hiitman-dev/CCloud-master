package com.pira.ccloud.ui.theme

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

// ─── Corner Radius Scale ───────────────────────────────────────────────

object GlassCorners {
    val Navigation = 28.dp
    val Pill = 999.dp
    val Search = 24.dp
    val Card = 20.dp
    val Button = 18.dp
    val Tag = 999.dp
}

// ─── Liquid Glass Material ─────────────────────────────────────────────

/**
 * iOS 26-inspired "Liquid Glass" material.
 *
 * Layers (bottom → top):
 *  1. Multi-layer drop shadow (ambient + spot) for physical depth
 *  2. Tinted translucent fill (gradient from top to bottom)
 *  3. Top-edge highlight border (glass refraction rim)
 *  4. Inner light-catch gradient (diagonal, low-alpha white)
 */
fun Modifier.liquidGlass(
    shape: Shape = RoundedCornerShape(GlassCorners.Card),
    isDark: Boolean = true,
    intensity: GlassIntensity = GlassIntensity.Chrome,
): Modifier {
    val (fillTop, fillBottom) = if (isDark) {
        Color.White.copy(alpha = when (intensity) {
            GlassIntensity.Chrome -> 0.18f
            GlassIntensity.Subtle -> 0.08f
        }) to Color.White.copy(alpha = when (intensity) {
            GlassIntensity.Chrome -> 0.10f
            GlassIntensity.Subtle -> 0.04f
        })
    } else {
        Color.White.copy(alpha = when (intensity) {
            GlassIntensity.Chrome -> 0.65f
            GlassIntensity.Subtle -> 0.40f
        }) to Color.White.copy(alpha = when (intensity) {
            GlassIntensity.Chrome -> 0.45f
            GlassIntensity.Subtle -> 0.20f
        })
    }

    val shadowColor = if (isDark) Color.Black.copy(alpha = 0.40f) else Color.Black.copy(alpha = 0.12f)
    val highlightColor = Color.White.copy(alpha = if (isDark) 0.25f else 0.60f)
    val borderTopColor = Color.White.copy(alpha = if (isDark) 0.22f else 0.70f)
    val borderBottomColor = Color.White.copy(alpha = if (isDark) 0.06f else 0.20f)

    return this
        .shadow(
            elevation = when (intensity) {
                GlassIntensity.Chrome -> 20.dp
                GlassIntensity.Subtle -> 8.dp
            },
            shape = shape,
            ambientColor = shadowColor.copy(alpha = shadowColor.alpha * 0.5f),
            spotColor = shadowColor
        )
        .clip(shape)
        .background(
            brush = Brush.verticalGradient(colors = listOf(fillTop, fillBottom))
        )
        .border(
            width = 1.dp,
            brush = Brush.verticalGradient(colors = listOf(borderTopColor, borderBottomColor)),
            shape = shape
        )
        .background(
            brush = Brush.linearGradient(
                colors = listOf(
                    highlightColor.copy(alpha = highlightColor.alpha * 0.3f),
                    Color.Transparent,
                    Color.Transparent,
                    highlightColor.copy(alpha = highlightColor.alpha * 0.12f)
                ),
                start = Offset.Zero,
                end = Offset.Infinite
            )
        )
}

/**
 * Backdrop blur — API 31+ only (RenderEffect).
 * On older APIs the translucent fill + shadow + edge highlight
 * already creates a convincing glass effect.
 */
fun Modifier.backdropBlur(
    blurRadius: Dp = 24.dp,
): Modifier {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return this
    val radius = blurRadius.value
    return this.graphicsLayer {
        renderEffect = RenderEffect
            .createBlurEffect(radius, radius, Shader.TileMode.CLAMP)
    }
}

fun Modifier.glassSurface(
    shape: Shape = RoundedCornerShape(GlassCorners.Card),
    tint: Color = Color.White,
    tintAlpha: Float = 0.38f,
    borderAlpha: Float = 0.28f
): Modifier = this
    .liquidGlass(shape = shape, isDark = tint.luminance() < 0.5f, intensity = GlassIntensity.Chrome)

fun Modifier.subtleGlassSurface(
    shape: Shape = RoundedCornerShape(GlassCorners.Card),
    tint: Color = Color.White
): Modifier = this
    .liquidGlass(shape = shape, isDark = tint.luminance() < 0.5f, intensity = GlassIntensity.Subtle)

enum class GlassIntensity { Chrome, Subtle }

@Composable
fun rememberGlassTint(): Color {
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    return if (isDark) Color.Black else Color.White
}

@Composable
fun GlassIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    iconTint: Color = MaterialTheme.colorScheme.primary
) {
    val tint = rememberGlassTint()
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(size)
            .liquidGlass(shape = CircleShape, isDark = tint.luminance() < 0.5f)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconTint
        )
    }
}

@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    content: @Composable () -> Unit
) {
    val tint = rememberGlassTint()
    Box(
        modifier = modifier.liquidGlass(shape = shape, isDark = tint.luminance() < 0.5f)
    ) {
        content()
    }
}

<<<<<<< HEAD
/**
 * Glass-styled Material Overlay Dialog.
 *
 * Background: Blur → Dim → Freeze
 * Dialog: Rounded (28dp), Glass, Minimal shadow
 * Only ONE sheet should exist. No nested overlays.
 */
=======
@Composable
fun Modifier.matteOverlay(
    shape: Shape = RoundedCornerShape(0.dp)
): Modifier {
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    val overlayColor = if (isDark) Color.Black else Color.White
    return this
        .background(
            brush = Brush.verticalGradient(
                colors = listOf(
                    overlayColor.copy(alpha = 0.85f),
                    overlayColor.copy(alpha = 0.92f),
                    overlayColor.copy(alpha = 0.95f)
                )
            )
        )
        .border(width = 0.dp, color = Color.Transparent, shape = shape)
}

>>>>>>> 18e9b33b29dda900dfc7eb9a48c6fbad8abbd743
@Composable
fun GlassAlertDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    dismissButton: (@Composable () -> Unit)? = null,
    icon: (@Composable () -> Unit)? = null,
    title: (@Composable () -> Unit)? = null,
    text: (@Composable () -> Unit)? = null,
    shape: Shape = RoundedCornerShape(28.dp),
    containerColor: Color = Color.Unspecified,
    tonalElevation: Dp = 0.dp,
    properties: DialogProperties = DialogProperties()
) {
    val tint = rememberGlassTint()
    val isDark = tint.luminance() < 0.5f
    Dialog(onDismissRequest = onDismissRequest, properties = properties) {
        Box(
            modifier = modifier
                .widthIn(min = 280.dp, max = 560.dp)
                .liquidGlass(shape = shape, isDark = isDark, intensity = GlassIntensity.Chrome)
                .padding(24.dp)
        ) {
            Column {
                icon?.let { iconContent ->
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) { iconContent() }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                title?.let { titleContent ->
                    ProvideTextStyle(MaterialTheme.typography.headlineSmall) { titleContent() }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                text?.let { textContent ->
                    ProvideTextStyle(MaterialTheme.typography.bodyMedium) { textContent() }
                    Spacer(modifier = Modifier.height(24.dp))
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    dismissButton?.let { dismissContent ->
                        dismissContent()
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    confirmButton()
                }
            }
        }
    }
}

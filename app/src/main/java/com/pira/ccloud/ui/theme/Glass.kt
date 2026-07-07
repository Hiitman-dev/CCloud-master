package com.pira.ccloud.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * Standard corner-radius scale, kept consistent everywhere instead of ad hoc
 * values scattered around the app.
 */
object GlassCorners {
    val Navigation = 30.dp
    val Search = 26.dp
    val Card = 20.dp
    val Button = 18.dp
    val Tag = 999.dp
}

/**
 * Liquid-glass ("glassmorphism") styling shared across the app.
 *
 * Note on limits: true backdrop blur-of-what's-behind-this-element requires
 * Android 12+ (RenderEffect, API 31). Since this app supports API 24+, we
 * approximate the "glass" look everywhere with a translucent tinted surface,
 * a soft diagonal light-catch gradient, and a hairline edge highlight - the
 * same technique most glassmorphism UIs use for broad device compatibility.
 *
 * Two intensities are used across the app, deliberately:
 *  - [glassSurface] (this one): for UI *chrome* - search bar, bottom nav,
 *    modal sheets, filter chips, dialogs. These are allowed to read as
 *    visibly translucent glass.
 *  - [subtleGlassSurface]: for regular *content* cards (movie/series
 *    posters). These stay close to a clean, opaque surface with only a
 *    faint frosted hint, so poster art stays vibrant and readable instead
 *    of every card in a grid looking like foggy glass.
 */
fun Modifier.glassSurface(
    shape: Shape = RoundedCornerShape(GlassCorners.Card),
    tint: Color = Color.White,
    tintAlpha: Float = 0.38f,
    borderAlpha: Float = 0.28f
): Modifier = this
    .clip(shape)
    .background(
        brush = Brush.linearGradient(
            colors = listOf(
                tint.copy(alpha = (tintAlpha * 1.3f).coerceAtMost(0.6f)),
                tint.copy(alpha = (tintAlpha * 0.75f).coerceAtMost(0.5f))
            )
        )
    )
    .border(
        width = 1.dp,
        brush = Brush.linearGradient(
            colors = listOf(
                tint.copy(alpha = borderAlpha),
                tint.copy(alpha = borderAlpha * 0.3f)
            )
        ),
        shape = shape
    )

/**
 * A much lighter touch than [glassSurface] - a near-opaque clean surface
 * with only a faint frosted hint. Use this for regular content cards
 * (movie/series posters, grid items) so the poster art stays vibrant and
 * clear, per the "don't make every card glass" design direction.
 */
fun Modifier.subtleGlassSurface(
    shape: Shape = RoundedCornerShape(GlassCorners.Card),
    tint: Color = Color.White
): Modifier = this
    .clip(shape)
    .background(tint.copy(alpha = 0.08f))
    .border(
        width = 1.dp,
        color = tint.copy(alpha = 0.14f),
        shape = shape
    )

/**
 * Picks a tint that matches the theme's own surface tone (dark tint on dark
 * theme, light tint on light theme) so that normal onSurface/onBackground
 * text - which is already tuned for that tone - stays readable on top of it.
 * (A tint that inverted the tone would fight with same-colored text sitting
 * on top of it, which is what made earlier glass surfaces hard to read.)
 */
@Composable
fun rememberGlassTint(): Color {
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    return if (isDark) Color.Black else Color.White
}

/** A circular, frosted-glass icon button - used for the floating search icon, filter trigger, etc. */
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
            .glassSurface(shape = CircleShape, tint = tint)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconTint
        )
    }
}

/** A generic frosted-glass surface container for cards, chips, sheets, etc. */
@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    content: @Composable () -> Unit
) {
    val tint = rememberGlassTint()
    Box(
        modifier = modifier.glassSurface(shape = shape, tint = tint)
    ) {
        content()
    }
}

/**
 * A matte overlay surface for detail screens (movie/series details).
 * Creates a semi-transparent frosted overlay that sits on top of the
 * previous screen content, giving a "matte glass" effect without a solid background.
 * The overlay uses the theme's surface color with low alpha for a subtle,
 * non-distracting appearance.
 */
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
        .border(
            width = 0.dp,
            color = Color.Transparent,
            shape = shape
        )
}

/**
 * A drop-in glass-styled replacement for Material3's `AlertDialog`.
 * Mirrors the same parameter names/positions (onDismissRequest, icon, title,
 * text, confirmButton, dismissButton) so existing call sites can switch from
 * `AlertDialog(...)` to `GlassAlertDialog(...)` without touching their content.
 */
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
    // Accepted for drop-in compatibility with Material3's AlertDialog signature,
    // but intentionally unused - the glass surface fully determines the look.
    containerColor: Color = Color.Unspecified,
    tonalElevation: Dp = 0.dp,
    properties: DialogProperties = DialogProperties()
) {
    val tint = rememberGlassTint()
    Dialog(onDismissRequest = onDismissRequest, properties = properties) {
        Box(
            modifier = modifier
                .widthIn(min = 280.dp, max = 560.dp)
                .glassSurface(shape = shape, tint = tint, tintAlpha = 0.5f, borderAlpha = 0.45f)
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

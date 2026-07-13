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
 * Frosted matte-glass styling shared across the app.
 *
 * Note on limits: true backdrop blur-of-what's-behind-this-element requires
 * Android 12+ (RenderEffect, API 31). Since this app supports API 24+, we
 * approximate the "frosted glass" look everywhere with a flat, fairly opaque
 * tinted surface and a soft hairline edge - deliberately *without* the
 * diagonal shine/gradient a clear "liquid glass" surface would have. Matte
 * glass reads as a solid, slightly translucent panel, not a glossy bubble.
 *
 * Two intensities are used across the app, deliberately:
 *  - [glassSurface] (this one): for UI *chrome* - search bar, bottom nav,
 *    modal sheets, filter chips, dialogs. These are allowed to read as a
 *    visibly frosted, matte panel.
 *  - [subtleGlassSurface]: for regular *content* cards (movie/series
 *    posters). These stay close to a clean, opaque surface with only a
 *    faint frosted hint, so poster art stays vibrant and readable instead
 *    of every card in a grid looking like foggy glass.
 */
fun Modifier.glassSurface(
    shape: Shape = RoundedCornerShape(GlassCorners.Card),
    tint: Color = Color.White,
    // Matte/frosted range: flat opacity, hairline border. Deliberately flat
    // (no gradient) - glass is a material for chrome, not a decorative
    // identity, so it should never read as a shiny/wet surface. Raised from
    // the original 45%-60% range: at low alpha a black tint over an
    // already near-black dark background (or a white tint over an already
    // near-white light background) barely showed up at all, so chrome like
    // the search/filter bar read as almost invisible in both themes.
    tintAlpha: Float = 0.78f,
    borderAlpha: Float = 0.22f
): Modifier = this
    .clip(shape)
    .background(color = tint.copy(alpha = tintAlpha.coerceIn(0f, 1f)))
    .border(
        width = 1.dp,
        color = tint.copy(alpha = borderAlpha.coerceIn(0f, 1f)),
        shape = shape
    )

/**
 * A much lighter touch than [glassSurface] - a near-opaque clean surface
 * with only a faint frosted hint. Use this for regular content cards
 * (movie/series posters, grid items) so the poster art stays vibrant and
 * clear, per the "don't make every card glass" design direction.
 *
 * Pass [rememberCardTint] (not [rememberGlassTint]) as the tint here: cards
 * sit flush on the plain page background rather than floating over
 * arbitrary imagery, so they need a tone that's actually offset from
 * [androidx.compose.material3.ColorScheme.background] - a flat black/white
 * wash at low alpha (the old default) was nearly invisible against an
 * already near-black dark background or an already near-white light one,
 * which is why cards used to blend into the page in both themes.
 */
fun Modifier.subtleGlassSurface(
    shape: Shape = RoundedCornerShape(GlassCorners.Card),
    tint: Color = Color.White
): Modifier = this
    .clip(shape)
    .background(tint.copy(alpha = 0.6f))
    .border(
        width = 1.dp,
        color = tint.copy(alpha = 0.32f),
        shape = shape
    )

/**
 * Picks a tint that matches the theme's own surface tone (dark tint on dark
 * theme, light tint on light theme) so that normal onSurface/onBackground
 * text - which is already tuned for that tone - stays readable on top of it.
 * (A tint that inverted the tone would fight with same-colored text sitting
 * on top of it, which is what made earlier glass surfaces hard to read.)
 *
 * This is for *chrome that floats over arbitrary content/imagery* (nav bar,
 * dialogs, bottom sheets, the search/filter bar) where a universal
 * black-in-dark / white-in-light scrim is the right call. For flat content
 * cards sitting on the plain page background, use [rememberCardTint]
 * instead - a pure black/white wash barely separates from an
 * already-near-black or already-near-white page background.
 */
@Composable
fun rememberGlassTint(): Color {
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    return if (isDark) Color.Black else Color.White
}

/**
 * Tone for regular content cards/panels (poster cards, episode rows,
 * download boxes, etc.) that sit directly on the plain page background and
 * need to visibly separate from it in *both* light and dark mode.
 *
 * Unlike [rememberGlassTint]'s flat black/white wash, this derives from the
 * theme's own `surfaceVariant` - which [buildAppColorScheme] already offsets
 * from `background` by a real, deliberate lightness step in both themes -
 * and against which `onSurface`/`onSurfaceVariant` text is already tuned to
 * read cleanly, so cards gain real color separation without hurting text
 * contrast.
 */
@Composable
fun rememberCardTint(): Color = MaterialTheme.colorScheme.surfaceVariant

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

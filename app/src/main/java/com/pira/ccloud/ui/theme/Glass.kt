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
 * ─── Glass Corner Radius System ───────────────────────────────────
 * Every radius value comes from the design system.
 *
 * Bottom Navigation : 32dp
 * Search Bar        : 28dp
 * Cards             : 22dp
 * Buttons           : 18dp
 * Poster            : 18dp
 * Bottom Sheet      : 30dp
 * Dialog            : 28dp
 * Tags / Chips      : 999dp (pill)
 */
object GlassCorners {
    val Navigation  = 32.dp
    val Search      = 28.dp
    val Card        = 22.dp
    val Button      = 18.dp
    val Poster      = 18.dp
    val BottomSheet = 30.dp
    val Dialog      = 28.dp
    val Tag         = 999.dp
}

/**
 * ─── Shadow System ────────────────────────────────────────────────
 * Soft ambient shadows only. Never heavy drop shadows.
 *
 * Large Surface : 0 12 40 rgba(0,0,0,0.08)
 * Small Surface : 0 6 20 rgba(0,0,0,0.06)
 * Buttons       : very subtle elevation only
 */
object GlassShadow {
    val LargeSurface = Pair(12.dp, Color.Black.copy(alpha = 0.08f))
    val SmallSurface = Pair(6.dp, Color.Black.copy(alpha = 0.06f))
}

/**
 * ─── Glass Design System ─────────────────────────────────────────
 *
 * Architectural frosted glass — NOT Apple Liquid Glass, NOT bubble,
 * NOT jelly. Think luxury hotel interiors, modern architecture,
 * premium automotive displays.
 *
 * Specifications:
 *   Opacity       : 28%–38%
 *   Backdrop Blur : 20px–32px (approximated via translucent tint on API < 31)
 *   Border        : 1px rgba(255,255,255,0.30)
 *   Reflection    : Very subtle diagonal gradient
 *   Shadow        : Soft ambient only
 *   Edge Highlight: Minimal
 *
 * Two intensities are deliberately maintained:
 *  - [glassSurface]       : for UI *chrome* — search bar, bottom nav,
 *                            modal sheets, filter chips, dialogs.
 *                            These read as visibly translucent glass.
 *  - [subtleGlassSurface] : for regular *content* cards (movie/series
 *                            posters). Near-opaque with only a faint
 *                            frosted hint, so poster art stays vibrant.
 */
fun Modifier.glassSurface(
    shape: Shape = RoundedCornerShape(GlassCorners.Card),
    tint: Color = Color.White,
    tintAlpha: Float = 0.32f,
    borderAlpha: Float = 0.30f
): Modifier = this
    .clip(shape)
    .background(
        brush = Brush.linearGradient(
            colors = listOf(
                tint.copy(alpha = (tintAlpha * 1.15f).coerceAtMost(0.44f)),
                tint.copy(alpha = (tintAlpha * 0.80f).coerceAtMost(0.35f))
            )
        )
    )
    .border(
        width = 1.dp,
        brush = Brush.linearGradient(
            colors = listOf(
                tint.copy(alpha = borderAlpha),
                tint.copy(alpha = borderAlpha * 0.35f)
            )
        ),
        shape = shape
    )

/**
 * A much lighter touch — near-opaque clean surface with only a faint
 * frosted hint. Use for content cards (movie/series posters, grid items)
 * so artwork stays vibrant and readable.
 */
fun Modifier.subtleGlassSurface(
    shape: Shape = RoundedCornerShape(GlassCorners.Card),
    tint: Color = Color.White
): Modifier = this
    .clip(shape)
    .background(tint.copy(alpha = 0.06f))
    .border(
        width = 1.dp,
        color = tint.copy(alpha = 0.10f),
        shape = shape
    )

/**
 * Picks a tint matching the theme's surface tone (dark tint on dark
 * theme, light tint on light theme) so normal onSurface/onBackground
 * text stays readable on top of it.
 */
@Composable
fun rememberGlassTint(): Color {
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    return if (isDark) Color.Black else Color.White
}

/**
 * A circular, frosted-glass icon button — floating search, filter trigger, etc.
 * Touch target: 44dp minimum per accessibility rules.
 */
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

/**
 * A generic frosted-glass surface container.
 */
@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(GlassCorners.Card),
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
 * Glass-styled Material Overlay Dialog.
 *
 * Background: Blur → Dim → Freeze
 * Dialog: Rounded (28dp), Glass, Minimal shadow
 * Only ONE sheet should exist. No nested overlays.
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
    shape: Shape = RoundedCornerShape(GlassCorners.Dialog),
    containerColor: Color = Color.Unspecified,
    tonalElevation: Dp = 0.dp,
    properties: DialogProperties = DialogProperties()
) {
    val tint = rememberGlassTint()
    Dialog(onDismissRequest = onDismissRequest, properties = properties) {
        Box(
            modifier = modifier
                .widthIn(min = 280.dp, max = 560.dp)
                .shadow(
                    elevation = GlassShadow.SmallSurface.first,
                    shape = shape,
                    ambientColor = GlassShadow.SmallSurface.second,
                    spotColor = GlassShadow.SmallSurface.second
                )
                .glassSurface(
                    shape = shape,
                    tint = tint,
                    tintAlpha = 0.48f,
                    borderAlpha = 0.38f
                )
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
                    ProvideTextStyle(MaterialTheme.typography.headlineSmall) {
                        titleContent()
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                text?.let { textContent ->
                    ProvideTextStyle(MaterialTheme.typography.bodyMedium) {
                        textContent()
                    }
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

/**
 * Glass-styled Bottom Sheet — used for Filter Panel, Download Panel,
 * Search Suggestions, etc.
 *
 * Radius: 30dp per design system.
 * Background auto-blurs, freezes, dims the page behind.
 * Only one sheet should exist at a time.
 */
@Composable
fun GlassBottomSheet(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val tint = rememberGlassTint()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .glassSurface(
                shape = RoundedCornerShape(
                    topStart = GlassCorners.BottomSheet,
                    topEnd = GlassCorners.BottomSheet
                ),
                tint = tint,
                tintAlpha = 0.42f,
                borderAlpha = 0.32f
            )
            .shadow(
                elevation = GlassShadow.LargeSurface.first,
                shape = RoundedCornerShape(
                    topStart = GlassCorners.BottomSheet,
                    topEnd = GlassCorners.BottomSheet
                ),
                ambientColor = GlassShadow.LargeSurface.second,
                spotColor = GlassShadow.LargeSurface.second
            )
            .navigationBarsPadding()
            .padding(24.dp)
    ) {
        content()
    }
}

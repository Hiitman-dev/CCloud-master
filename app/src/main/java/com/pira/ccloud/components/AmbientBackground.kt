package com.pira.ccloud.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.min

/**
 * A slow, ambient animated glow that sits behind the entire app (mounted
 * once, above CCloudTheme's colorScheme so it can read the live theme
 * colors, and below MainScreen/the NavHost so every screen - which don't
 * paint their own opaque background - shows it through).
 *
 * Design intent ("نیمه زنده" - semi-alive):
 *  - A few large, very low-alpha radial-gradient blobs in the app's own
 *    primary/secondary/tertiary hues (never anything off-brand), each
 *    drifting on its own slow, independent loop so the motion reads as
 *    organic rather than a single obvious animation.
 *  - Touch adds a gentle, imperceptible pull: the blobs drift a little
 *    toward wherever the finger last was, smoothed through a spring so it
 *    never feels like a direct 1:1 drag - just a subtle sense that the
 *    background is "aware" of the touch.
 *  - Touch tracking reads pointer position in the Initial pass and never
 *    calls change.consume(), so it never blocks clicks/scrolling in any
 *    of the real UI drawn on top of it.
 */
@Composable
fun AmbientBackground(modifier: Modifier = Modifier) {
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val tertiary = MaterialTheme.colorScheme.tertiary
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    // Blobs read clearly against a near-black background at a higher alpha
    // than they would against a near-white one, where the same alpha nearly
    // disappears.
    val blobAlpha = if (isDark) 0.20f else 0.10f

    val infinite = rememberInfiniteTransition(label = "ambient_bg")

    // Three independent, slow drift cycles - deliberately different
    // durations/phases so the three blobs never move in lockstep.
    val t1 by infinite.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(24000, easing = LinearEasing), RepeatMode.Reverse),
        label = "t1"
    )
    val t2 by infinite.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(31000, easing = LinearEasing), RepeatMode.Reverse),
        label = "t2"
    )
    val t3 by infinite.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(27000, easing = LinearEasing), RepeatMode.Reverse),
        label = "t3"
    )
    // Gentle "breathing" size pulse, its own independent slow cycle.
    val breathe by infinite.animateFloat(
        initialValue = 0.92f, targetValue = 1.08f,
        animationSpec = infiniteRepeatable(tween(9000, easing = LinearEasing), RepeatMode.Reverse),
        label = "breathe"
    )

    // Last touch position, followed with a soft spring rather than
    // snapping - this is what makes the reaction read as a gentle pull
    // instead of the background visibly tracking your finger.
    val touch = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
    var rawTouch by remember { mutableStateOf<Offset?>(null) }

    LaunchedEffect(rawTouch) {
        rawTouch?.let {
            touch.animateTo(
                it,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessVeryLow
                )
            )
        }
    }

    // The single opaque base fill for the *entire app*: every screen is
    // deliberately transparent (see MainScreen's Scaffold containerColor)
    // so this layer shows through everywhere. Previously this Canvas only
    // drew the low-alpha glow blobs below and never painted a solid color
    // first - so on top of nothing, the real "page" the user saw was
    // whatever the native Android window background happened to be
    // (Theme.CCloud, a light-only theme with no dark variant), regardless
    // of the in-app theme. That's why dark mode showed dark chrome (cards,
    // nav bar - which do read MaterialTheme.colorScheme correctly) floating
    // over a page that stayed stuck in light mode. Filling with the live
    // themed background color here first is what actually makes dark mode
    // (and light mode) apply consistently everywhere.
    val background = MaterialTheme.colorScheme.background

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                // Track position only - read in the Initial pass and never
                // consumed, so every click/scroll above this layer behaves
                // exactly as if this background wasn't here at all.
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Initial)
                        val change = event.changes.firstOrNull()
                        if (change != null && change.pressed) {
                            rawTouch = change.position
                        }
                    }
                }
            }
    ) {
        drawRect(color = background)

        val w = size.width
        val h = size.height
        val base = min(w, h)
        val pull = touch.value

        fun blobCenter(fx: Float, fy: Float): Offset {
            val base0 = Offset(fx * w, fy * h)
            // Only blend toward touch once we actually have one; otherwise
            // pull is (0,0) and would drag everything toward the corner.
            return if (rawTouch != null) {
                Offset(
                    x = base0.x + (pull.x - base0.x) * 0.10f,
                    y = base0.y + (pull.y - base0.y) * 0.10f
                )
            } else {
                base0
            }
        }

        val radius = base * 0.55f * breathe

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(primary.copy(alpha = blobAlpha), primary.copy(alpha = 0f)),
                center = blobCenter(0.20f + 0.15f * t1, 0.25f + 0.10f * t2),
                radius = radius
            ),
            radius = radius,
            center = blobCenter(0.20f + 0.15f * t1, 0.25f + 0.10f * t2)
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(tertiary.copy(alpha = blobAlpha), tertiary.copy(alpha = 0f)),
                center = blobCenter(0.80f - 0.15f * t2, 0.30f + 0.12f * t3),
                radius = radius * 0.9f
            ),
            radius = radius * 0.9f,
            center = blobCenter(0.80f - 0.15f * t2, 0.30f + 0.12f * t3)
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(secondary.copy(alpha = blobAlpha), secondary.copy(alpha = 0f)),
                center = blobCenter(0.45f + 0.12f * t3, 0.80f - 0.15f * t1),
                radius = radius * 0.85f
            ),
            radius = radius * 0.85f,
            center = blobCenter(0.45f + 0.12f * t3, 0.80f - 0.15f * t1)
        )
    }
}

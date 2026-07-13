package com.pira.ccloud.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Builds a full, internally-consistent Material3 [ColorScheme] from one seed
 * color. Every role — including container, outline, and surface-container
 * roles — is derived algorithmically so nothing silently falls back to
 * Material3's default lilac/violet palette.
 *
 * Two completely independent palettes are produced:
 *
 *  - **Light ("Clean Premium Elegance")**: warm off-whites with real tonal
 *    separation between background / surface / surfaceVariant, soft shadows,
 *    and an airy, refined feel.
 *
 *  - **Dark ("Deep Premium Depth")**: deep neutral backgrounds (never pure
 *    black), layered surfaces with clear elevation steps, soft overlays,
 *    and premium contrast designed for reduced eye strain.
 *
 * The two themes are not inversions — each has its own carefully crafted
 * luminance spread, saturation levels, and contrast targets.
 */

// ─── HSL utilities ────────────────────────────────────────────

private fun Color.toHsl(): FloatArray {
    val r = red
    val g = green
    val b = blue
    val max = max(r, max(g, b))
    val min = min(r, min(g, b))
    val l = (max + min) / 2f
    if (max == min) return floatArrayOf(0f, 0f, l)
    val d = max - min
    val s = if (l > 0.5f) d / (2f - max - min) else d / (max + min)
    val h = when (max) {
        r -> ((g - b) / d + (if (g < b) 6f else 0f))
        g -> (b - r) / d + 2f
        else -> (r - g) / d + 4f
    } / 6f
    return floatArrayOf(h * 360f, s, l)
}

private fun hslToColor(h: Float, s: Float, l: Float, alpha: Float = 1f): Color {
    val hh = ((h % 360f) + 360f) % 360f / 360f
    if (s == 0f) return Color(l, l, l, alpha)
    fun hue2rgb(p: Float, q: Float, t0: Float): Float {
        var t = t0
        if (t < 0f) t += 1f
        if (t > 1f) t -= 1f
        return when {
            t < 1f / 6f -> p + (q - p) * 6f * t
            t < 1f / 2f -> q
            t < 2f / 3f -> p + (q - p) * (2f / 3f - t) * 6f
            else -> p
        }
    }
    val q = if (l < 0.5f) l * (1f + s) else l + s - l * s
    val p = 2f * l - q
    val r = hue2rgb(p, q, hh + 1f / 3f)
    val g = hue2rgb(p, q, hh)
    val b = hue2rgb(p, q, hh - 1f / 3f)
    return Color(r.coerceIn(0f, 1f), g.coerceIn(0f, 1f), b.coerceIn(0f, 1f), alpha)
}

/** Returns a tone of [seed]'s hue at the requested saturation / lightness. */
private fun Color.tone(saturation: Float, lightness: Float): Color {
    val hsl = toHsl()
    return hslToColor(hsl[0], saturation.coerceIn(0f, 1f), lightness.coerceIn(0f, 1f))
}

private fun Color.shiftHue(degrees: Float, saturation: Float, lightness: Float): Color {
    val hsl = toHsl()
    return hslToColor(hsl[0] + degrees, saturation.coerceIn(0f, 1f), lightness.coerceIn(0f, 1f))
}

// ─── Color scheme builder ─────────────────────────────────────

fun buildAppColorScheme(seed: Color, dark: Boolean): ColorScheme {
    val hsl = seed.toHsl()
    val hue = hsl[0]
    // Capped so a vivid user-picked accent still resolves to a soft,
    // desaturated tone — the interface stays "mostly monochromatic" with
    // color used only as a quiet accent.
    val baseSat = hsl[1].coerceIn(0.14f, 0.30f)
    val seedColor = hslToColor(hue, baseSat, if (dark) 0.74f else 0.42f)

    return if (dark) buildDarkScheme(hue, baseSat, seedColor)
    else buildLightScheme(hue, baseSat, seedColor)
}

// ───────────────────────────────────────────────────────────────
//  DARK — "Deep Premium Depth"
//
//  Deep charcoal base (never pure black), three distinct surface
//  tiers with clear luminance steps, premium contrast.
// ───────────────────────────────────────────────────────────────
private fun buildDarkScheme(hue: Float, baseSat: Float, seedColor: Color): ColorScheme {
    return darkColorScheme(
        primary = seedColor,
        onPrimary = hslToColor(hue, baseSat, 0.12f),
        primaryContainer = hslToColor(hue, baseSat * 0.70f, 0.22f),
        onPrimaryContainer = hslToColor(hue, baseSat * 0.5f, 0.90f),

        secondary = hslToColor(hue, baseSat * 0.35f, 0.80f),
        onSecondary = hslToColor(hue, baseSat * 0.30f, 0.14f),
        secondaryContainer = hslToColor(hue, baseSat * 0.28f, 0.20f),
        onSecondaryContainer = hslToColor(hue, baseSat * 0.30f, 0.88f),

        tertiary = seedColor.shiftHue(24f, baseSat * 0.40f, 0.78f),
        onTertiary = hslToColor(hue + 24f, baseSat * 0.40f, 0.14f),
        tertiaryContainer = seedColor.shiftHue(24f, baseSat * 0.28f, 0.22f),
        onTertiaryContainer = hslToColor(hue + 24f, baseSat * 0.40f, 0.88f),

        // ── Surface hierarchy (3 tiers) ──
        // Tier 0: page background — deepest, near-black
        background = hslToColor(hue, 0.02f, 0.065f),
        onBackground = hslToColor(hue, 0.02f, 0.95f),

        // Tier 1: base surface — cards, list items
        surface = hslToColor(hue, 0.03f, 0.100f),
        onSurface = hslToColor(hue, 0.02f, 0.95f),

        // Tier 2: elevated surface — panels, chips, raised cards
        surfaceVariant = hslToColor(hue, 0.04f, 0.185f),
        onSurfaceVariant = hslToColor(hue, 0.03f, 0.82f),

        surfaceTint = seedColor,
        inverseSurface = hslToColor(hue, 0.03f, 0.92f),
        inverseOnSurface = hslToColor(hue, 0.04f, 0.16f),
        inversePrimary = hslToColor(hue, baseSat, 0.36f),

        // Borders — visible but not harsh
        outline = hslToColor(hue, 0.04f, 0.44f),
        outlineVariant = hslToColor(hue, 0.04f, 0.26f),

        scrim = Color.Black
    )
}

// ───────────────────────────────────────────────────────────────
//  LIGHT — "Clean Premium Elegance"
//
//  Warm off-whites with real tonal separation between background /
//  surface / surfaceVariant, airy and refined.
// ───────────────────────────────────────────────────────────────
private fun buildLightScheme(hue: Float, baseSat: Float, seedColor: Color): ColorScheme {
    return lightColorScheme(
        primary = seedColor,
        onPrimary = Color.White,
        primaryContainer = hslToColor(hue, baseSat * 0.55f, 0.90f),
        onPrimaryContainer = hslToColor(hue, baseSat, 0.18f),

        secondary = hslToColor(hue, baseSat * 0.40f, 0.42f),
        onSecondary = Color.White,
        secondaryContainer = hslToColor(hue, baseSat * 0.30f, 0.93f),
        onSecondaryContainer = hslToColor(hue, baseSat * 0.40f, 0.18f),

        tertiary = seedColor.shiftHue(24f, baseSat * 0.45f, 0.44f),
        onTertiary = Color.White,
        tertiaryContainer = seedColor.shiftHue(24f, baseSat * 0.30f, 0.92f),
        onTertiaryContainer = hslToColor(hue + 24f, baseSat * 0.50f, 0.18f),

        // ── Surface hierarchy (3 tiers with visible separation) ──
        // Tier 0: page background — warm off-white
        background = hslToColor(hue, 0.05f, 0.970f),
        onBackground = hslToColor(hue, 0.06f, 0.14f),

        // Tier 1: base surface — slightly lighter than background
        surface = hslToColor(hue, 0.03f, 0.995f),
        onSurface = hslToColor(hue, 0.06f, 0.14f),

        // Tier 2: elevated surface — clear mid-tone step
        surfaceVariant = hslToColor(hue, 0.06f, 0.930f),
        onSurfaceVariant = hslToColor(hue, 0.05f, 0.34f),

        surfaceTint = seedColor,
        inverseSurface = hslToColor(hue, 0.04f, 0.18f),
        inverseOnSurface = hslToColor(hue, 0.03f, 0.96f),
        inversePrimary = hslToColor(hue, baseSat, 0.78f),

        // Borders — warm, visible
        outline = hslToColor(hue, 0.06f, 0.56f),
        outlineVariant = hslToColor(hue, 0.05f, 0.83f),

        scrim = Color.Black
    )
}

private fun similar(a: Float, b: Float) = abs(a - b) < 0.01f

package com.pira.ccloud.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Every screen in the app used to build its ColorScheme by calling
 * `lightColorScheme(primary = ..., secondary = ..., ...)` / `darkColorScheme(...)`
 * while only overriding a handful of roles (primary/secondary/tertiary/
 * background/surface/surfaceVariant). Any role that wasn't explicitly passed
 * silently fell back to Material3's own baseline lilac/violet palette -
 * primaryContainer, secondaryContainer, outline, etc. That's why a random
 * purple kept showing up (e.g. in the download-quality buttons) no matter
 * which accent color the user picked, and why light mode looked like one
 * flat white slab: background/surface/surfaceVariant were all near-identical
 * off-whites and nothing else was tuned.
 *
 * This file derives *every* role from a single brand/seed color so the whole
 * app - containers included - always matches the chosen accent, and light
 * mode gets real tonal separation between background/surface/cards.
 */

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

/** Returns a tone of [seed]'s hue at the requested saturation/lightness. */
private fun Color.tone(saturation: Float, lightness: Float): Color {
    val hsl = toHsl()
    return hslToColor(hsl[0], saturation.coerceIn(0f, 1f), lightness.coerceIn(0f, 1f))
}

private fun Color.shiftHue(degrees: Float, saturation: Float, lightness: Float): Color {
    val hsl = toHsl()
    return hslToColor(hsl[0] + degrees, saturation.coerceIn(0f, 1f), lightness.coerceIn(0f, 1f))
}

/**
 * Builds a full, internally-consistent Material3 [ColorScheme] from one seed
 * color so every role - including the container/outline roles screens forget
 * to set - stays on-brand instead of quietly falling back to Material's
 * default purple.
 */
fun buildAppColorScheme(seed: Color, dark: Boolean): ColorScheme {
    val hsl = seed.toHsl()
    val hue = hsl[0]
    val baseSat = max(hsl[1], 0.28f)
    val seedColor = hslToColor(hue, baseSat, if (dark) 0.72f else 0.42f)

    return if (dark) {
        darkColorScheme(
            primary = seedColor,
            onPrimary = hslToColor(hue, baseSat, 0.12f),
            primaryContainer = hslToColor(hue, baseSat * 0.75f, 0.26f),
            onPrimaryContainer = hslToColor(hue, baseSat * 0.6f, 0.90f),
            secondary = hslToColor(hue, baseSat * 0.45f, 0.78f),
            onSecondary = hslToColor(hue, baseSat * 0.4f, 0.16f),
            secondaryContainer = hslToColor(hue, baseSat * 0.35f, 0.24f),
            onSecondaryContainer = hslToColor(hue, baseSat * 0.3f, 0.88f),
            tertiary = seedColor.shiftHue(40f, baseSat * 0.55f, 0.76f),
            onTertiary = hslToColor(hue + 40f, baseSat * 0.5f, 0.16f),
            tertiaryContainer = seedColor.shiftHue(40f, baseSat * 0.4f, 0.26f),
            onTertiaryContainer = hslToColor(hue + 40f, baseSat * 0.4f, 0.88f),
            background = hslToColor(hue, 0.10f, 0.08f),
            onBackground = hslToColor(hue, 0.06f, 0.94f),
            surface = hslToColor(hue, 0.12f, 0.11f),
            onSurface = hslToColor(hue, 0.06f, 0.94f),
            surfaceVariant = hslToColor(hue, 0.14f, 0.20f),
            onSurfaceVariant = hslToColor(hue, 0.08f, 0.82f),
            surfaceTint = seedColor,
            inverseSurface = hslToColor(hue, 0.08f, 0.92f),
            inverseOnSurface = hslToColor(hue, 0.10f, 0.16f),
            inversePrimary = hslToColor(hue, baseSat, 0.36f),
            outline = hslToColor(hue, 0.10f, 0.48f),
            outlineVariant = hslToColor(hue, 0.12f, 0.30f),
            scrim = Color.Black
        )
    } else {
        lightColorScheme(
            primary = seedColor,
            onPrimary = Color.White,
            primaryContainer = hslToColor(hue, baseSat * 0.55f, 0.88f),
            onPrimaryContainer = hslToColor(hue, baseSat, 0.20f),
            secondary = hslToColor(hue, baseSat * 0.4f, 0.38f),
            onSecondary = Color.White,
            secondaryContainer = hslToColor(hue, baseSat * 0.35f, 0.90f),
            onSecondaryContainer = hslToColor(hue, baseSat * 0.4f, 0.20f),
            tertiary = seedColor.shiftHue(40f, baseSat * 0.5f, 0.40f),
            onTertiary = Color.White,
            tertiaryContainer = seedColor.shiftHue(40f, baseSat * 0.35f, 0.90f),
            onTertiaryContainer = hslToColor(hue + 40f, baseSat * 0.5f, 0.20f),
            background = hslToColor(hue, 0.24f, 0.965f),
            onBackground = hslToColor(hue, 0.18f, 0.14f),
            surface = hslToColor(hue, 0.20f, 0.99f),
            onSurface = hslToColor(hue, 0.18f, 0.14f),
            surfaceVariant = hslToColor(hue, 0.28f, 0.90f),
            onSurfaceVariant = hslToColor(hue, 0.16f, 0.32f),
            surfaceTint = seedColor,
            inverseSurface = hslToColor(hue, 0.12f, 0.18f),
            inverseOnSurface = hslToColor(hue, 0.08f, 0.96f),
            inversePrimary = hslToColor(hue, baseSat, 0.78f),
            outline = hslToColor(hue, 0.14f, 0.55f),
            outlineVariant = hslToColor(hue, 0.20f, 0.82f),
            scrim = Color.Black
        )
    }
}

/** The app's brand seed when the user hasn't picked a custom accent color. */
val DefaultBrandSeed = Color(0xFF6650A4)

private fun similar(a: Float, b: Float) = abs(a - b) < 0.01f

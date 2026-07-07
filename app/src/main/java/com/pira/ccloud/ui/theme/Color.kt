package com.pira.ccloud.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Premium, restrained, near-monochromatic palette (2026-2027 flagship
 * streaming direction). Everything reads as tonal variations of
 * white/ivory/silver/slate, with two very subtle blue accents for
 * selection/emphasis - nothing saturated, neon, or "bright" is used
 * anywhere in the app.
 */

// --- Primary neutrals ---
val WarmWhite = Color(0xFFFAF9F7)
val SoftIvory = Color(0xFFF3F1EC)
val LightSilver = Color(0xFFE4E4E7)
val IceGray = Color(0xFFCED2D6)
val MutedBlue = Color(0xFF5C7285)
val SoftSlate = Color(0xFF2B2E33)

// --- Accents (used sparingly: selection state, focus, subtle highlights) ---
val AccentSubtleBlue = Color(0xFF6E8CA8)
val AccentSoftCyan = Color(0xFF7FB3B0)

// --- Dark-mode ink tones (kept in the same cool/warm-neutral family) ---
val DeepInk = Color(0xFF14161A)
val CharcoalSurface = Color(0xFF1B1E23)

/**
 * The app's default brand seed - a soft, desaturated blue, matching the
 * "very subtle blue" accent direction rather than any vivid brand color.
 */
val DefaultBrandSeed = AccentSubtleBlue

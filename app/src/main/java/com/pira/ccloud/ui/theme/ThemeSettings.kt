package com.pira.ccloud.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color

data class ThemeSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val primaryColor: Color = defaultPrimaryColor
    // Removed secondaryColor as it's no longer used
)

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

// Default colors - a soft, desaturated blue rather than a vivid brand hue,
// matching the "mostly monochromatic, subtle blue accent" design system.
val defaultPrimaryColor = DefaultBrandSeed

// Predefined color options - every option is deliberately muted/desaturated
// so no matter which accent someone picks, the interface stays calm rather
// than turning into a saturated, neon-bright theme.
val colorOptions = listOf(
    Color(0xFF6E8CA8), // Subtle Blue (default)
    Color(0xFF7FB3B0), // Soft Cyan
    Color(0xFF5C7285), // Muted Slate Blue
    Color(0xFF8A8677), // Warm Taupe
    Color(0xFF7D8471), // Sage
    Color(0xFF9A8B7A), // Soft Clay
    Color(0xFF6F7A8C), // Cool Slate
    Color(0xFF8E8B99)  // Muted Lavender
)

@Composable
fun rememberThemeSettings(): ThemeSettings {
    return remember { ThemeSettings() }
}
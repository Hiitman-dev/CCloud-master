package com.pira.ccloud.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.pira.ccloud.data.model.FontSettings
import com.pira.ccloud.utils.StorageUtils

/**
 * Typography scale for the premium streaming design system.
 *
 * Weights are restricted to Light (300), Normal (400) and Medium (500) only
 * - the spec explicitly calls for avoiding heavy/bold text so the interface
 * reads as elegant and calm rather than shouty. Large display sizes lean on
 * Light for an airier, more editorial feel; small UI labels use Medium for
 * just enough emphasis to stay legible at size, never heavier.
 */
fun appTypography(fontFamily: FontFamily?): androidx.compose.material3.Typography {
    val family = fontFamily ?: FontFamily.Default
    return androidx.compose.material3.Typography(
        bodyLarge = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.3.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 21.sp,
            letterSpacing = 0.2.sp
        ),
        bodySmall = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 17.sp,
            letterSpacing = 0.2.sp
        ),
        headlineLarge = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Light,
            fontSize = 32.sp,
            lineHeight = 39.sp,
            letterSpacing = 0.sp
        ),
        headlineMedium = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Light,
            fontSize = 28.sp,
            lineHeight = 35.sp,
            letterSpacing = 0.sp
        ),
        headlineSmall = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Normal,
            fontSize = 24.sp,
            lineHeight = 31.sp,
            letterSpacing = 0.sp
        ),
        titleLarge = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Normal,
            fontSize = 22.sp,
            lineHeight = 28.sp,
            letterSpacing = 0.sp
        ),
        titleMedium = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            lineHeight = 23.sp,
            letterSpacing = 0.1.sp
        ),
        titleSmall = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp
        ),
        labelLarge = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp
        ),
        labelMedium = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.3.sp
        ),
        labelSmall = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            lineHeight = 15.sp,
            letterSpacing = 0.3.sp
        )
    )
}

<<<<<<< HEAD
/**
 * App-wide Material3 theme.
 *
 * Deliberately does **not** use `dynamicColorScheme` (Android 12+ wallpaper
 * extraction): the design system calls for a consistent, calm, mostly
 * monochromatic identity, and a color scheme that changes with the user's
 * wallpaper is the opposite of that - two phones side by side would look
 * like different products. Instead every mode (default brand seed, or a
 * user-picked accent from Settings) goes through the same
 * [buildAppColorScheme] derivation so all color roles - including the ones
 * screens never override directly - stay internally consistent.
 */
=======
// Note: SurfaceWarmWhite, SurfacePearl, SurfaceIceGray, SurfaceCloud,
// DarkBackground, DarkSurface, DarkSurfaceElevated, DarkSurfaceMuted,
// TextMutedLight, TextMutedDark are already defined publicly in Color.kt
// (same package) - reused directly here instead of redeclaring them.
private val ThemeTextPrimary    = Color(0xFF1A1C22)
private val ThemeTextOnDark     = Color(0xFFF0F1F5)

private val ThemeAccentBlue     = Color(0xFF5B7FFF)
private val ThemeAccentBlueDark = Color(0xFF7B9AFF)

private val LightColorScheme = lightColorScheme(
    primary = ThemeAccentBlue, onPrimary = Color.White,
    primaryContainer = Color(0xFFE0E7FF), onPrimaryContainer = Color(0xFF1A2550),
    secondary = SurfaceIceGray, onSecondary = ThemeTextPrimary,
    secondaryContainer = SurfaceCloud, onSecondaryContainer = ThemeTextPrimary,
    tertiary = Color(0xFF7C8DAA),
    background = SurfaceWarmWhite, onBackground = ThemeTextPrimary,
    surface = SurfacePearl, onSurface = ThemeTextPrimary,
    surfaceVariant = SurfaceIceGray, onSurfaceVariant = TextMutedLight,
    error = Color(0xFFBA1A1A), onError = Color.White,
    outline = Color(0xFFD0D3DA), outlineVariant = Color(0xFFE4E6EC)
)

private val DarkColorScheme = darkColorScheme(
    primary = ThemeAccentBlueDark, onPrimary = Color(0xFF0D1540),
    primaryContainer = Color(0xFF2A3570), onPrimaryContainer = Color(0xFFD6DFFF),
    secondary = DarkSurfaceElevated, onSecondary = ThemeTextOnDark,
    secondaryContainer = DarkSurfaceMuted, onSecondaryContainer = ThemeTextOnDark,
    tertiary = Color(0xFF8A95AD),
    background = DarkBackground, onBackground = ThemeTextOnDark,
    surface = DarkSurface, onSurface = ThemeTextOnDark,
    surfaceVariant = DarkSurfaceElevated, onSurfaceVariant = TextMutedDark,
    error = Color(0xFFFFB4AB), onError = Color(0xFF690005),
    outline = Color(0xFF3A3D46), outlineVariant = Color(0xFF2A2D36)
)

>>>>>>> 6287ac19c27b480fc114839c05283fe62579b0c5
@Composable
fun CCloudTheme(
    themeSettings: ThemeSettings = ThemeSettings(),
    fontSettings: FontSettings = FontSettings(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val darkTheme = when (themeSettings.themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val fontFamily = FontManager.loadFontFamily(context, fontSettings.fontType)

<<<<<<< HEAD
    val colorScheme = remember(themeSettings.primaryColor, darkTheme) {
        buildAppColorScheme(seed = themeSettings.primaryColor, dark = darkTheme)
=======
    val colorScheme = when {
        themeSettings.primaryColor != defaultPrimaryColor -> {
            if (darkTheme) {
                darkColorScheme(
                    primary = themeSettings.primaryColor, onPrimary = Color.White,
                    primaryContainer = themeSettings.primaryColor.copy(alpha = 0.15f),
                    onPrimaryContainer = themeSettings.primaryColor.copy(alpha = 0.9f),
                    secondary = DarkSurfaceElevated, onSecondary = ThemeTextOnDark,
                    secondaryContainer = DarkSurfaceMuted, onSecondaryContainer = ThemeTextOnDark,
                    tertiary = Color(0xFF8A95AD),
                    background = DarkBackground, onBackground = ThemeTextOnDark,
                    surface = DarkSurface, onSurface = ThemeTextOnDark,
                    surfaceVariant = DarkSurfaceElevated, onSurfaceVariant = TextMutedDark,
                    error = Color(0xFFFFB4AB), onError = Color(0xFF690005),
                    outline = Color(0xFF3A3D46), outlineVariant = Color(0xFF2A2D36)
                )
            } else {
                lightColorScheme(
                    primary = themeSettings.primaryColor, onPrimary = Color.White,
                    primaryContainer = themeSettings.primaryColor.copy(alpha = 0.12f),
                    onPrimaryContainer = themeSettings.primaryColor.copy(alpha = 0.85f),
                    secondary = SurfaceIceGray, onSecondary = ThemeTextPrimary,
                    secondaryContainer = SurfaceCloud, onSecondaryContainer = ThemeTextPrimary,
                    tertiary = Color(0xFF7C8DAA),
                    background = SurfaceWarmWhite, onBackground = ThemeTextPrimary,
                    surface = SurfacePearl, onSurface = ThemeTextPrimary,
                    surfaceVariant = SurfaceIceGray, onSurfaceVariant = TextMutedLight,
                    error = Color(0xFFBA1A1A), onError = Color.White,
                    outline = Color(0xFFD0D3DA), outlineVariant = Color(0xFFE4E6EC)
                )
            }
        }
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
>>>>>>> 6287ac19c27b480fc114839c05283fe62579b0c5
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = appTypography(fontFamily),
        content = content
    )
}

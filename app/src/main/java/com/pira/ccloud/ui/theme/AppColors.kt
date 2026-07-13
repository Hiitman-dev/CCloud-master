package com.pira.ccloud.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Custom semantic color tokens that go beyond Material3's built-in roles.
 *
 * Each token has an independent light and dark value — the two themes are
 * designed as separate palettes, not inversions of each other.
 *
 * Access via `LocalAppColors.current` inside any composable.
 */
@Immutable
data class AppColors(
    // ─── Rating ───────────────────────────────────────────────
    val starGold: Color,
    val starGoldMuted: Color,

    // ─── Stats (content type indicators) ──────────────────────
    val statCoral: Color,
    val statTeal: Color,
    val statAmber: Color,
    val statGreen: Color,

    // ─── Download / Player action buttons ─────────────────────
    val actionOrange: Color,
    val actionBlue: Color,
    val actionGreen: Color,
    val actionPurple: Color,

    // ─── Splash / Welcome ─────────────────────────────────────
    val splashActiveIndicator: Color,
    val splashInactiveIndicator: Color,
    val splashBackground: Color,

    // ─── Status ───────────────────────────────────────────────
    val statusSuccess: Color,
    val statusWarning: Color,
    val statusError: Color,
    val statusInfo: Color,

    // ─── Shadows & overlays ───────────────────────────────────
    val cardShadow: Color,
    val overlayScrim: Color,
    val dividerColor: Color,

    // ─── Elevation surfaces (beyond M3 surfaceContainer) ──────
    val elevatedCard: Color,
    val bottomNavBackground: Color,
    val topBarBackground: Color,
    val bottomSheetBackground: Color,
    val dialogBackground: Color,

    // ─── Media player ─────────────────────────────────────────
    val playerControlBg: Color,
    val playerProgressTrack: Color,
    val playerProgressBuffer: Color,

    // ─── Input fields ─────────────────────────────────────────
    val inputBackground: Color,
    val inputBorder: Color,
    val inputBorderFocused: Color,

    // ─── Skeleton / Loading ───────────────────────────────────
    val skeletonBase: Color,
    val skeletonShimmer: Color,

    // ─── Filter bar / Top navigation ──────────────────────────
    val filterBarBackground: Color,
    val filterBarText: Color,
    val filterBarAccent: Color,
    val filterBarSecondary: Color,
)

val LocalAppColors = staticCompositionLocalOf { lightAppColors() }

// ───────────────────────────────────────────────────────────────
//  LIGHT THEME — "Clean Premium Elegance"
//
//  Warm off-whites, soft shadows, visible tonal separation,
//  airy and refined. Premium flagship quality.
// ───────────────────────────────────────────────────────────────
fun lightAppColors() = AppColors(
    // Rating
    starGold = Color(0xFFFFB300),
    starGoldMuted = Color(0xFFE0CBA8),

    // Stats
    statCoral = Color(0xFFE85D5D),
    statTeal = Color(0xFF3BA89E),
    statAmber = Color(0xFFD4940A),
    statGreen = Color(0xFF43A047),

    // Download action buttons
    actionOrange = Color(0xFFD84315),
    actionBlue = Color(0xFF1565C0),
    actionGreen = Color(0xFF2E7D32),
    actionPurple = Color(0xFF6A1B9A),

    // Splash
    splashActiveIndicator = Color(0xFFFFB300),
    splashInactiveIndicator = Color(0xFFBDBDBD),
    splashBackground = Color(0xFFF8F6F3),

    // Status
    statusSuccess = Color(0xFF43A047),
    statusWarning = Color(0xFFF9A825),
    statusError = Color(0xFFD32F2F),
    statusInfo = Color(0xFF1976D2),

    // Shadows & overlays
    cardShadow = Color(0x1A000000),
    overlayScrim = Color(0x52000000),
    dividerColor = Color(0xFFE0DDD8),

    // Elevation surfaces
    elevatedCard = Color(0xFFF5F2EE),
    bottomNavBackground = Color(0xFFFAFAF7),
    topBarBackground = Color(0xFFFAFAF7),
    bottomSheetBackground = Color(0xFFFFFFFF),
    dialogBackground = Color(0xFFFFFFFF),

    // Media player
    playerControlBg = Color(0xCC1A1A1A),
    playerProgressTrack = Color(0xFFD6D3CE),
    playerProgressBuffer = Color(0xFFBDB9B3),

    // Input fields
    inputBackground = Color(0xFFF3F0EB),
    inputBorder = Color(0xFFD4D0CB),
    inputBorderFocused = Color(0xFF6E8CA8),

    // Skeleton
    skeletonBase = Color(0xFFEDEAE5),
    skeletonShimmer = Color(0xFFF8F5F0),

    // Filter bar
    filterBarBackground = Color(0xFFF5F5F5),
    filterBarText = Color(0xFF1A1A1A),
    filterBarAccent = Color(0xFFC77B4A),
    filterBarSecondary = Color(0xFF757575),
)

// ───────────────────────────────────────────────────────────────
//  DARK THEME — "Deep Premium Depth"
//
//  Deep neutral backgrounds (never pure black), layered surfaces
//  with clear elevation, soft overlays, premium contrast.
//  Designed for reduced eye strain with visual richness.
// ───────────────────────────────────────────────────────────────
fun darkAppColors() = AppColors(
    // Rating — brighter variants for visibility on dark backgrounds
    starGold = Color(0xFFFFCA28),
    starGoldMuted = Color(0xFF8D7B52),

    // Stats — lighter variants for dark-background contrast
    statCoral = Color(0xFFEF7B7B),
    statTeal = Color(0xFF5ECDC4),
    statAmber = Color(0xFFFFD54F),
    statGreen = Color(0xFF66BB6A),

    // Download action buttons — lighter tints for dark mode
    actionOrange = Color(0xFFFF8A65),
    actionBlue = Color(0xFF64B5F6),
    actionGreen = Color(0xFF81C784),
    actionPurple = Color(0xFFBA68C8),

    // Splash
    splashActiveIndicator = Color(0xFFFFCA28),
    splashInactiveIndicator = Color(0xFF616161),
    splashBackground = Color(0xFF121214),

    // Status — brighter variants for dark-background contrast
    statusSuccess = Color(0xFF66BB6A),
    statusWarning = Color(0xFFFFD54F),
    statusError = Color(0xFFEF5350),
    statusInfo = Color(0xFF64B5F6),

    // Shadows & overlays — no shadow in dark mode; use surface contrast
    cardShadow = Color(0x00000000),
    overlayScrim = Color(0x8A000000),
    dividerColor = Color(0xFF2A2D33),

    // Elevation surfaces — distinct layered depth
    elevatedCard = Color(0xFF1E2028),
    bottomNavBackground = Color(0xFF16181D),
    topBarBackground = Color(0xFF16181D),
    bottomSheetBackground = Color(0xFF1E2028),
    dialogBackground = Color(0xFF252830),

    // Media player
    playerControlBg = Color(0xB3000000),
    playerProgressTrack = Color(0xFF3A3D45),
    playerProgressBuffer = Color(0xFF2A2D35),

    // Input fields
    inputBackground = Color(0xFF1E2028),
    inputBorder = Color(0xFF3A3D45),
    inputBorderFocused = Color(0xFF8EAFCC),

    // Skeleton
    skeletonBase = Color(0xFF1E2028),
    skeletonShimmer = Color(0xFF282B33),

    // Filter bar — AMOLED-optimized deep dark
    filterBarBackground = Color(0xFF0E0E0E),
    filterBarText = Color(0xFFFFFFFF),
    filterBarAccent = Color(0xFFC77B4A),
    filterBarSecondary = Color(0xFFBDBDBD),
)

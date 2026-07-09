package com.pira.ccloud.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.pira.ccloud.ui.theme.GlassCorners
import com.pira.ccloud.ui.theme.liquidGlass

/**
<<<<<<< HEAD
 * Floating pill-shaped Liquid Glass bottom navigation bar.
 *
 * Design:
 *  - Pill shape with generous corner radius (999dp)
 *  - Floats above the bottom edge with margin on all sides
 *  - Semi-transparent frosted glass background with gradient fill
 *  - Thin bright top-edge highlight (glass refraction rim)
 *  - Soft outer drop shadow for physical depth
 *  - Selected icon gets a small pill/bubble highlight background
 *  - Unselected icons are lower-contrast
 *  - Spring-based animations on selection changes
=======
 * Telegram-inspired floating pill navigation bar.
 *
 * Semi-opaque glass surface, rounded capsule (32px), soft ambient shadow,
 * thin 1px border. Height ~80dp. No heavy blur or liquid effects.
>>>>>>> 03d9d8ea365ac7c4ed6ac59077927b3f93b49314
 */
@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
<<<<<<< HEAD
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
=======
    val glassTint = rememberGlassTint()
>>>>>>> 03d9d8ea365ac7c4ed6ac59077927b3f93b49314

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
<<<<<<< HEAD
            .padding(horizontal = 20.dp, vertical = 12.dp)
=======
<<<<<<< HEAD
            .padding(horizontal = 20.dp, vertical = 8.dp)
=======
            .padding(horizontal = 16.dp, vertical = 8.dp)
>>>>>>> 16bb46ea3318e8f7e2ba73e2f974008e3b01c44d
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(GlassCorners.Navigation),
<<<<<<< HEAD
                ambientColor = Color.Black.copy(alpha = 0.06f),
                spotColor = Color.Black.copy(alpha = 0.04f)
=======
                ambientColor = Color.Black.copy(alpha = 0.1f),
                spotColor = Color.Black.copy(alpha = 0.1f)
>>>>>>> 16bb46ea3318e8f7e2ba73e2f974008e3b01c44d
            )
            .glassSurface(
                shape = RoundedCornerShape(GlassCorners.Navigation),
                tint = glassTint,
<<<<<<< HEAD
                tintAlpha = 0.42f,
                borderAlpha = 0.28f
            ),
        shape = RoundedCornerShape(GlassCorners.Navigation),
        color = Color.Transparent,
        tonalElevation = 0.dp
=======
                // Stronger glass effect with higher alpha for better visibility
                tintAlpha = 0.55f,
                borderAlpha = 0.38f
            )
>>>>>>> 16bb46ea3318e8f7e2ba73e2f974008e3b01c44d
>>>>>>> 03d9d8ea365ac7c4ed6ac59077927b3f93b49314
    ) {
        // Glass pill container
        Box(
            modifier = Modifier
                .fillMaxWidth()
<<<<<<< HEAD
                .liquidGlass(
                    shape = RoundedCornerShape(GlassCorners.Pill),
                    isDark = isDark,
                    intensity = com.pira.ccloud.ui.theme.GlassIntensity.Chrome
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppScreens.screens.filter { it.showBottomBar }.forEach { screen ->
                    val isSelected = currentRoute == screen.route

                    // Spring-animated scale for selected icon
                    val scale by animateFloatAsState(
                        targetValue = if (isSelected) 1.0f else 0.88f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        label = "icon_scale"
                    )

                    // Animated icon color
                    val iconColor by animateColorAsState(
                        targetValue = if (isSelected)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        animationSpec = tween(durationMillis = 250),
                        label = "icon_color"
                    )
=======
                .padding(horizontal = 6.dp, vertical = 6.dp),
        ) {
            AppScreens.screens.filter { it.showBottomBar }.forEach { screen ->
                val isSelected = currentRoute == screen.route
                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.08f else 1f,
                    animationSpec = spring(),
                    label = "scale"
                )
                val iconColor by animateColorAsState(
                    targetValue = if (isSelected)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    animationSpec = tween(durationMillis = 220),
                    label = "iconColor"
                )
                val textColor by animateColorAsState(
                    targetValue = if (isSelected)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    animationSpec = tween(durationMillis = 220),
                    label = "textColor"
                )
>>>>>>> 03d9d8ea365ac7c4ed6ac59077927b3f93b49314

                    // Animated text color
                    val textColor by animateColorAsState(
                        targetValue = if (isSelected)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        animationSpec = tween(durationMillis = 250),
                        label = "text_color"
                    )

                    // Animated pill highlight width
                    val highlightWidth by animateDpAsState(
                        targetValue = if (isSelected) 56.dp else 0.dp,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        ),
                        label = "highlight_width"
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(
                                interactionSource = remember(screen.route) { MutableInteractionSource() },
                                indication = null
                            ) {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        launchSingleTop = true
                                        restoreState = true
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                    }
                                }
                            }
<<<<<<< HEAD
                            .semantics {
                                role = Role.Tab
                                selected = isSelected
                            }
                            .padding(vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        // Icon container with pill highlight
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .scale(scale),
                            contentAlignment = Alignment.Center
                        ) {
                            // Animated pill highlight behind selected icon
                            if (isSelected) {
                                Surface(
                                    modifier = Modifier
                                        .width(highlightWidth)
                                        .height(28.dp),
                                    shape = RoundedCornerShape(GlassCorners.Pill),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                ) {}
                            }
                            Icon(
                                imageVector = screen.icon ?: Icons.Default.Movie,
                                contentDescription = stringResource(screen.resourceId),
                                tint = iconColor,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        // Label text
                        Text(
                            text = stringResource(screen.resourceId),
                            color = textColor,
                            fontSize = MaterialTheme.typography.labelSmall.fontSize,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            maxLines = 1
                        )
                    }
=======
                        }
                        .semantics {
                            role = Role.Tab
                            selected = isSelected
                        }
                        .padding(vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .scale(scale),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Surface(
                                modifier = Modifier.size(38.dp),
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
                            ) {}
                        }
                        Icon(
                            imageVector = screen.icon ?: Icons.Default.Movie,
                            contentDescription = stringResource(screen.resourceId),
                            tint = iconColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Text(
                        text = stringResource(screen.resourceId),
                        color = textColor,
                        fontSize = MaterialTheme.typography.labelSmall.fontSize,
                        fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Light,
                        maxLines = 1
                    )
>>>>>>> 03d9d8ea365ac7c4ed6ac59077927b3f93b49314
                }
            }
        }
    }
}

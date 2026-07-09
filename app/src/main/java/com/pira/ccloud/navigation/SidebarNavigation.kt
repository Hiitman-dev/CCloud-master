package com.pira.ccloud.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.pira.ccloud.ui.theme.GlassCorners
import com.pira.ccloud.ui.theme.liquidGlass

/**
 * Floating Liquid Glass sidebar navigation for TV.
 *
 * Sits as a vertical pill column on the left edge with the same
 * glassmorphism treatment as the bottom bar — translucent fill,
 * bright edge highlight, soft shadow.
 */
@Composable
fun SidebarNavigation(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(100.dp)
            .padding(top = 24.dp, bottom = 24.dp, start = 12.dp)
            .liquidGlass(
                shape = RoundedCornerShape(GlassCorners.Navigation),
                isDark = isDark,
                intensity = com.pira.ccloud.ui.theme.GlassIntensity.Chrome
            )
            .padding(horizontal = 8.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppScreens.screens.filter { it.showSidebar }.forEach { screen ->
                val isSelected = currentRoute == screen.route

                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.08f else 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "scale"
                )

                val iconColor by animateColorAsState(
                    targetValue = if (isSelected)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f),
                    animationSpec = tween(durationMillis = 250),
                    label = "iconColor"
                )

                val textColor by animateColorAsState(
                    targetValue = if (isSelected)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    animationSpec = tween(durationMillis = 250),
                    label = "textColor"
                )

                val highlightWidth by animateDpAsState(
                    targetValue = if (isSelected) 68.dp else 0.dp,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    ),
                    label = "highlight_width"
                )

                NavigationRailItem(
                    icon = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .scale(scale),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Surface(
                                        modifier = Modifier
                                            .width(highlightWidth)
                                            .height(36.dp),
                                        shape = RoundedCornerShape(GlassCorners.Pill),
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                    ) {}
                                }
                                Icon(
                                    imageVector = screen.icon ?: Icons.Default.Movie,
                                    contentDescription = stringResource(screen.resourceId),
                                    tint = iconColor,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Text(
                                text = stringResource(screen.resourceId),
                                color = textColor,
                                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                maxLines = 1
                            )
                        }
                    },
                    label = null,
                    selected = isSelected,
                    onClick = {
                        if (currentRoute != screen.route) {
                            navController.navigate(screen.route) {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                            }
                        }
                    },
                    colors = NavigationRailItemDefaults.colors(
                        selectedIconColor = Color.Transparent,
                        unselectedIconColor = Color.Transparent,
                        selectedTextColor = Color.Transparent,
                        unselectedTextColor = Color.Transparent,
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

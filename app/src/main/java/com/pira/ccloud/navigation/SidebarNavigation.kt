package com.pira.ccloud.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun SidebarNavigation(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationRail(
        modifier = Modifier
            .fillMaxHeight()
            .width(100.dp)
            .padding(top = 24.dp, bottom = 24.dp),
        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
        header = {
            Spacer(modifier = Modifier.height(16.dp))
        }
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(28.dp)
            ) {
                AppScreens.bottomNavScreens.forEach { screen ->
                    val isSelected = currentRoute == screen.route
                    val scale by animateFloatAsState(
                        targetValue = if (isSelected) 1.1f else 1f,
                        animationSpec = tween(durationMillis = 200),
                        label = "scale"
                    )

                    val iconColor by animateColorAsState(
                        targetValue = if (isSelected)
                            androidx.compose.material3.MaterialTheme.colorScheme.primary
                        else
                            androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                        animationSpec = tween(durationMillis = 200),
                        label = "iconColor"
                    )

                    val textColor by animateColorAsState(
                        targetValue = if (isSelected)
                            androidx.compose.material3.MaterialTheme.colorScheme.primary
                        else
                            androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                        animationSpec = tween(durationMillis = 200),
                        label = "textColor"
                    )

                    NavigationRailItem(
                        icon = {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(horizontal = 22.dp)
                            ) {
                                androidx.compose.foundation.layout.Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .scale(scale),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        androidx.compose.material3.Surface(
                                            modifier = Modifier.size(48.dp),
                                            shape = CircleShape,
                                            color = androidx.compose.material3.MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                        ) {}
                                    }
                                    Icon(
                                        imageVector = screen.icon ?: Icons.Default.Movie,
                                        contentDescription = stringResource(screen.resourceId),
                                        tint = iconColor,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                                Text(
                                    text = stringResource(screen.resourceId),
                                    color = textColor,
                                    fontSize = androidx.compose.material3.MaterialTheme.typography.labelMedium.fontSize,
                                    fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
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
                            selectedIconColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                            unselectedIconColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                            selectedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                            unselectedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

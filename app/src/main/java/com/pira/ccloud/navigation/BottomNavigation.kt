package com.pira.ccloud.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.pira.ccloud.ui.theme.glassSurface
import com.pira.ccloud.ui.theme.rememberGlassTint

/**
 * A fully custom floating pill nav bar - drawn as our own Row inside a glass
 * Box - instead of Material3's `NavigationBar`. `NavigationBar` draws its own
 * rectangular `Surface` internally for elevation/shadow, which showed through
 * as a hard square edge under our rounded clip. Building it ourselves means
 * the glass panel's shape is the *only* shape being drawn - a true floating
 * capsule that sits clear of the screen edges, with an opaque-enough tint
 * that it doesn't let the movie posters/list bleed through and confuse
 * which tab is selected.
 */
@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val glassTint = rememberGlassTint()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .glassSurface(
                shape = RoundedCornerShape(32.dp),
                tint = glassTint,
                // Stronger than the default so posters/lists behind it never
                // bleed through enough to interfere with reading the tab bar.
                tintAlpha = 0.82f,
                borderAlpha = 0.5f
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 6.dp),
        ) {
            AppScreens.screens.filter { it.showBottomBar }.forEach { screen ->
                val isSelected = currentRoute == screen.route
                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.1f else 1f,
                    animationSpec = tween(durationMillis = 200),
                    label = "scale"
                )

                val iconColor by animateColorAsState(
                    targetValue = if (isSelected)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    animationSpec = tween(durationMillis = 200),
                    label = "iconColor"
                )

                val textColor by animateColorAsState(
                    targetValue = if (isSelected)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    animationSpec = tween(durationMillis = 200),
                    label = "textColor"
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
                        .semantics {
                            role = Role.Tab
                            selected = isSelected
                        }
                        .padding(vertical = 6.dp),
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
                                modifier = Modifier.size(34.dp),
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            ) {}
                        }
                        Icon(
                            imageVector = screen.icon ?: Icons.Default.Movie,
                            contentDescription = stringResource(screen.resourceId),
                            tint = iconColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Text(
                        text = stringResource(screen.resourceId),
                        color = textColor,
                        fontSize = MaterialTheme.typography.labelMedium.fontSize,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

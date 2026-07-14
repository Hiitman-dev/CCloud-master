package com.pira.ccloud.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Animated spacer between settings cards with staggered reveal.
 */
@Composable
fun SettingsSpacer(delayMillis: Int) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn(animationSpec = tween(delayMillis)) + slideInVertically(animationSpec = tween(delayMillis)),
    ) {
        Spacer(modifier = Modifier.height(16.dp))
    }
}

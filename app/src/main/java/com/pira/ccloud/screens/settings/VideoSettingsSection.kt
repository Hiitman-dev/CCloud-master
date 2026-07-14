package com.pira.ccloud.screens.settings

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.pira.ccloud.data.model.SubtitleSettings
import com.pira.ccloud.data.model.VideoPlayerSettings
import com.pira.ccloud.ui.theme.glassSurface
import com.pira.ccloud.ui.theme.rememberGlassTint

@Composable
fun VideoSettingsSection(
    videoPlayerSettings: VideoPlayerSettings,
    subtitleSettings: SubtitleSettings,
    onVideoPlayerSettingsChanged: (VideoPlayerSettings) -> Unit,
    onSubtitleSettingsChanged: (SubtitleSettings) -> Unit,
    modifier: Modifier = Modifier,
    nextFocusRequester: FocusRequester? = null
) {
    var isExpanded by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val glassTint = rememberGlassTint()
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .glassSurface(shape = RoundedCornerShape(20.dp), tint = glassTint)
            .clickable { isExpanded = !isExpanded }
            .focusable()
            .focusRequester(focusRequester)
            .focusProperties {
                nextFocusRequester?.let { down = it }
            }
            .onKeyEvent { keyEvent ->
                when (keyEvent.key) {
                    Key.Enter, Key.Spacebar -> {
                        isExpanded = !isExpanded
                        true
                    }
                    else -> false
                }
            },
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.TextFields,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Video Player Settings",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(1f)
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Seek Time: ${videoPlayerSettings.seekTimeSeconds} seconds",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Slider(
                        value = videoPlayerSettings.seekTimeSeconds.toFloat(),
                        onValueChange = { seconds ->
                            onVideoPlayerSettingsChanged(videoPlayerSettings.copy(seekTimeSeconds = seconds.toInt()))
                        },
                        valueRange = 5f..30f,
                        steps = 24,
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusable()
                            .onKeyEvent { keyEvent ->
                                when (keyEvent.key) {
                                    Key.DirectionLeft -> {
                                        val newValue = (videoPlayerSettings.seekTimeSeconds - 1).coerceIn(5, 30).toFloat()
                                        onVideoPlayerSettingsChanged(videoPlayerSettings.copy(seekTimeSeconds = newValue.toInt()))
                                        true
                                    }
                                    Key.DirectionRight -> {
                                        val newValue = (videoPlayerSettings.seekTimeSeconds + 1).coerceIn(5, 30).toFloat()
                                        onVideoPlayerSettingsChanged(videoPlayerSettings.copy(seekTimeSeconds = newValue.toInt()))
                                        true
                                    }
                                    else -> false
                                }
                            }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Subtitle Settings Section
                    Text(
                        text = "Subtitle Settings",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Text color setting
                    SubtitleColorSetting(
                        title = "Text Color",
                        currentColor = Color(subtitleSettings.textColor),
                        onColorSelected = { color ->
                            onSubtitleSettingsChanged(subtitleSettings.copy(textColor = android.graphics.Color.argb(
                                (color.alpha * 255).toInt(),
                                (color.red * 255).toInt(),
                                (color.green * 255).toInt(),
                                (color.blue * 255).toInt()
                            )))
                        },
                        defaultColor = Color.Yellow
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Border color setting
                    SubtitleColorSetting(
                        title = "Background Color",
                        currentColor = Color(subtitleSettings.borderColor),
                        onColorSelected = { color ->
                            onSubtitleSettingsChanged(subtitleSettings.copy(borderColor = android.graphics.Color.argb(
                                (color.alpha * 255).toInt(),
                                (color.red * 255).toInt(),
                                (color.green * 255).toInt(),
                                (color.blue * 255).toInt()
                            )))
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Text size setting
                    Text(
                        text = "Text Size: ${subtitleSettings.textSize.toInt()}sp",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Slider(
                        value = subtitleSettings.textSize,
                        onValueChange = { size ->
                            onSubtitleSettingsChanged(subtitleSettings.copy(textSize = size))
                        },
                        valueRange = 10f..50f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusable()
                            .onKeyEvent { keyEvent ->
                                when (keyEvent.key) {
                                    Key.DirectionLeft -> {
                                        val newValue = (subtitleSettings.textSize - 1).coerceIn(10f, 50f)
                                        onSubtitleSettingsChanged(subtitleSettings.copy(textSize = newValue))
                                        true
                                    }
                                    Key.DirectionRight -> {
                                        val newValue = (subtitleSettings.textSize + 1).coerceIn(10f, 50f)
                                        onSubtitleSettingsChanged(subtitleSettings.copy(textSize = newValue))
                                        true
                                    }
                                    else -> false
                                }
                            }
                    )
                }
            }
        }
    }
}

@Composable
private fun SubtitleColorSetting(
    title: String,
    currentColor: Color,
    onColorSelected: (Color) -> Unit,
    defaultColor: Color = Color.Yellow
) {
    val context = LocalContext.current

    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Color preview
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(currentColor)
                    .clickable {
                        // Simple color picker - cycle through preset colors
                        val colors = listOf(
                            Color.Yellow, Color.White, Color.Cyan, Color.Magenta,
                            Color.Red, Color.Green, Color.Blue, defaultColor
                        )
                        val currentIndex = colors.indexOfFirst {
                            it.red == currentColor.red &&
                            it.green == currentColor.green &&
                            it.blue == currentColor.blue
                        }
                        val nextIndex = (currentIndex + 1) % colors.size
                        onColorSelected(colors[nextIndex])
                    }
            )
            Text(
                text = "Tap to change",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 12.dp)
            )
        }
    }
}

package com.pira.ccloud.screens.settings

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.FormatColorFill
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.unit.dp
import com.pira.ccloud.ui.theme.ColorPickerCanvas
import com.pira.ccloud.ui.theme.ThemeMode
import com.pira.ccloud.ui.theme.ThemeSettings
import com.pira.ccloud.ui.theme.colorOptions
import com.pira.ccloud.ui.theme.defaultPrimaryColor
import com.pira.ccloud.ui.theme.glassSurface
import com.pira.ccloud.ui.theme.rememberGlassTint

@Composable
fun ThemeSettingsSection(
    themeSettings: ThemeSettings,
    onThemeSettingsChanged: (ThemeSettings) -> Unit,
    modifier: Modifier = Modifier,
    nextFocusRequester: FocusRequester? = null
) {
    var isExpanded by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val glassTint = rememberGlassTint()

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
                    imageVector = Icons.Default.FormatColorFill,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Theme Settings",
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
                    // Theme Mode Section
                    Text(
                        text = "Theme Mode",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    ThemeModeOption(
                        mode = ThemeMode.LIGHT,
                        label = "Light",
                        isSelected = themeSettings.themeMode == ThemeMode.LIGHT,
                        onSelect = { mode -> onThemeSettingsChanged(themeSettings.copy(themeMode = mode)) }
                    )

                    ThemeModeOption(
                        mode = ThemeMode.DARK,
                        label = "Dark",
                        isSelected = themeSettings.themeMode == ThemeMode.DARK,
                        onSelect = { mode -> onThemeSettingsChanged(themeSettings.copy(themeMode = mode)) }
                    )

                    ThemeModeOption(
                        mode = ThemeMode.SYSTEM,
                        label = "System Default",
                        isSelected = themeSettings.themeMode == ThemeMode.SYSTEM,
                        onSelect = { mode -> onThemeSettingsChanged(themeSettings.copy(themeMode = mode)) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Primary Color Section
                    Text(
                        text = "Primary Color",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Display color options in rows of 4
                    for (rowColors in colorOptions.chunked(4)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
                        ) {
                            rowColors.forEach { color ->
                                androidx.compose.foundation.layout.Box(
                                    modifier = Modifier.weight(1f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    ColorOptionButton(
                                        color = color,
                                        isSelected = themeSettings.primaryColor == color,
                                        onSelect = { selectedColor ->
                                            onThemeSettingsChanged(themeSettings.copy(primaryColor = selectedColor))
                                        }
                                    )
                                }
                            }
                            // Fill remaining spaces if less than 4 items
                            repeat(4 - rowColors.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }

                    // Default color option
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Start
                    ) {
                        androidx.compose.foundation.layout.Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            ColorOptionButton(
                                color = defaultPrimaryColor,
                                isSelected = themeSettings.primaryColor == defaultPrimaryColor,
                                onSelect = { selectedColor ->
                                    onThemeSettingsChanged(themeSettings.copy(primaryColor = selectedColor))
                                },
                                label = "Default"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Custom color canvas
                    Text(
                        text = "Custom Color",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    ColorPickerCanvas(
                        initialColor = themeSettings.primaryColor,
                        onColorConfirmed = { pickedColor ->
                            onThemeSettingsChanged(themeSettings.copy(primaryColor = pickedColor))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemeModeOption(
    mode: ThemeMode,
    label: String,
    isSelected: Boolean,
    onSelect: (ThemeMode) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect(mode) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = { onSelect(mode) }
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
private fun ColorOptionButton(
    color: Color,
    isSelected: Boolean,
    onSelect: (Color) -> Unit,
    label: String? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onSelect(color) }
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .then(
                    if (isSelected) {
                        Modifier.padding(2.dp)
                    } else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FormatColorFill,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color)
            )
        }
        if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

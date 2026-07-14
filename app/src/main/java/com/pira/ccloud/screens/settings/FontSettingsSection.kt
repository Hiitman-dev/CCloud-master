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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.TextFields
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.unit.dp
import com.pira.ccloud.data.model.FontSettings
import com.pira.ccloud.data.model.FontType
import com.pira.ccloud.ui.theme.glassSurface
import com.pira.ccloud.ui.theme.rememberGlassTint

@Composable
fun FontSettingsSection(
    fontSettings: FontSettings,
    onFontSettingsChanged: (FontSettings) -> Unit,
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
                    imageVector = Icons.Default.TextFields,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Font Settings",
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
                        text = "Select Font",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    FontOption(
                        fontType = FontType.DEFAULT,
                        label = "System Default",
                        isSelected = fontSettings.fontType == FontType.DEFAULT,
                        onSelect = { fontType ->
                            onFontSettingsChanged(fontSettings.copy(fontType = fontType))
                        }
                    )

                    FontOption(
                        fontType = FontType.VAZIRMATN,
                        label = "Vazirmatn",
                        isSelected = fontSettings.fontType == FontType.VAZIRMATN,
                        onSelect = { fontType ->
                            onFontSettingsChanged(fontSettings.copy(fontType = fontType))
                        }
                    )

                    FontOption(
                        fontType = FontType.YEKAN_BAKH,
                        label = "Yekan Bakh",
                        isSelected = fontSettings.fontType == FontType.YEKAN_BAKH,
                        onSelect = { fontType ->
                            onFontSettingsChanged(fontSettings.copy(fontType = fontType))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun FontOption(
    fontType: FontType,
    label: String,
    isSelected: Boolean,
    onSelect: (FontType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect(fontType) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = { onSelect(fontType) }
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

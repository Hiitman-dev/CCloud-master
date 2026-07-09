package com.pira.ccloud.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.pira.ccloud.data.model.Source
import com.pira.ccloud.ui.theme.GlassCorners
import com.pira.ccloud.ui.theme.GlassAlertDialog
import com.pira.ccloud.utils.DownloadUtils

@Composable
fun DownloadOptionsDialog(
    source: Source,
    onDismiss: () -> Unit,
    onCopyLink: () -> Unit,
    onDownloadWithBrowser: () -> Unit,
    onDownloadWithADM: () -> Unit,
    onOpenInVLC: () -> Unit,
    onOpenInMXPlayer: () -> Unit,
    onOpenInKMPlayer: () -> Unit
) {
    GlassAlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Download & Play Options",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
            )
        },
        text = {
            Text(
                text = "Choose how to handle this video",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Download Options",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = { onCopyLink(); onDismiss() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(GlassCorners.Button),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) { Text("Copy Link") }
                Button(
                    onClick = { onDownloadWithBrowser(); onDismiss() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(GlassCorners.Button),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) { Text("Download with Browser") }
                Button(
                    onClick = { onDownloadWithADM(); onDismiss() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(GlassCorners.Button),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) { Text("Download with ADM") }

                Text(
                    text = "Play Options",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = { onOpenInVLC(); onDismiss() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(GlassCorners.Button),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) { Text("Open in VLC Player") }
                Button(
                    onClick = { onOpenInMXPlayer(); onDismiss() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(GlassCorners.Button),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) { Text("Open in MX Player") }
                Button(
                    onClick = { onOpenInKMPlayer(); onDismiss() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(GlassCorners.Button),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) { Text("Open in KM Player") }
                TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) { Text("Cancel") }
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(GlassCorners.Dialog),
        tonalElevation = 0.dp
    )
}

@Composable
fun CopyLinksButton(sources: List<Source>, modifier: Modifier = Modifier) {
    var showDialog by remember { mutableStateOf(false) }
    OutlinedButton(
        onClick = { showDialog = true },
        modifier = modifier,
        shape = RoundedCornerShape(GlassCorners.Button),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
    ) {
        Icon(Icons.Default.ContentCopy, contentDescription = "Copy Links", modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text("Copy Links")
    }
    if (showDialog) CopySelectedLinksDialog(sources, { showDialog = false })
}

@Composable
fun CopySelectedLinksDialog(sources: List<Source>, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val checkedState = remember {
        mutableStateMapOf<Int, Boolean>().apply {
            sources.forEach { put(it.id, true) }
        }
    }
    GlassAlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Copy Selected Links",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("Choose which qualities to copy", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(4.dp))
                sources.forEach { source ->
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = checkedState[source.id] ?: false,
                            onCheckedChange = { checkedState[source.id] = it },
                            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(source.quality, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val selectedLinks = sources.filter { checkedState[it.id] == true }.map { it.url }
                    if (selectedLinks.isNotEmpty()) DownloadUtils.copyMultipleToClipboard(context, selectedLinks.joinToString("\n"))
                    onDismiss()
                },
                shape = RoundedCornerShape(GlassCorners.Button),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer)
            ) { Text("Copy") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(GlassCorners.Dialog),
        tonalElevation = 0.dp
    )
}

@Composable
fun CopySeasonLinksButton(episodes: List<com.pira.ccloud.data.model.Episode>, modifier: Modifier = Modifier) {
    var showDialog by remember { mutableStateOf(false) }
    OutlinedButton(
        onClick = { showDialog = true },
        modifier = modifier,
        shape = RoundedCornerShape(GlassCorners.Button),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
    ) {
        Icon(Icons.Default.ContentCopy, contentDescription = "Copy Season Links", modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text("Copy Season Links")
    }
    if (showDialog) CopySeasonLinksDialog(episodes, { showDialog = false })
}

@Composable
fun CopySeasonLinksDialog(episodes: List<com.pira.ccloud.data.model.Episode>, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val allQualities = remember(episodes) {
        episodes.flatMap { it.sources.map { source -> source.quality } }.distinct()
    }
    val checkedState = remember {
        mutableStateMapOf<String, Boolean>().apply {
            allQualities.forEach { put(it, true) }
        }
    }
    GlassAlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Copy Season Links",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("Pick which qualities to copy across all ${episodes.size} episodes", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(4.dp))
                if (allQualities.isEmpty()) Text("No downloadable links found in this season.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
                allQualities.forEach { quality ->
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = checkedState[quality] ?: false,
                            onCheckedChange = { checkedState[quality] = it },
                            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(quality, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val selectedQualities = allQualities.filter { checkedState[it] == true }.toSet()
                    val selectedLinks = episodes.flatMap { episode ->
                        episode.sources.filter { it.quality in selectedQualities }.map { it.url }
                    }
                    if (selectedLinks.isNotEmpty()) DownloadUtils.copyMultipleToClipboard(context, selectedLinks.joinToString("\n"))
                    onDismiss()
                },
                shape = RoundedCornerShape(GlassCorners.Button),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer)
            ) { Text("Copy") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(GlassCorners.Dialog),
        tonalElevation = 0.dp
    )
}

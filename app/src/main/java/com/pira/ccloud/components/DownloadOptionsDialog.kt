package com.pira.ccloud.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
<<<<<<< HEAD
=======
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
>>>>>>> 2541a1adf58b55ec85598c2da3096e5129b30f0b
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
<<<<<<< HEAD
import androidx.compose.material3.MaterialTheme
=======
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
>>>>>>> 2541a1adf58b55ec85598c2da3096e5129b30f0b
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pira.ccloud.data.model.Source
<<<<<<< HEAD
=======
import com.pira.ccloud.ui.theme.GlassAlertDialog
import com.pira.ccloud.ui.theme.GlassCorners
import com.pira.ccloud.utils.DownloadUtils
>>>>>>> 2541a1adf58b55ec85598c2da3096e5129b30f0b

/**
 * The download & play options panel, per spec: a bottom sheet, not a
 * centered dialog. `ModalBottomSheet` already gives us the Material Overlay
 * behavior the spec asks for - the page behind it is dimmed and scroll is
 * frozen - so the sheet itself becomes the single primary layer, with no
 * glass card nested inside another container.
 */
@OptIn(ExperimentalMaterial3Api::class)
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
<<<<<<< HEAD
    AlertDialog(
=======
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
>>>>>>> 2541a1adf58b55ec85598c2da3096e5129b30f0b
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Grab handle
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 12.dp)
                    .size(width = 36.dp, height = 4.dp)
                    .background(
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                        RoundedCornerShape(GlassCorners.Tag)
                    )
            )

            Text(
                text = "Download & Play Options",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
            )
            Text(
                text = "Choose how to handle this video",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Download Options",
                style = MaterialTheme.typography.titleSmall,
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
            ) {
                Text("Copy Link")
            }
<<<<<<< HEAD
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 6.dp
    )
}
=======

            Button(
                onClick = { onDownloadWithBrowser(); onDismiss() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(GlassCorners.Button),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Text("Download with Browser")
            }

            Button(
                onClick = { onDownloadWithADM(); onDismiss() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(GlassCorners.Button),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Text("Download with ADM")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Play Options",
                style = MaterialTheme.typography.titleSmall,
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
            ) {
                Text("Open in VLC Player")
            }

            Button(
                onClick = { onOpenInMXPlayer(); onDismiss() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(GlassCorners.Button),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Text("Open in MX Player")
            }

            Button(
                onClick = { onOpenInKMPlayer(); onDismiss() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(GlassCorners.Button),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Text("Open in KM Player")
            }

            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel")
            }
        }
    }
}
/**
 * FEATURE: "Copy Selected Links"
 * A stylized M3 button that, when tapped, opens a multi-select dialog listing every
 * available quality for the current media/episode so the user can copy several
 * direct URLs at once (newline separated) to the system clipboard.
 */
@Composable
fun CopyLinksButton(
    sources: List<Source>,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    OutlinedButton(
        onClick = { showDialog = true },
        modifier = modifier,
        shape = RoundedCornerShape(GlassCorners.Button),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Icon(
            imageVector = Icons.Default.ContentCopy,
            contentDescription = "Copy Links",
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Copy Links")
    }

    if (showDialog) {
        CopySelectedLinksDialog(
            sources = sources,
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
fun CopySelectedLinksDialog(
    sources: List<Source>,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    // Default every quality to selected for convenience.
    val checkedState = remember { mutableStateMapOf<Int, Boolean>().apply {
        sources.forEach { put(it.id, true) }
    } }

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
                Text(
                    text = "Choose which qualities to copy",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                sources.forEach { source ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = checkedState[source.id] ?: false,
                            onCheckedChange = { isChecked ->
                                checkedState[source.id] = isChecked
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = source.quality,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val selectedLinks = sources
                        .filter { checkedState[it.id] == true }
                        .map { it.url }
                    if (selectedLinks.isNotEmpty()) {
                        DownloadUtils.copyMultipleToClipboard(context, selectedLinks.joinToString("\n"))
                    }
                    onDismiss()
                },
                shape = RoundedCornerShape(GlassCorners.Button),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Text("Copy")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(GlassCorners.Card),
        tonalElevation = 6.dp
    )
}

/**
 * FEATURE: "Copy Season Links"
 * Unlike [CopyLinksButton] (single episode), this gathers every episode's
 * links for the whole season, grouped by quality: check a quality and it
 * pulls that quality's URL from every episode in the season that has one.
 */
@Composable
fun CopySeasonLinksButton(
    episodes: List<com.pira.ccloud.data.model.Episode>,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    OutlinedButton(
        onClick = { showDialog = true },
        modifier = modifier,
        shape = RoundedCornerShape(GlassCorners.Button),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Icon(
            imageVector = Icons.Default.ContentCopy,
            contentDescription = "Copy Season Links",
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Copy Season Links")
    }

    if (showDialog) {
        CopySeasonLinksDialog(
            episodes = episodes,
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
fun CopySeasonLinksDialog(
    episodes: List<com.pira.ccloud.data.model.Episode>,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    // Every distinct quality label that appears across the season's episodes.
    val allQualities = remember(episodes) {
        episodes.flatMap { it.sources.map { source -> source.quality } }.distinct()
    }
    val checkedState = remember { mutableStateMapOf<String, Boolean>().apply {
        allQualities.forEach { put(it, true) }
    } }

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
                Text(
                    text = "Pick which qualities to copy across all ${episodes.size} episodes",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (allQualities.isEmpty()) {
                    Text(
                        text = "No downloadable links found in this season.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                allQualities.forEach { quality ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = checkedState[quality] ?: false,
                            onCheckedChange = { isChecked ->
                                checkedState[quality] = isChecked
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = quality,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val selectedQualities = allQualities.filter { checkedState[it] == true }.toSet()
                    val selectedLinks = episodes.flatMap { episode ->
                        episode.sources
                            .filter { it.quality in selectedQualities }
                            .map { it.url }
                    }
                    if (selectedLinks.isNotEmpty()) {
                        DownloadUtils.copyMultipleToClipboard(context, selectedLinks.joinToString("\n"))
                    }
                    onDismiss()
                },
                shape = RoundedCornerShape(GlassCorners.Button),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Text("Copy")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(GlassCorners.Card),
        tonalElevation = 6.dp
    )
}
>>>>>>> 2541a1adf58b55ec85598c2da3096e5129b30f0b

package com.pira.ccloud.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pira.ccloud.data.model.Episode
import com.pira.ccloud.data.model.Season
import com.pira.ccloud.data.model.Source
import com.pira.ccloud.ui.theme.GlassCorners
import com.pira.ccloud.ui.theme.glassSurface
import com.pira.ccloud.ui.theme.rememberCardTint
import com.pira.ccloud.ui.theme.rememberGlassTint
import com.pira.ccloud.utils.DownloadUtils

/**
 * Redesigned download section, modeled on the boxed, per-quality download
 * list layout (title header + one row per quality, each with a link count
 * and a "View Links" action) rather than the old, purely dialog-driven
 * per-episode download flow.
 */

/**
 * Titled matte panel that wraps a download section. Used for both the
 * movie quality list and (per-season) the series quality list.
 */
@Composable
fun DownloadBoxPanel(
    modifier: Modifier = Modifier,
    title: String = "Download Box",
    headerAccessory: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val tint = rememberCardTint()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .glassSurface(
                shape = RoundedCornerShape(GlassCorners.Card),
                tint = tint,
                tintAlpha = 0.55f,
                borderAlpha = 0.3f
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.CloudDownload,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            headerAccessory?.invoke()
        }
        Spacer(modifier = Modifier.height(14.dp))
        content()
    }
}

/**
 * One row inside a Download Box: quality/type badge + link count on the
 * left, a "View Links" button on the right.
 */
@Composable
fun QualityDownloadRow(
    quality: String,
    type: String,
    linkCount: Int,
    onViewLinks: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.55f))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                shape = RoundedCornerShape(14.dp)
            )
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.18f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = quality,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                if (type.isNotBlank()) {
                    Text(
                        text = type,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (linkCount > 1) "$linkCount episode links" else "1 link available",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Button(
            onClick = onViewLinks,
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(text = "View Links", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
        }
    }
}

/**
 * Download Box for a movie: one row per available quality. Tapping
 * "View Links" hands the chosen [Source] back to the caller, which reuses
 * the existing quality-selection sheet (play/download actions) for it.
 */
@Composable
fun MovieDownloadBox(
    sources: List<Source>,
    onViewSource: (Source) -> Unit,
    modifier: Modifier = Modifier
) {
    if (sources.isEmpty()) return
    DownloadBoxPanel(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            sources.forEach { source ->
                QualityDownloadRow(
                    quality = source.quality,
                    type = source.type,
                    linkCount = 1,
                    onViewLinks = { onViewSource(source) }
                )
            }
        }
    }
}

/**
 * Download Box for one season: episode sources are grouped by quality
 * across the whole season (matching the reference design's per-season,
 * per-quality rows - "WEB-DL 1080p", "WEB-DL 720p", etc., each showing how
 * many episodes have that quality). Tapping "View Links" opens a sheet
 * listing every episode's link for that quality.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeasonDownloadBox(
    season: Season,
    onPlayEpisodeSource: (Episode, Source) -> Unit,
    modifier: Modifier = Modifier,
    initiallyExpanded: Boolean = true
) {
    val groups = remember(season) {
        season.episodes
            .flatMap { episode -> episode.sources.map { source -> episode to source } }
            .groupBy { (_, source) -> source.quality }
            .toList()
            .sortedByDescending { (_, pairs) -> pairs.size }
    }
    if (groups.isEmpty()) return

    var expanded by remember(season.id) { mutableStateOf(initiallyExpanded) }
    var activeQuality by remember(season.id) { mutableStateOf<String?>(null) }

    val tint = rememberCardTint()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .glassSurface(
                shape = RoundedCornerShape(GlassCorners.Card),
                tint = tint,
                tintAlpha = 0.55f,
                borderAlpha = 0.3f
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = season.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.18f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${season.episodes.size} episodes",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        AnimatedVisibility(visible = expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                groups.forEach { (quality, pairs) ->
                    QualityDownloadRow(
                        quality = quality,
                        type = pairs.first().second.type,
                        linkCount = pairs.size,
                        onViewLinks = { activeQuality = quality }
                    )
                }
            }
        }
    }

    val activeGroup = groups.firstOrNull { (quality, _) -> quality == activeQuality }
    if (activeGroup != null) {
        QualityLinksSheet(
            seasonTitle = season.title,
            quality = activeGroup.first,
            items = activeGroup.second,
            onDismiss = { activeQuality = null },
            onPlay = { episode, source ->
                activeQuality = null
                onPlayEpisodeSource(episode, source)
            }
        )
    }
}

/**
 * Bottom sheet listing every episode's link for one quality group, with a
 * "Copy All" shortcut plus per-episode copy/play/open actions.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QualityLinksSheet(
    seasonTitle: String,
    quality: String,
    items: List<Pair<Episode, Source>>,
    onDismiss: () -> Unit,
    onPlay: (Episode, Source) -> Unit
) {
    val context = LocalContext.current
    val glassTint = rememberGlassTint()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.Transparent,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .glassSurface(
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                    tint = glassTint,
                    tintAlpha = 0.95f,
                    borderAlpha = 0.25f
                )
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp, bottom = 28.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(width = 40.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "$seasonTitle • $quality",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${items.size} episode link(s)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = {
                    val joined = items.joinToString("\n") { (_, source) -> source.url }
                    DownloadUtils.copyMultipleToClipboard(context, joined)
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Copy All")
            }
            Spacer(modifier = Modifier.height(4.dp))

            items.forEach { (episode, source) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.55f))
                        .padding(start = 12.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = episode.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Row {
                        IconButton(onClick = { DownloadUtils.copyToClipboard(context, source.url) }) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy link",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        IconButton(onClick = { onPlay(episode, source) }) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        IconButton(onClick = { DownloadUtils.openUrl(context, source.url) }) {
                            Icon(
                                imageVector = Icons.Default.OpenInBrowser,
                                contentDescription = "Open in browser",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            Spacer(modifier = Modifier.height(4.dp))
            TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("Close", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

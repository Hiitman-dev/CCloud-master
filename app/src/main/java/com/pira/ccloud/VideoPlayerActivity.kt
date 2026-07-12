package com.pira.ccloud

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.Typeface
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.TypedValue
import android.view.PixelCopy
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.FrameLayout
import android.widget.Toast
import com.pira.ccloud.utils.ViewHistoryManager
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.Forward
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.CropPortrait
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import com.pira.ccloud.ui.theme.GlassAlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.common.TrackGroup
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.CaptionStyleCompat
import androidx.media3.ui.PlayerView
import com.pira.ccloud.data.model.SubtitleSettings
import com.pira.ccloud.data.model.VideoPlayerSettings
import com.pira.ccloud.data.model.FontSettings
import com.pira.ccloud.data.model.WatchedEpisode
import com.pira.ccloud.utils.StorageUtils
import com.pira.ccloud.ui.theme.FontManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


// Extension function to set subtitle text size on PlayerView
fun PlayerView.setSubtitleTextSize(spSize: Float) {
    val displayMetrics = context.resources.displayMetrics
    val pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spSize, displayMetrics)
    subtitleView?.setFixedTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, pixels)
}

// Extension function to set subtitle colors and font
fun PlayerView.setSubtitleColors(settings: SubtitleSettings, typeface: Typeface? = null, showBackground: Boolean = false, showBorder: Boolean = true) {
    val bgColor = if (showBackground) android.graphics.Color.argb(180, 0, 0, 0) else android.graphics.Color.TRANSPARENT
    val edgeType = if (showBorder) CaptionStyleCompat.EDGE_TYPE_OUTLINE else CaptionStyleCompat.EDGE_TYPE_NONE
    val style = CaptionStyleCompat(
        settings.textColor,
        bgColor,
        settings.borderColor,
        edgeType,
        settings.borderColor,
        typeface
    )
    subtitleView?.setStyle(style)
}

class VideoPlayerActivity : ComponentActivity() {
    companion object {
        const val EXTRA_VIDEO_URL = "video_url"
        const val EXTRA_SERIES_ID = "series_id"
        const val EXTRA_SEASON_ID = "season_id"
        const val EXTRA_EPISODE_ID = "episode_id"
        const val EXTRA_TITLE = "title"
        const val EXTRA_SUBTITLE_URLS = "subtitle_urls"
        const val EXTRA_QUALITY_URLS = "quality_urls"
        const val EXTRA_QUALITY_LABELS = "quality_labels"
        const val REQUEST_WRITE_SETTINGS = 1001

        fun start(context: Context, videoUrl: String, title: String? = null) {
            val intent = Intent(context, VideoPlayerActivity::class.java).apply {
                putExtra(EXTRA_VIDEO_URL, videoUrl)
                title?.let { putExtra(EXTRA_TITLE, it) }
            }
            context.startActivity(intent)
        }

        fun startWithEpisodeInfo(context: Context, videoUrl: String, seriesId: Int, seasonId: Int, episodeId: Int) {
            val intent = Intent(context, VideoPlayerActivity::class.java).apply {
                putExtra(EXTRA_VIDEO_URL, videoUrl)
                putExtra(EXTRA_SERIES_ID, seriesId)
                putExtra(EXTRA_SEASON_ID, seasonId)
                putExtra(EXTRA_EPISODE_ID, episodeId)
            }
            context.startActivity(intent)
        }
    }

    private var exoPlayer: ExoPlayer? = null
    private var videoUrl: String? = null
    private var videoTitle: String? = null
    private var seriesId: Int? = null
    private var seasonId: Int? = null
    private var episodeId: Int? = null
    private var playerInitialized = false
    private var isActivityResumed = false
    private var hasMarkedAsWatched = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        enableFullScreenMode()
        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        videoUrl = intent.getStringExtra(EXTRA_VIDEO_URL)
        videoTitle = intent.getStringExtra(EXTRA_TITLE)
        seriesId = intent.getIntExtra(EXTRA_SERIES_ID, -1).takeIf { it != -1 }
        seasonId = intent.getIntExtra(EXTRA_SEASON_ID, -1).takeIf { it != -1 }
        episodeId = intent.getIntExtra(EXTRA_EPISODE_ID, -1).takeIf { it != -1 }

        if (videoUrl != null) {
            setContent {
                VideoPlayerScreen(
                    videoUrl = videoUrl!!,
                    videoTitle = videoTitle ?: "",
                    seriesId = seriesId,
                    seasonId = seasonId,
                    episodeId = episodeId,
                    onBack = this::finish
                ) { player ->
                    exoPlayer = player
                    playerInitialized = true
                }
            }
        } else {
            finish()
        }
    }

    override fun onKeyDown(keyCode: Int, event: android.view.KeyEvent?): Boolean {
        try {
            exoPlayer?.let { player ->
                when (keyCode) {
                    android.view.KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,
                    android.view.KeyEvent.KEYCODE_DPAD_CENTER -> {
                        player.playWhenReady = !player.playWhenReady
                        return true
                    }
                    android.view.KeyEvent.KEYCODE_MEDIA_PLAY -> {
                        player.playWhenReady = true
                        return true
                    }
                    android.view.KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                        player.playWhenReady = false
                        return true
                    }
                    android.view.KeyEvent.KEYCODE_DPAD_LEFT -> {
                        val newPosition = (player.currentPosition - 10000).coerceAtLeast(0L)
                        player.seekTo(newPosition)
                        return true
                    }
                    android.view.KeyEvent.KEYCODE_DPAD_RIGHT -> {
                        val newPosition = (player.currentPosition + 10000).coerceAtMost(player.duration)
                        player.seekTo(newPosition)
                        return true
                    }
                    android.view.KeyEvent.KEYCODE_BACK -> {
                        finish()
                        return true
                    }
                }
            }
        } catch (_: Exception) {}
        return super.onKeyDown(keyCode, event)
    }

    private fun enableFullScreenMode() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.insetsController?.let { controller ->
                    controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                    controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                )
            } else {
                @Suppress("DEPRECATION")
                window.addFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN)
            }
        } catch (_: Exception) {
            @Suppress("DEPRECATION")
            try { window.addFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN) } catch (_: Exception) {}
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try { exoPlayer?.release() } catch (_: Exception) {}
        exoPlayer = null
        playerInitialized = false
        try { window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) } catch (_: Exception) {}
    }

    override fun onResume() {
        super.onResume()
        isActivityResumed = true
        try { enableFullScreenMode() } catch (_: Exception) {}
    }

    override fun onPause() {
        super.onPause()
        isActivityResumed = false
        try { exoPlayer?.playWhenReady = false } catch (_: Exception) {}
    }
}

// ──────────────────────────────────────────────────────────────
// Aspect mode enum (must be outside composable for K2 compiler)
// ──────────────────────────────────────────────────────────────
enum class AspectMode { FIT, FILL, CROP, WIDE_16_9 }

// ──────────────────────────────────────────────────────────────
// Main composable
// ──────────────────────────────────────────────────────────────
@Composable
fun VideoPlayerScreen(
    videoUrl: String,
    videoTitle: String,
    seriesId: Int?,
    seasonId: Int?,
    episodeId: Int?,
    onBack: () -> Unit,
    onPlayerReady: (ExoPlayer) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // ── Player state ──
    var isPlaying by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableLongStateOf(0L) }
    var duration by remember { mutableLongStateOf(0L) }
    var isBuffering by remember { mutableStateOf(false) }
    var playerError by remember { mutableStateOf<String?>(null) }
    var isRetrying by remember { mutableStateOf(false) }

    // ── UI state ──
    var showControls by remember { mutableStateOf(true) }
    var controlsAlpha by remember { mutableFloatStateOf(1f) }
    var isLocked by remember { mutableStateOf(false) }
    var showSpeedDropdown by remember { mutableStateOf(false) }
    var showTrackSelectionDialog by remember { mutableStateOf(false) }
    var showSubtitleToggle by remember { mutableStateOf(false) }
    var playbackSpeed by remember { mutableFloatStateOf(1.0f) }
    var hasMarkedAsWatched by remember { mutableStateOf(false) }

    // ── Aspect ratio ──
    var aspectMode by remember { mutableStateOf(AspectMode.FIT) }
    val aspectModeIndex by remember(aspectMode) { mutableStateOf(aspectMode.ordinal) }

    // ── Track selection ──
    var currentTracks by remember { mutableStateOf(Tracks.EMPTY) }
    var trackSelector by remember { mutableStateOf<DefaultTrackSelector?>(null) }
    var subtitlesEnabled by remember { mutableStateOf(true) }
    var showSubtitlePanel by remember { mutableStateOf(false) }
    var showAudioDropdown by remember { mutableStateOf(false) }
    var showBackground by remember { mutableStateOf(false) }
    var showBorder by remember { mutableStateOf(true) }

    // ── Gesture state ──
    var seekGestureActive by remember { mutableStateOf(false) }
    var seekGestureDelta by remember { mutableFloatStateOf(0f) }
    var brightnessGestureActive by remember { mutableStateOf(false) }
    var brightnessGestureDelta by remember { mutableFloatStateOf(0f) }
    var volumeGestureActive by remember { mutableStateOf(false) }
    var volumeGestureDelta by remember { mutableFloatStateOf(0f) }
    var gestureInitialPosition by remember { mutableLongStateOf(0L) }
    var gestureInitialBrightness by remember { mutableFloatStateOf(0f) }
    var gestureInitialVolume by remember { mutableFloatStateOf(0f) }

    // ── Double-tap state ──
    var doubleTapSide by remember { mutableStateOf(0) } // 0=none, -1=left, 1=right
    var doubleTapAlpha by remember { mutableFloatStateOf(0f) }

    // ── Screenshot state ──
    var showScreenshotFlash by remember { mutableStateOf(false) }

    // ── Speed options ──
    val speedOptions = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 1.75f, 2.0f, 2.5f, 3.0f)

    // ── Aspect mode labels ──
    val aspectModeLabels = listOf("FIT", "FILL", "CROP", "16:9")

    // ── Audio manager for volume ──
    val audioManager = remember(context) {
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }
    val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

    // ── Settings ──
    val videoPlayerSettings = remember(context) {
        try { StorageUtils.loadVideoPlayerSettings(context) } catch (_: Exception) { VideoPlayerSettings.DEFAULT }
    }
    var subtitleSettings by remember(context) {
        mutableStateOf(
            try { StorageUtils.loadSubtitleSettings(context) } catch (_: Exception) { SubtitleSettings.getDefaultSettings(context) }
        )
    }
    val fontSettings = remember(context) {
        try { StorageUtils.loadFontSettings(context) } catch (_: Exception) { FontSettings.DEFAULT }
    }
    val customTypeface = remember(fontSettings.fontType) {
        try {
            when (fontSettings.fontType) {
                com.pira.ccloud.data.model.FontType.DEFAULT -> null
                com.pira.ccloud.data.model.FontType.VAZIRMATN -> {
                    try { Typeface.createFromAsset(context.assets, "font/vazirmatn_regular.ttf") } catch (_: Exception) { null }
                }
                com.pira.ccloud.data.model.FontType.YEKAN_BAKH -> {
                    try { androidx.core.content.res.ResourcesCompat.getFont(context, com.pira.ccloud.R.font.yekan_bakh_regular) } catch (_: Exception) { null }
                }
            }
        } catch (_: Exception) { null }
    }

    // ── System brightness ──
    val initialBrightness = remember {
        try {
            Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS) / 255f
        } catch (_: Exception) { 0.5f }
    }

    // ── Resume position ──
    val savedPosition: Long = remember {
        val id = seriesId ?: 0
        val type = if (seriesId != null) "series" else "movie"
        val epId = episodeId ?: -1
        ViewHistoryManager.getLastPosition(context, id, type, epId)
    }
    var showResumeDialog by remember { mutableStateOf(savedPosition > 10_000L) }
    var hasResumed by remember { mutableStateOf(false) }

    // ── ExoPlayer setup ──
    val exoPlayer = remember(context) {
        try {
            val selector = DefaultTrackSelector(context).apply {
                setParameters(buildUponParameters().setMaxVideoSizeSd())
            }
            trackSelector = selector

            ExoPlayer.Builder(context)
                .setTrackSelector(selector)
                .build().apply {
                    try {
                        setMediaItem(MediaItem.fromUri(Uri.parse(videoUrl)))
                        prepare()
                        playWhenReady = false
                        setPlaybackSpeed(playbackSpeed)
                    } catch (_: Exception) {}
                }
        } catch (_: Exception) { null }
    }

    // ── Track listener ──
    val trackListener = remember(exoPlayer) {
        object : Player.Listener {
            override fun onTracksChanged(tracks: Tracks) { currentTracks = tracks }
        }
    }
    LaunchedEffect(exoPlayer) { exoPlayer?.addListener(trackListener) }
    DisposableEffect(exoPlayer) { onDispose { exoPlayer?.removeListener(trackListener) } }

    // ── Player state listener ──
    val playerListener = remember(exoPlayer) {
        object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_READY -> {
                        duration = exoPlayer?.duration ?: 0L
                        isBuffering = false
                        isRetrying = false
                        playerError = null
                        if (!isRetrying) exoPlayer?.playWhenReady = isPlaying
                    }
                    Player.STATE_BUFFERING -> isBuffering = true
                    Player.STATE_ENDED -> isPlaying = false
                    Player.STATE_IDLE -> {}
                }
            }
            override fun onPositionDiscontinuity(oldPosition: Player.PositionInfo, newPosition: Player.PositionInfo, reason: Int) {
                currentPosition = exoPlayer?.currentPosition ?: 0L
            }
            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                isRetrying = true
                playerError = error.message
                val retryPos = currentPosition
                val wasPlaying = isPlaying
                CoroutineScope(Dispatchers.Main).launch {
                    delay(3000)
                    try {
                        exoPlayer?.let { player ->
                            player.setMediaItem(MediaItem.fromUri(Uri.parse(videoUrl)))
                            player.prepare()
                            player.seekTo(retryPos)
                            player.playWhenReady = wasPlaying
                            isPlaying = wasPlaying
                            isRetrying = false
                            playerError = null
                        }
                    } catch (_: Exception) {}
                }
            }
        }
    }
    LaunchedEffect(exoPlayer) { exoPlayer?.addListener(playerListener) }
    DisposableEffect(exoPlayer) { onDispose { exoPlayer?.removeListener(playerListener) } }

    // ── Resume dialog ──
    if (showResumeDialog && !hasResumed) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showResumeDialog = false },
            title = { Text("Resume Playback") },
            text = {
                Text("You left off at ${formatTime(savedPosition)}. Resume from where you stopped?")
            },
            confirmButton = {
                androidx.compose.material3.Button(
                    onClick = {
                        exoPlayer?.seekTo(savedPosition)
                        currentPosition = savedPosition
                        isPlaying = true
                        hasResumed = true
                        showResumeDialog = false
                    }
                ) {
                    Text("Resume")
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        isPlaying = true
                        hasResumed = true
                        showResumeDialog = false
                    }
                ) {
                    Text("Play from Beginning")
                }
            }
        )
    }

    // ── Mark as watching when playback starts ──
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            val id = seriesId ?: 0
            val type = if (seriesId != null) "series" else "movie"
            val epId = episodeId ?: -1
            ViewHistoryManager.markAsWatching(context, id, type, epId)
        }
    }

    // ── Periodic position save ──
    LaunchedEffect(isPlaying, currentPosition) {
        if (isPlaying && currentPosition > 0) {
            kotlinx.coroutines.delay(10_000) // Save every 10 seconds
            val id = seriesId ?: 0
            val type = if (seriesId != null) "series" else "movie"
            val epId = episodeId ?: -1
            ViewHistoryManager.updateProgress(context, id, type, currentPosition, duration, currentPosition, epId)
        }
    }

    // ── Notify activity ──
    LaunchedEffect(Unit) {
        try { exoPlayer?.let { onPlayerReady(it) } } catch (_: Exception) {}
    }

    // ── Play state sync ──
    LaunchedEffect(isPlaying, exoPlayer) {
        try {
            exoPlayer?.playWhenReady = isPlaying
            if (isPlaying && !hasMarkedAsWatched && seriesId != null && seasonId != null && episodeId != null) {
                try {
                    StorageUtils.saveWatchedEpisode(
                        context,
                        WatchedEpisode(seriesId = seriesId, seasonId = seasonId, episodeId = episodeId)
                    )
                    hasMarkedAsWatched = true
                } catch (_: Exception) {}
            }
        } catch (_: Exception) {}
    }

    // ── Speed sync ──
    LaunchedEffect(playbackSpeed, exoPlayer) {
        try { exoPlayer?.setPlaybackSpeed(playbackSpeed) } catch (_: Exception) {}
    }

    // ── Position polling ──
    LaunchedEffect(exoPlayer, isPlaying) {
        while (true) {
            delay(200)
            try {
                exoPlayer?.let { player ->
                    if (player.isPlaying) {
                        currentPosition = player.currentPosition
                        duration = player.duration
                    }
                }
            } catch (_: Exception) {}
        }
    }

    // ── Auto-hide controls ──
    LaunchedEffect(showControls, isPlaying) {
        if (showControls && isPlaying) {
            delay(3000)
            showControls = false
        }
    }

    // ── Double-tap fade out ──
    LaunchedEffect(doubleTapSide) {
        if (doubleTapSide != 0) {
            doubleTapAlpha = 1f
            delay(600)
            doubleTapAlpha = 0f
            delay(200)
            doubleTapSide = 0
        }
    }

    // ── Screenshot flash ──
    LaunchedEffect(showScreenshotFlash) {
        if (showScreenshotFlash) {
            delay(200)
            showScreenshotFlash = false
        }
    }

    // ── Cleanup ──
    DisposableEffect(exoPlayer) {
        onDispose {
            try { exoPlayer?.release() } catch (_: Exception) {}
        }
    }

    // ── Controls alpha animation ──
    val animatedControlsAlpha by animateFloatAsState(
        targetValue = if (showControls) 1f else 0f,
        animationSpec = tween(durationMillis = 250),
        label = "controlsAlpha"
    )

    // ── Aspect ratio for PlayerView ──
    val resizeMode = when (aspectMode) {
        AspectMode.FIT -> AspectRatioFrameLayout.RESIZE_MODE_FIT
        AspectMode.FILL -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
        AspectMode.CROP -> AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
        AspectMode.WIDE_16_9 -> AspectRatioFrameLayout.RESIZE_MODE_FIT
    }

    // ═══════════════════════════════════════════════════════════
    // UI
    // ═══════════════════════════════════════════════════════════
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { offset ->
                        if (isLocked) return@detectTapGestures
                        // Keep a dead zone around the center (40%-60% of width) so double taps
                        // near the middle of the screen (close to the play/pause controls) don't
                        // accidentally trigger a seek. Only taps clearly on the left or right
                        // portions of the screen will rewind/forward.
                        val leftZoneEnd = size.width * 0.4f
                        val rightZoneStart = size.width * 0.6f
                        val seekMs = 10_000L
                        exoPlayer?.let { player ->
                            when {
                                offset.x < leftZoneEnd -> {
                                    // Rewind 10s
                                    val newPos = (player.currentPosition - seekMs).coerceAtLeast(0L)
                                    player.seekTo(newPos)
                                    currentPosition = newPos
                                    doubleTapSide = -1
                                }
                                offset.x > rightZoneStart -> {
                                    // Forward 10s
                                    val newPos = (player.currentPosition + seekMs).coerceAtMost(player.duration)
                                    player.seekTo(newPos)
                                    currentPosition = newPos
                                    doubleTapSide = 1
                                }
                                else -> {
                                    // Center dead zone: double tap here just toggles play/pause
                                    // instead of seeking, since it's too close to the center
                                    // to be an intentional seek gesture.
                                    if (player.isPlaying) player.pause() else player.play()
                                }
                            }
                        }
                    },
                    onTap = {
                        if (!isLocked) {
                            showControls = !showControls
                        }
                    }
                )
            }
            .pointerInput(isLocked) {
                if (isLocked) return@pointerInput
                detectDragGestures(
                    onDragStart = { offset ->
                        val halfWidth = size.width / 3
                        gestureInitialPosition = exoPlayer?.currentPosition ?: 0L
                        gestureInitialBrightness = try {
                            Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS) / 255f
                        } catch (_: Exception) { 0.5f }
                        gestureInitialVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat()
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val halfWidth = size.width / 3
                        val startX = change.position.x - dragAmount.x

                        when {
                            // Left third: brightness
                            startX < halfWidth -> {
                                if (!brightnessGestureActive) {
                                    brightnessGestureActive = true
                                    seekGestureActive = false
                                    volumeGestureActive = false
                                }
                                brightnessGestureDelta += -dragAmount.y / size.height
                                val newBrightness = (gestureInitialBrightness + brightnessGestureDelta).coerceIn(0.01f, 1f)
                                try {
                                    Settings.System.putInt(
                                        context.contentResolver,
                                        Settings.System.SCREEN_BRIGHTNESS,
                                        (newBrightness * 255).roundToInt()
                                    )
                                } catch (_: Exception) {}
                            }
                            // Right third: volume
                            startX > size.width - halfWidth -> {
                                if (!volumeGestureActive) {
                                    volumeGestureActive = true
                                    seekGestureActive = false
                                    brightnessGestureActive = false
                                }
                                volumeGestureDelta += -dragAmount.y / size.height
                                val volumeDelta = (volumeGestureDelta * maxVolume).roundToInt()
                                val currentVol = gestureInitialVolume.toInt()
                                val newVol = (currentVol + volumeDelta).coerceIn(0, maxVolume)
                                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVol, 0)
                            }
                            // Middle third: seek
                            else -> {
                                if (!seekGestureActive) {
                                    seekGestureActive = true
                                    brightnessGestureActive = false
                                    volumeGestureActive = false
                                }
                                seekGestureDelta = dragAmount.x / size.width
                                val seekDeltaMs = (seekGestureDelta * 120_000).toLong() // 120s max swipe
                                val newPos = (gestureInitialPosition + seekDeltaMs).coerceIn(0L, duration)
                                currentPosition = newPos
                            }
                        }
                    },
                    onDragEnd = {
                        if (seekGestureActive) {
                            exoPlayer?.seekTo(currentPosition)
                        }
                        seekGestureActive = false
                        brightnessGestureActive = false
                        volumeGestureActive = false
                        seekGestureDelta = 0f
                        brightnessGestureDelta = 0f
                        volumeGestureDelta = 0f
                    }
                )
            }
    ) {
        // ── PlayerView ──
        if (exoPlayer == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Initializing player...", color = Color.White)
            }
            return@Box
        }

        AndroidView(
            factory = { ctx ->
                try {
                    val pv = PlayerView(ctx)
                    pv.player = exoPlayer
                    pv.useController = false
                    pv.layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    pv.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                    pv.setSubtitleTextSize(subtitleSettings.textSize)
                    pv.setSubtitleColors(subtitleSettings, customTypeface, showBackground, showBorder)
                    pv
                } catch (_: Exception) {
                    View(ctx).apply { setBackgroundColor(android.graphics.Color.BLACK) }
                }
            },
            modifier = Modifier.fillMaxSize(),
            update = { pv ->
                if (pv is PlayerView) {
                    pv.player = exoPlayer
                    pv.resizeMode = resizeMode
                    pv.setSubtitleTextSize(subtitleSettings.textSize)
                    pv.setSubtitleColors(subtitleSettings, customTypeface, showBackground, showBorder)
                }
            }
        )

        // ── Gesture indicator overlays ──

        // Seek gesture indicator
        if (seekGestureActive) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.75f))
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val deltaMs = seekGestureDelta * 120_000
                        val sign = if (deltaMs >= 0) "+" else ""
                        Icon(
                            imageVector = if (deltaMs >= 0) Icons.Default.Forward else Icons.Default.Replay,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = "${sign}${formatTime(deltaMs.toLong())}",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Text(
                            text = "${formatTime(currentPosition)} / ${formatTime(duration)}",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // Brightness gesture indicator
        if (brightnessGestureActive) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.CenterStart
            ) {
                Box(
                    modifier = Modifier
                        .padding(start = 48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.75f))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.BrightnessMedium,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                        val brightness = (gestureInitialBrightness + brightnessGestureDelta).coerceIn(0.01f, 1f)
                        LinearProgressIndicator(
                            progress = { brightness },
                            modifier = Modifier
                                .width(120.dp)
                                .padding(top = 8.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = Color(0xFFFFD700),
                            trackColor = Color.White.copy(alpha = 0.3f)
                        )
                        Text(
                            text = "${(brightness * 100).toInt()}%",
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }

        // Volume gesture indicator
        if (volumeGestureActive) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Box(
                    modifier = Modifier
                        .padding(end = 48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.75f))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                        val volDelta = (volumeGestureDelta * maxVolume).roundToInt()
                        val currentVol = (gestureInitialVolume.toInt() + volDelta).coerceIn(0, maxVolume)
                        LinearProgressIndicator(
                            progress = { currentVol.toFloat() / maxVolume },
                            modifier = Modifier
                                .width(120.dp)
                                .padding(top = 8.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = Color.White,
                            trackColor = Color.White.copy(alpha = 0.3f)
                        )
                        Text(
                            text = "${(currentVol.toFloat() / maxVolume * 100).toInt()}%",
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }

        // ── Double-tap seek indicators ──
        if (doubleTapSide != 0) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = if (doubleTapSide < 0) Alignment.CenterStart else Alignment.CenterEnd
            ) {
                Box(
                    modifier = Modifier
                        .alpha(doubleTapAlpha)
                        .padding(horizontal = 48.dp)
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = if (doubleTapSide < 0) Icons.Default.Replay else Icons.Default.Forward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = if (doubleTapSide < 0) "-10s" else "+10s",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // ── Center play/pause (shown when paused) ──
        AnimatedVisibility(
            visible = !isPlaying && showControls,
            enter = fadeIn(tween(200)),
            exit = fadeOut(tween(200)),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = { isPlaying = true },
                    modifier = Modifier
                        .size(64.dp)
                        .alpha(0.7f)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }

        // ── Buffering indicator ──
        if (isBuffering && !isRetrying) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .width(120.dp)
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.3f)
                )
            }
        }

        // ── Top control bar ──
        AnimatedVisibility(
            visible = showControls && !isLocked,
            enter = fadeIn(tween(250)),
            exit = fadeOut(tween(250)),
            modifier = Modifier.fillMaxSize()
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Back button — save position before exiting
                        IconButton(onClick = {
                            val pos = currentPosition
                            val dur = duration
                            val id = seriesId ?: 0
                            val type = if (seriesId != null) "series" else "movie"
                            val epId = episodeId ?: -1
                            ViewHistoryManager.updateProgress(context, id, type, pos, dur, pos, epId)
                            onBack()
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }

                        // Title
                        Text(
                            text = videoTitle.ifEmpty { "Playing" },
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp)
                        )

                        // Subtitle customization panel toggle
                        IconButton(onClick = {
                            showSubtitlePanel = !showSubtitlePanel
                            showAudioDropdown = false
                        }) {
                            Icon(
                                imageVector = Icons.Default.Subtitles,
                                contentDescription = "Subtitles",
                                tint = if (subtitlesEnabled) Color.White else Color.White.copy(alpha = 0.4f)
                            )
                        }

                        // Quality / track selection
                        IconButton(onClick = { showTrackSelectionDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = Color.White
                            )
                        }

                        // Speed selector
                        Box {
                            IconButton(onClick = { showSpeedDropdown = true }) {
                                Text(
                                    text = "${playbackSpeed}x",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            DropdownMenu(
                                expanded = showSpeedDropdown,
                                onDismissRequest = { showSpeedDropdown = false },
                                modifier = Modifier.background(Color(0xFF1A1A1A))
                            ) {
                                speedOptions.forEach { speed ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = "${speed}x",
                                                color = if (speed == playbackSpeed) Color(0xFF00BFFF) else Color.White,
                                                fontWeight = if (speed == playbackSpeed) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        onClick = {
                                            playbackSpeed = speed
                                            showSpeedDropdown = false
                                        }
                                    )
                                }
                            }
                        }

                        // Audio track selector
                        Box {
                            IconButton(onClick = {
                                showAudioDropdown = !showAudioDropdown
                                showSubtitlePanel = false
                            }) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Audio Track",
                                    tint = Color.White
                                )
                            }
                            DropdownMenu(
                                expanded = showAudioDropdown,
                                onDismissRequest = { showAudioDropdown = false },
                                modifier = Modifier.background(Color(0xFF1A1A1A))
                            ) {
                                val audioTrackGroups = currentTracks.groups.filter { it.type == C.TRACK_TYPE_AUDIO }
                                if (audioTrackGroups.isEmpty()) {
                                    DropdownMenuItem(
                                        text = { Text("No audio tracks", color = Color.White.copy(alpha = 0.5f)) },
                                        onClick = { showAudioDropdown = false }
                                    )
                                } else {
                                    audioTrackGroups.forEachIndexed { groupIndex, trackGroup ->
                                        for (i in 0 until trackGroup.length) {
                                            val format = trackGroup.getTrackFormat(i)
                                            val trackName = format.language?.let {
                                                if (it.isNotEmpty()) it else format.label ?: "Track ${groupIndex + 1}.${i + 1}"
                                            } ?: format.label ?: "Track ${groupIndex + 1}.${i + 1}"
                                            val isSelected = trackGroup.isTrackSelected(i)
                                            DropdownMenuItem(
                                                text = {
                                                    Text(
                                                        text = trackName,
                                                        color = if (isSelected) Color(0xFF00BFFF) else Color.White,
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                                    )
                                                },
                                                onClick = {
                                                    val ts = trackSelector
                                                    ts?.setParameters(
                                                        ts.buildUponParameters()
                                                            .setTrackTypeDisabled(C.TRACK_TYPE_AUDIO, false)
                                                            .setOverrideForType(
                                                                TrackSelectionOverride(trackGroup.mediaTrackGroup, i)
                                                            )
                                                    )
                                                    showAudioDropdown = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // ── Bottom control bar ──
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.7f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    // Progress bar
                    if (isRetrying) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .height(3.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = Color.White
                        )
                    } else {
                        Slider(
                            value = if (duration > 0) currentPosition.toFloat() / duration.toFloat() else 0f,
                            onValueChange = { progress ->
                                val newPos = (progress * duration).toLong()
                                currentPosition = newPos
                                exoPlayer?.seekTo(newPos)
                            },
                            onValueChangeFinished = {
                                exoPlayer?.seekTo(currentPosition)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(24.dp),
                            colors = SliderDefaults.colors(
                                thumbColor = Color.White,
                                activeTrackColor = Color(0xFF00BFFF),
                                inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                            )
                        )
                    }

                    // Time + controls row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatTime(currentPosition),
                            color = Color.White,
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "/", color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = formatTime(duration),
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        // Skip back 10s
                        IconButton(onClick = {
                            val newPos = ((exoPlayer?.currentPosition ?: 0L) - 10_000L).coerceAtLeast(0L)
                            exoPlayer?.seekTo(newPos)
                            currentPosition = newPos
                        }) {
                            Icon(
                                imageVector = Icons.Default.Replay,
                                contentDescription = "Rewind 10s",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        // Play / Pause
                        IconButton(onClick = { isPlaying = !isPlaying }) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        // Skip forward 10s
                        IconButton(onClick = {
                            val newPos = ((exoPlayer?.currentPosition ?: 0L) + 10_000L).coerceAtMost(duration)
                            exoPlayer?.seekTo(newPos)
                            currentPosition = newPos
                        }) {
                            Icon(
                                imageVector = Icons.Default.Forward,
                                contentDescription = "Forward 10s",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        // Aspect ratio toggle
                        IconButton(onClick = {
                            aspectMode = when (aspectMode) {
                                AspectMode.FIT -> AspectMode.FILL
                                AspectMode.FILL -> AspectMode.CROP
                                AspectMode.CROP -> AspectMode.WIDE_16_9
                                AspectMode.WIDE_16_9 -> AspectMode.FIT
                            }
                        }) {
                            Text(
                                text = aspectModeLabels[aspectModeIndex],
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Lock rotation
                        IconButton(onClick = { isLocked = !isLocked }) {
                            Icon(
                                imageVector = if (isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                                contentDescription = if (isLocked) "Unlock" else "Lock",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        // Screenshot
                        IconButton(onClick = {
                            showScreenshotFlash = true
                            val activity = context as? Activity ?: return@IconButton
                            val rootView = activity.window.decorView.findViewById<View>(android.R.id.content)
                            val bitmap = Bitmap.createBitmap(rootView.width, rootView.height, Bitmap.Config.ARGB_8888)
                            try {
                                PixelCopy.request(activity.window, bitmap, { result ->
                                    if (result == PixelCopy.SUCCESS) {
                                        scope.launch(Dispatchers.IO) {
                                            try {
                                                val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                                                val screenshotsDir = File(picturesDir, "Screenshots")
                                                if (!screenshotsDir.exists()) screenshotsDir.mkdirs()
                                                val file = File(screenshotsDir, "screenshot_${System.currentTimeMillis()}.png")
                                                FileOutputStream(file).use { fos ->
                                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                                                }
                                                // Also add to MediaStore
                                                val contentValues = ContentValues().apply {
                                                    put(MediaStore.Images.Media.DISPLAY_NAME, file.name)
                                                    put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                                                    put(MediaStore.Images.Media.DATA, file.absolutePath)
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                                        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Screenshots")
                                                    }
                                                }
                                                context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                                                withContext(Dispatchers.Main) {
                                                    Toast.makeText(context, "Screenshot saved", Toast.LENGTH_SHORT).show()
                                                }
                                            } catch (_: Exception) {
                                                withContext(Dispatchers.Main) {
                                                    Toast.makeText(context, "Screenshot failed", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }
                                    }
                                }, Handler(Looper.getMainLooper()))
                            } catch (_: Exception) {
                                Toast.makeText(context, "Screenshot failed", Toast.LENGTH_SHORT).show()
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = "Screenshot",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        }

        // ── Lock indicator (centered, fades in when locked) ──
        val lockAlpha by animateFloatAsState(
            targetValue = if (isLocked && showControls) 1f else 0f,
            animationSpec = tween(durationMillis = 300),
            label = "lockAlpha"
        )
        if (isLocked && showControls) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .alpha(lockAlpha)
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        // ── Locked overlay hint ──
        if (isLocked && !showControls) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                // Tap to show unlock button
                                showControls = true
                            }
                        )
                    }
            )
        }

        // ── Screenshot flash ──
        if (showScreenshotFlash) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = 0.4f))
            )
        }

        // ── Subtitle customization panel (slides in from right) ──
        if (showSubtitlePanel) {
            // Dismiss backdrop
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures { showSubtitlePanel = false }
                    }
            )
            SubtitleCustomizationPanel(
                subtitlesEnabled = subtitlesEnabled,
                onSubtitleToggle = { enabled ->
                    subtitlesEnabled = enabled
                    val ts = trackSelector
                    ts?.setParameters(
                        ts.buildUponParameters()
                            .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, !enabled)
                    )
                },
                textSize = subtitleSettings.textSize,
                onTextSizeChange = { newSize ->
                    subtitleSettings = subtitleSettings.copy(textSize = newSize)
                    try { StorageUtils.saveSubtitleSettings(context, subtitleSettings) } catch (_: Exception) {}
                },
                textColor = subtitleSettings.textColor,
                onTextColorChange = { newColor ->
                    subtitleSettings = subtitleSettings.copy(textColor = newColor)
                    try { StorageUtils.saveSubtitleSettings(context, subtitleSettings) } catch (_: Exception) {}
                },
                showBackground = showBackground,
                onBackgroundToggle = { showBackground = it },
                showBorder = showBorder,
                onBorderToggle = { showBorder = it },
                onDismiss = { showSubtitlePanel = false }
            )
        }

        // ── Track selection dialog ──
        if (showTrackSelectionDialog) {
            TrackSelectionDialog(
                tracks = currentTracks,
                trackSelector = trackSelector,
                onDismiss = { showTrackSelectionDialog = false }
            )
        }
    }
}

// ──────────────────────────────────────────────────────────────
// Track selection dialog
// ──────────────────────────────────────────────────────────────
@Composable
fun TrackSelectionDialog(
    tracks: Tracks,
    trackSelector: DefaultTrackSelector?,
    onDismiss: () -> Unit
) {
    val audioTrackGroups = remember(tracks) {
        tracks.groups.filter { it.type == C.TRACK_TYPE_AUDIO }
    }
    val textTrackGroups = remember(tracks) {
        tracks.groups.filter { it.type == C.TRACK_TYPE_TEXT }
    }

    var showAudioDropdown by remember { mutableStateOf(false) }
    var showSubtitleDropdown by remember { mutableStateOf(false) }

    val currentAudioSelection = remember(audioTrackGroups) {
        audioTrackGroups.firstOrNull { it.isSelected }?.let { group ->
            (0 until group.length).firstOrNull { group.isTrackSelected(it) }?.let { index ->
                val format = group.getTrackFormat(index)
                format.language?.let { if (it.isNotEmpty()) it else format.label ?: "Track $index" }
                    ?: format.label ?: "Track $index"
            }
        } ?: "None"
    }

    val currentSubtitleSelection = remember(textTrackGroups) {
        textTrackGroups.firstOrNull { it.isSelected }?.let { group ->
            (0 until group.length).firstOrNull { group.isTrackSelected(it) }?.let { index ->
                val format = group.getTrackFormat(index)
                format.language?.let { if (it.isNotEmpty()) it else format.label ?: "Subtitle $index" }
                    ?: format.label ?: "Subtitle $index"
            }
        } ?: "None"
    }

    GlassAlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Track Selection",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                if (audioTrackGroups.isNotEmpty()) {
                    Text(
                        text = "Audio Tracks",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showAudioDropdown = true }
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = currentAudioSelection,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Expand audio tracks",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        DropdownMenu(
                            expanded = showAudioDropdown,
                            onDismissRequest = { showAudioDropdown = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "None",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                },
                                onClick = {
                                    trackSelector?.setParameters(
                                        trackSelector.buildUponParameters()
                                            .setTrackTypeDisabled(C.TRACK_TYPE_AUDIO, true)
                                    )
                                    showAudioDropdown = false
                                }
                            )

                            audioTrackGroups.forEachIndexed { groupIndex, trackGroup ->
                                for (i in 0 until trackGroup.length) {
                                    val format = trackGroup.getTrackFormat(i)
                                    val trackName = format.language?.let {
                                        if (it.isNotEmpty()) it else format.label ?: "Track ${groupIndex + 1}.${i + 1}"
                                    } ?: format.label ?: "Track ${groupIndex + 1}.${i + 1}"

                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = trackName,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        },
                                        onClick = {
                                            trackSelector?.setParameters(
                                                trackSelector.buildUponParameters()
                                                    .setTrackTypeDisabled(C.TRACK_TYPE_AUDIO, false)
                                                    .setOverrideForType(
                                                        TrackSelectionOverride(trackGroup.mediaTrackGroup, i)
                                                    )
                                            )
                                            showAudioDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (textTrackGroups.isNotEmpty()) {
                    Text(
                        text = "Subtitles",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showSubtitleDropdown = true }
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = currentSubtitleSelection,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Expand subtitle tracks",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        DropdownMenu(
                            expanded = showSubtitleDropdown,
                            onDismissRequest = { showSubtitleDropdown = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "None",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                },
                                onClick = {
                                    trackSelector?.setParameters(
                                        trackSelector.buildUponParameters()
                                            .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, true)
                                    )
                                    showSubtitleDropdown = false
                                }
                            )

                            textTrackGroups.forEachIndexed { groupIndex, trackGroup ->
                                for (i in 0 until trackGroup.length) {
                                    val format = trackGroup.getTrackFormat(i)
                                    val trackName = format.language?.let {
                                        if (it.isNotEmpty()) it else format.label ?: "Subtitle ${groupIndex + 1}.${i + 1}"
                                    } ?: format.label ?: "Subtitle ${groupIndex + 1}.${i + 1}"

                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = trackName,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        },
                                        onClick = {
                                            trackSelector?.setParameters(
                                                trackSelector.buildUponParameters()
                                                    .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, false)
                                                    .setOverrideForType(
                                                        TrackSelectionOverride(trackGroup.mediaTrackGroup, i)
                                                    )
                                            )
                                            showSubtitleDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Text(
                        text = "No subtitles available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = MaterialTheme.colorScheme.primary)
            }
        }
    )
}

// ──────────────────────────────────────────────────────────────
// Subtitle customization panel (slide-in from right)
// ──────────────────────────────────────────────────────────────
@Composable
private fun SubtitleCustomizationPanel(
    subtitlesEnabled: Boolean,
    onSubtitleToggle: (Boolean) -> Unit,
    textSize: Float,
    onTextSizeChange: (Float) -> Unit,
    textColor: Int,
    onTextColorChange: (Int) -> Unit,
    showBackground: Boolean,
    onBackgroundToggle: (Boolean) -> Unit,
    showBorder: Boolean,
    onBorderToggle: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.CenterEnd
    ) {
        AnimatedVisibility(
            visible = true,
            enter = slideInHorizontally(initialOffsetX = { it }),
            exit = slideOutHorizontally(targetOffsetX = { it })
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(240.dp)
                    .background(Color.Black.copy(alpha = 0.85f))
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header
                    Text(
                        text = "Subtitle Settings",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // ON/OFF Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Subtitles",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        Switch(
                            checked = subtitlesEnabled,
                            onCheckedChange = onSubtitleToggle,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF00BFFF),
                                checkedTrackColor = Color(0xFF00BFFF).copy(alpha = 0.3f),
                                uncheckedThumbColor = Color.White.copy(alpha = 0.5f),
                                uncheckedTrackColor = Color.White.copy(alpha = 0.2f)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Text size slider
                    Text(
                        text = "Text Size: ${textSize.toInt()}sp",
                        color = Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Slider(
                        value = textSize,
                        onValueChange = onTextSizeChange,
                        valueRange = 12f..32f,
                        steps = 19,
                        modifier = Modifier.fillMaxWidth(),
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = Color(0xFF00BFFF),
                            inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Text color picker
                    Text(
                        text = "Text Color",
                        color = Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val colorPresets = listOf(
                            android.graphics.Color.WHITE to "White",
                            android.graphics.Color.YELLOW to "Yellow",
                            android.graphics.Color.CYAN to "Cyan",
                            android.graphics.Color.GREEN to "Green"
                        )
                        colorPresets.forEach { (colorInt, label) ->
                            val isSelected = textColor == colorInt
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(colorInt))
                                    .then(
                                        if (isSelected) Modifier.padding(2.dp)
                                        else Modifier
                                    )
                                    .clip(CircleShape)
                                    .then(
                                        if (isSelected) Modifier.background(Color.Black.copy(alpha = 0.3f))
                                        else Modifier
                                    )
                                    .clickable { onTextColorChange(colorInt) },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(Color(colorInt))
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Background toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Dark Background",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        Switch(
                            checked = showBackground,
                            onCheckedChange = onBackgroundToggle,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF00BFFF),
                                checkedTrackColor = Color(0xFF00BFFF).copy(alpha = 0.3f),
                                uncheckedThumbColor = Color.White.copy(alpha = 0.5f),
                                uncheckedTrackColor = Color.White.copy(alpha = 0.2f)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Border toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Border / Outline",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        Switch(
                            checked = showBorder,
                            onCheckedChange = onBorderToggle,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF00BFFF),
                                checkedTrackColor = Color(0xFF00BFFF).copy(alpha = 0.3f),
                                uncheckedThumbColor = Color.White.copy(alpha = 0.5f),
                                uncheckedTrackColor = Color.White.copy(alpha = 0.2f)
                            )
                        )
                    }
                }
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────
// Utility
// ──────────────────────────────────────────────────────────────
fun formatTime(milliseconds: Long): String {
    val totalSeconds = (milliseconds / 1000).toInt()
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}

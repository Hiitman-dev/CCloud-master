package com.pira.ccloud.player

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.media3.common.Tracks
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector

/**
 * State holder for the video player composable.
 * Groups related state variables for better organization and testability.
 *
 * Note: This class is prepared for future migration. The current VideoPlayerScreen
 * composable uses individual remember states. When ready to migrate, this state
 * holder can be used to centralize all player state.
 */
class VideoPlayerState {
    // ── Player state ──────────────────────────────────────
    var isPlaying by mutableStateOf(false)
        private set
    var currentPosition by mutableLongStateOf(0L)
        private set
    var duration by mutableLongStateOf(0L)
        private set
    var isBuffering by mutableStateOf(false)
        private set
    var playerError by mutableStateOf<String?>(null)
        private set
    var isRetrying by mutableStateOf(false)
        private set

    // ── UI state ──────────────────────────────────────────
    var showControls by mutableStateOf(true)
        private set
    var controlsAlpha by mutableFloatStateOf(1f)
        private set
    var isLocked by mutableStateOf(false)
        private set
    var playbackSpeed by mutableFloatStateOf(1.0f)
        private set

    // ── Aspect ratio ──────────────────────────────────────
    var aspectMode by mutableStateOf(AspectMode.FIT)
        private set

    // ── Track selection ───────────────────────────────────
    var currentTracks by mutableStateOf(Tracks.EMPTY)
        private set
    var subtitlesEnabled by mutableStateOf(true)
        private set

    // ── Gesture state ─────────────────────────────────────
    var seekGestureActive by mutableStateOf(false)
        private set
    var seekGestureDelta by mutableFloatStateOf(0f)
        private set
    var brightnessGestureActive by mutableStateOf(false)
        private set
    var brightnessGestureDelta by mutableFloatStateOf(0f)
        private set
    var volumeGestureActive by mutableStateOf(false)
        private set
    var volumeGestureDelta by mutableFloatStateOf(0f)
        private set
    var gestureInitialPosition by mutableLongStateOf(0L)
        private set
    var gestureInitialBrightness by mutableFloatStateOf(0f)
        private set
    var gestureInitialVolume by mutableFloatStateOf(0f)
        private set

    // ── Double-tap state ──────────────────────────────────
    var doubleTapSide by mutableStateOf(0)
        private set
    var doubleTapAlpha by mutableFloatStateOf(0f)
        private set

    // ── Screenshot state ──────────────────────────────────
    var showScreenshotFlash by mutableStateOf(false)
        private set

    // ── Public update methods ─────────────────────────────

    fun updatePlayingState(playing: Boolean) { isPlaying = playing }
    fun updatePosition(position: Long) { currentPosition = position }
    fun updateDuration(dur: Long) { duration = dur }
    fun updateBufferingState(buffering: Boolean) { isBuffering = buffering }
    fun updateError(error: String?) { playerError = error }
    fun updateRetryingState(retrying: Boolean) { isRetrying = retrying }

    fun toggleControls() { showControls = !showControls }
    fun hideControls() { showControls = false }
    fun showControls() { showControls = true }
    fun toggleLock() { isLocked = !isLocked }

    fun updatePlaybackSpeed(speed: Float) { playbackSpeed = speed }
    fun updateAspectMode(mode: AspectMode) { aspectMode = mode }
    fun updateTracks(tracks: Tracks) { currentTracks = tracks }
    fun toggleSubtitles() { subtitlesEnabled = !subtitlesEnabled }

    fun updateSeekGesture(active: Boolean, delta: Float = seekGestureDelta) {
        seekGestureActive = active
        seekGestureDelta = delta
    }

    fun updateBrightnessGesture(active: Boolean, delta: Float = brightnessGestureDelta) {
        brightnessGestureActive = active
        brightnessGestureDelta = delta
    }

    fun updateVolumeGesture(active: Boolean, delta: Float = volumeGestureDelta) {
        volumeGestureActive = active
        volumeGestureDelta = delta
    }

    fun updateGestureInitialPositions(position: Long, brightness: Float, volume: Float) {
        gestureInitialPosition = position
        gestureInitialBrightness = brightness
        gestureInitialVolume = volume
    }

    fun updateDoubleTap(side: Int) { doubleTapSide = side }
    fun updateDoubleTapAlpha(alpha: Float) { doubleTapAlpha = alpha }
    fun updateScreenshotFlash(flash: Boolean) { showScreenshotFlash = flash }
}

/**
 * Aspect ratio mode enum.
 */
enum class AspectMode { FIT, FILL, CROP, WIDE_16_9 }

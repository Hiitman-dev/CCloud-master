package com.pira.ccloud.data.repository

import android.content.Context
import android.util.Log
import com.pira.ccloud.data.model.FontSettings
import com.pira.ccloud.data.model.SubtitleSettings
import com.pira.ccloud.data.model.VideoPlayerSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : ISettingsRepository {

    companion object {
        private const val TAG = "SettingsRepository"
        private const val SUBTITLE_FILE = "subtitle_settings.json"
        private const val VIDEO_PLAYER_FILE = "video_player_settings.json"
        private const val FONT_FILE = "font_settings.json"
        private const val WELCOME_FILE = "welcome_completed.flag"
    }

    // In-memory caches
    private var cachedSubtitleSettings: SubtitleSettings? = null
    private var cachedVideoPlayerSettings: VideoPlayerSettings? = null
    private var cachedFontSettings: FontSettings? = null
    private var cachedWelcomeCompleted: Boolean? = null

    // ── Subtitle Settings ───────────────────────────────────

    override suspend fun saveSubtitleSettings(settings: SubtitleSettings) = withContext(Dispatchers.IO) {
        try {
            val jsonString = Json.encodeToString(settings)
            File(context.filesDir, SUBTITLE_FILE).writeText(jsonString)
            cachedSubtitleSettings = settings
            Log.d(TAG, "Subtitle settings saved")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving subtitle settings", e)
        }
    }

    override suspend fun loadSubtitleSettings(): SubtitleSettings {
        return cachedSubtitleSettings ?: try {
            val file = File(context.filesDir, SUBTITLE_FILE)
            if (file.exists()) {
                Json.decodeFromString<SubtitleSettings>(file.readText()).also { cachedSubtitleSettings = it }
            } else {
                SubtitleSettings.getDefaultSettings(context).also { cachedSubtitleSettings = it }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading subtitle settings", e)
            SubtitleSettings.getDefaultSettings(context).also { cachedSubtitleSettings = it }
        }
    }

    // ── Video Player Settings ───────────────────────────────

    override suspend fun saveVideoPlayerSettings(settings: VideoPlayerSettings) = withContext(Dispatchers.IO) {
        try {
            val jsonString = Json.encodeToString(settings)
            File(context.filesDir, VIDEO_PLAYER_FILE).writeText(jsonString)
            cachedVideoPlayerSettings = settings
            Log.d(TAG, "Video player settings saved")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving video player settings", e)
        }
    }

    override suspend fun loadVideoPlayerSettings(): VideoPlayerSettings {
        return cachedVideoPlayerSettings ?: try {
            val file = File(context.filesDir, VIDEO_PLAYER_FILE)
            if (file.exists()) {
                Json.decodeFromString<VideoPlayerSettings>(file.readText()).also { cachedVideoPlayerSettings = it }
            } else {
                VideoPlayerSettings.DEFAULT.also { cachedVideoPlayerSettings = it }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading video player settings", e)
            VideoPlayerSettings.DEFAULT.also { cachedVideoPlayerSettings = it }
        }
    }

    // ── Font Settings ───────────────────────────────────────

    override suspend fun saveFontSettings(settings: FontSettings) = withContext(Dispatchers.IO) {
        try {
            val jsonString = Json.encodeToString(settings)
            File(context.filesDir, FONT_FILE).writeText(jsonString)
            cachedFontSettings = settings
            Log.d(TAG, "Font settings saved")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving font settings", e)
        }
    }

    override suspend fun loadFontSettings(): FontSettings {
        return cachedFontSettings ?: try {
            val file = File(context.filesDir, FONT_FILE)
            if (file.exists()) {
                Json.decodeFromString<FontSettings>(file.readText()).also { cachedFontSettings = it }
            } else {
                FontSettings.DEFAULT.also { cachedFontSettings = it }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading font settings", e)
            FontSettings.DEFAULT.also { cachedFontSettings = it }
        }
    }

    // ── Welcome State ───────────────────────────────────────

    override suspend fun saveWelcomeCompleted() = withContext(Dispatchers.IO) {
        try {
            File(context.filesDir, WELCOME_FILE).writeText("completed")
            cachedWelcomeCompleted = true
            Log.d(TAG, "Welcome screen marked as completed")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving welcome completed state", e)
        }
    }

    override suspend fun isWelcomeCompleted(): Boolean {
        return cachedWelcomeCompleted ?: try {
            File(context.filesDir, WELCOME_FILE).exists().also { cachedWelcomeCompleted = it }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking welcome completed state", e)
            false.also { cachedWelcomeCompleted = it }
        }
    }
}

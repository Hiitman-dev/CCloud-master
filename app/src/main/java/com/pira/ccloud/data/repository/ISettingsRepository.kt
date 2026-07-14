package com.pira.ccloud.data.repository

import com.pira.ccloud.data.model.FontSettings
import com.pira.ccloud.data.model.SubtitleSettings
import com.pira.ccloud.data.model.VideoPlayerSettings

interface ISettingsRepository {
    // Subtitle settings
    suspend fun saveSubtitleSettings(settings: SubtitleSettings)
    suspend fun loadSubtitleSettings(): SubtitleSettings

    // Video player settings
    suspend fun saveVideoPlayerSettings(settings: VideoPlayerSettings)
    suspend fun loadVideoPlayerSettings(): VideoPlayerSettings

    // Font settings
    suspend fun saveFontSettings(settings: FontSettings)
    suspend fun loadFontSettings(): FontSettings

    // Welcome state
    suspend fun saveWelcomeCompleted()
    suspend fun isWelcomeCompleted(): Boolean
}

package com.pira.ccloud

import android.app.Application
import com.pira.ccloud.data.repository.IContentCacheRepository
import com.pira.ccloud.data.repository.IFavoritesRepository
import com.pira.ccloud.data.repository.IHistoryRepository
import com.pira.ccloud.data.repository.ISettingsRepository
import com.pira.ccloud.utils.StorageUtils
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class CCloudApplication : Application() {

    @Inject lateinit var favoritesRepository: IFavoritesRepository
    @Inject lateinit var settingsRepository: ISettingsRepository
    @Inject lateinit var historyRepository: IHistoryRepository
    @Inject lateinit var contentCacheRepository: IContentCacheRepository

    override fun onCreate() {
        super.onCreate()
        // Initialize StorageUtils facade with injected repositories
        // This allows existing code to continue working while new code uses repositories directly
        StorageUtils.initialize(
            favoritesRepository = favoritesRepository,
            settingsRepository = settingsRepository,
            historyRepository = historyRepository,
            contentCacheRepository = contentCacheRepository
        )
    }
}

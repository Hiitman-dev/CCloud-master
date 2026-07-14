package com.pira.ccloud.di

import com.pira.ccloud.data.repository.ContentCacheRepository
import com.pira.ccloud.data.repository.FavoritesRepository
import com.pira.ccloud.data.repository.HistoryRepository
import com.pira.ccloud.data.repository.IContentCacheRepository
import com.pira.ccloud.data.repository.IFavoritesRepository
import com.pira.ccloud.data.repository.IHistoryRepository
import com.pira.ccloud.data.repository.ISettingsRepository
import com.pira.ccloud.data.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StorageModule {

    @Binds
    @Singleton
    abstract fun bindFavoritesRepository(impl: FavoritesRepository): IFavoritesRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepository): ISettingsRepository

    @Binds
    @Singleton
    abstract fun bindHistoryRepository(impl: HistoryRepository): IHistoryRepository

    @Binds
    @Singleton
    abstract fun bindContentCacheRepository(impl: ContentCacheRepository): IContentCacheRepository
}

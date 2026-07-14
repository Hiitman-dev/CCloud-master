package com.pira.ccloud.di

import com.pira.ccloud.data.repository.CountryPostersRepository
import com.pira.ccloud.data.repository.CountryRepository
import com.pira.ccloud.data.repository.GenreRepository
import com.pira.ccloud.data.repository.ICountryPostersRepository
import com.pira.ccloud.data.repository.ICountryRepository
import com.pira.ccloud.data.repository.IGenreRepository
import com.pira.ccloud.data.repository.IMovieRepository
import com.pira.ccloud.data.repository.ISearchRepository
import com.pira.ccloud.data.repository.ISeriesRepository
import com.pira.ccloud.data.repository.ISeasonsRepository
import com.pira.ccloud.data.repository.MovieRepository
import com.pira.ccloud.data.repository.SearchRepository
import com.pira.ccloud.data.repository.SeriesRepository
import com.pira.ccloud.data.repository.SeasonsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMovieRepository(impl: MovieRepository): IMovieRepository

    @Binds
    @Singleton
    abstract fun bindSeriesRepository(impl: SeriesRepository): ISeriesRepository

    @Binds
    @Singleton
    abstract fun bindSearchRepository(impl: SearchRepository): ISearchRepository

    @Binds
    @Singleton
    abstract fun bindGenreRepository(impl: GenreRepository): IGenreRepository

    @Binds
    @Singleton
    abstract fun bindCountryRepository(impl: CountryRepository): ICountryRepository

    @Binds
    @Singleton
    abstract fun bindCountryPostersRepository(impl: CountryPostersRepository): ICountryPostersRepository

    @Binds
    @Singleton
    abstract fun bindSeasonsRepository(impl: SeasonsRepository): ISeasonsRepository
}

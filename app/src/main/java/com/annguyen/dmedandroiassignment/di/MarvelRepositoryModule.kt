package com.annguyen.dmedandroiassignment.di

import android.content.Context
import com.annguyen.dmed.data.repository.RepositoryImpl
import com.annguyen.dmed.data.repository.local.ComicDatabase
import com.annguyen.dmed.data.repository.local.LocalDataSource
import com.annguyen.dmed.data.repository.local.LocalDataSourceImpl
import com.annguyen.dmed.data.repository.network.MarvelAPIService
import com.annguyen.dmed.data.repository.network.NetworkDataSource
import com.annguyen.dmed.data.repository.network.NetworkDataSourceImpl
import com.annguyen.dmed.domain.repository.Repository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MarvelRepositoryModule {

    @Singleton
    @Binds
    abstract fun provideRepository(
        repository: RepositoryImpl
    ): Repository

    @Singleton
    @Binds
    abstract fun provideLocalDataSource(
        localDataSource: LocalDataSourceImpl
    ): LocalDataSource

    @Singleton
    @Binds
    abstract fun provideNetworkDataSource(
        networkDataSource: NetworkDataSourceImpl
    ): NetworkDataSource
}

@Module
@InstallIn(SingletonComponent::class)
object LocalDatabaseModule {

    @Singleton
    @Provides
    fun provideLocalDatabase(@ApplicationContext context: Context): ComicDatabase =
        ComicDatabase.create(context)

    @Singleton
    @Provides
    fun provideAPIService(): MarvelAPIService = MarvelAPIService.build()
}
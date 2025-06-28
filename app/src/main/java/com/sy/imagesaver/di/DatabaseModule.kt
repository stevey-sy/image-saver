package com.sy.imagesaver.di

import android.content.Context
import androidx.room.Room
import com.sy.imagesaver.data.local.AppDatabase
import com.sy.imagesaver.data.local.dao.MediaDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return AppDatabase.getDatabase(context)
    }
    
    @Provides
    @Singleton
    fun provideMediaDao(database: AppDatabase): MediaDao {
        return database.mediaDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
interface DatabaseModuleBinds {
    
    @dagger.Binds
    @Singleton
    fun bindMediaLocalDataSource(
        mediaLocalDataSourceImpl: com.sy.imagesaver.data.local.datasource.MediaLocalDataSourceImpl
    ): com.sy.imagesaver.data.local.datasource.MediaLocalDataSource
} 
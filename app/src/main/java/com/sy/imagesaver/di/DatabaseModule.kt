package com.sy.imagesaver.di

import android.content.Context
import androidx.room.Room
import com.sy.imagesaver.data.local.AppDatabase
import com.sy.imagesaver.data.local.dao.BookmarkDao
import com.sy.imagesaver.data.local.datasource.BookmarkLocalDataSource
import com.sy.imagesaver.data.local.datasource.BookmarkLocalDataSourceImpl
import dagger.Binds
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
    fun provideBookmarkDao(database: AppDatabase): BookmarkDao {
        return database.bookmarkDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
interface DatabaseModuleBinds {
    
    @Binds
    @Singleton
    fun bindBookmarkLocalDataSource(
        bookmarkLocalDataSourceImpl: BookmarkLocalDataSourceImpl
    ): BookmarkLocalDataSource
} 
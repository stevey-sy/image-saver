package com.sy.imagesaver.di

import android.content.Context
import com.sy.imagesaver.data.repository.BookmarkRepository
import com.sy.imagesaver.data.repository.BookmarkRepositoryImpl
import com.sy.imagesaver.util.NetworkUtil
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    
    @Binds
    abstract fun bindBookmarkRepository(
        bookmarkRepositoryImpl: BookmarkRepositoryImpl
    ): BookmarkRepository
}

@Module
@InstallIn(SingletonComponent::class)
object UtilModule {
    
    @Provides
    @Singleton
    fun provideNetworkUtil(
        @ApplicationContext context: Context
    ): NetworkUtil {
        return NetworkUtil(context)
    }
}
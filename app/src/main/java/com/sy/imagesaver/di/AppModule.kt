package com.sy.imagesaver.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    // VideoPlayerManager는 더 이상 필요하지 않음
}
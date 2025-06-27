package com.sy.imagesaver.data.remote.datasource

import com.sy.imagesaver.data.remote.dto.KakaoVideoResponseDto

interface VideoRemoteDataSource {
    suspend fun searchVideos(
        query: String,
        page: Int = 1,
        size: Int = 30
    ): KakaoVideoResponseDto
} 
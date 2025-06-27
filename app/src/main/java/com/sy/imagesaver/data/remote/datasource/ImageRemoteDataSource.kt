package com.sy.imagesaver.data.remote.datasource

import com.sy.imagesaver.data.remote.dto.KakaoImageResponseDto

interface ImageRemoteDataSource {
    suspend fun searchImages(
        query: String,
        page: Int = 1,
        size: Int = 30
    ): KakaoImageResponseDto
} 
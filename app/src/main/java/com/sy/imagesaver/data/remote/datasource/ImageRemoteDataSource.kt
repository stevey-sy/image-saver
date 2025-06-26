package com.sy.imagesaver.data.remote.datasource

import com.sy.imagesaver.data.remote.dto.ImageSearchResponseDto

interface ImageRemoteDataSource {
    suspend fun searchImages(
        query: String,
        page: Int = 1,
        size: Int = 30
    ): ImageSearchResponseDto
} 
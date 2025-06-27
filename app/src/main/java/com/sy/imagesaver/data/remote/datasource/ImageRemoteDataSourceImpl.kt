package com.sy.imagesaver.data.remote.datasource

import KakaoApiService
import com.sy.imagesaver.data.remote.dto.KakaoImageResponseDto
import javax.inject.Inject

class ImageRemoteDataSourceImpl @Inject constructor(
    private val kakaoApiService: KakaoApiService
) : ImageRemoteDataSource {
    
    override suspend fun searchImages(
        query: String,
        page: Int,
        size: Int
    ): KakaoImageResponseDto {
        return kakaoApiService.searchImages(
            query = query,
            page = page,
            size = size
        )
    }
} 
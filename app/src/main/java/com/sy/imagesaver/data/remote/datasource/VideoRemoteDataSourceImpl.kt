package com.sy.imagesaver.data.remote.datasource

import com.sy.imagesaver.data.remote.dto.VideoSearchResponseDto
import com.sy.imagesaver.data.remote.service.KakaoApiService
import javax.inject.Inject

class VideoRemoteDataSourceImpl @Inject constructor(
    private val kakaoApiService: KakaoApiService
) : VideoRemoteDataSource {
    
    override suspend fun searchVideos(
        query: String,
        page: Int,
        size: Int
    ): VideoSearchResponseDto {
        return kakaoApiService.searchVideos(
            query = query,
            page = page,
            size = size
        )
    }
} 
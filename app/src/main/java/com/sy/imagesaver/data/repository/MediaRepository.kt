package com.sy.imagesaver.data.repository

import com.sy.imagesaver.data.remote.dto.KakaoResponseDto
import com.sy.imagesaver.domain.data.Media
import kotlinx.coroutines.flow.Flow

interface MediaRepository {
    fun searchMedia(
        query: String,
        page: Int = 1,
        size: Int = 30
    ): Flow<KakaoResponseDto<Media>>
}
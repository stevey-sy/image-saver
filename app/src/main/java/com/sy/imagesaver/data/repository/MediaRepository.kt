package com.sy.imagesaver.data.repository

import androidx.paging.PagingData
import com.sy.imagesaver.data.remote.dto.KakaoResponseDto
import com.sy.imagesaver.domain.data.Media
import com.sy.imagesaver.presentation.model.MediaUiModel
import kotlinx.coroutines.flow.Flow

interface MediaRepository {
    fun searchMedia(
        query: String,
        page: Int = 1,
        size: Int = 30
    ): Flow<KakaoResponseDto<Media>>
    
    fun searchMediaPaged(query: String): Flow<PagingData<MediaUiModel>>
}
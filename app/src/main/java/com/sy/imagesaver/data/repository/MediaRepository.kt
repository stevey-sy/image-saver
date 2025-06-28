package com.sy.imagesaver.data.repository

import androidx.paging.PagingData
import com.sy.imagesaver.presentation.model.MediaUiModel
import kotlinx.coroutines.flow.Flow

interface MediaRepository {
    fun searchMediaPaged(query: String): Flow<PagingData<MediaUiModel>>
    suspend fun getBookmarkedThumbnailUrls(): List<String>
    
    // 캐시 관련 메서드
    fun searchMediaPagedWithCache(query: String): Flow<PagingData<MediaUiModel>>
    suspend fun clearSearchCache()
    suspend fun getCacheInfo(): Map<String, Long> // query -> remaining minutes
}
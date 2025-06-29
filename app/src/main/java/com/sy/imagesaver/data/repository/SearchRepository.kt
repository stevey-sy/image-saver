package com.sy.imagesaver.data.repository

import androidx.paging.PagingData
import com.sy.imagesaver.presentation.model.SearchResultUiModel
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun searchMediaPaged(query: String): Flow<PagingData<SearchResultUiModel>>
    suspend fun getBookmarkedThumbnailUrls(): List<String>
    
    // 캐시 관련 메서드
    fun searchMediaPagedWithCache(query: String): Flow<PagingData<SearchResultUiModel>>
    suspend fun clearSearchCache()
    suspend fun getCacheInfo(): Map<String, Long> // query -> remaining minutes
}
package com.sy.imagesaver.data.repository

import androidx.paging.PagingData
import com.sy.imagesaver.presentation.model.SearchResultUiModel
import kotlinx.coroutines.flow.Flow
import com.sy.imagesaver.data.cache.CachedQueryInfo

interface SearchRepository {
    fun searchMediaPaged(query: String): Flow<PagingData<SearchResultUiModel>>
    suspend fun getBookmarkedThumbnailUrls(): List<String>
    
    // 캐시 관련 메서드
    fun searchMediaPagedWithCache(query: String): Flow<PagingData<SearchResultUiModel>>
    suspend fun clearSearchCache()
    suspend fun getCacheInfo(): Map<String, Long> // query -> remaining minutes
    suspend fun getCachedQueryList(): List<String> // 캐시된 검색어 목록 반환
    suspend fun getCachedQueryListWithTime(): List<CachedQueryInfo> // 캐시된 검색어와 시간 정보 반환
}
package com.sy.imagesaver.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sy.imagesaver.data.cache.CachedQueryInfo
import com.sy.imagesaver.data.cache.SearchCacheManager
import com.sy.imagesaver.data.local.datasource.BookmarkLocalDataSource
import com.sy.imagesaver.data.mapper.SearchResultMapper
import com.sy.imagesaver.data.remote.datasource.ImageRemoteDataSource
import com.sy.imagesaver.data.remote.datasource.VideoRemoteDataSource
import com.sy.imagesaver.data.remote.paging.SearchResultPagingSource
import com.sy.imagesaver.presentation.model.SearchResultUiModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val imageRemoteDataSource: ImageRemoteDataSource,
    private val videoRemoteDataSource: VideoRemoteDataSource,
    private val bookmarkLocalDataSource: BookmarkLocalDataSource,
    private val searchCacheManager: SearchCacheManager,
    private val searchResultMapper: SearchResultMapper
) : SearchRepository {

    companion object {
        private const val PAGE_SIZE = 20 // 이미지와 비디오 각각 가져올 개수
    }
    
    override fun searchMediaPaged(query: String): Flow<PagingData<SearchResultUiModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE * 2, // 이미지와 비디오 합쳐서 총 40개
                enablePlaceholders = false,
                prefetchDistance = 1 // 미리 가져오는 페이지 수를 1로 제한
            ),
            pagingSourceFactory = {
                SearchResultPagingSource(
                    imageRemoteDataSource,
                    videoRemoteDataSource,
                    bookmarkLocalDataSource,
                    searchCacheManager,
                    searchResultMapper,
                    query,
                    PAGE_SIZE
                )
            }
        ).flow
    }
    
    override fun searchMediaPagedWithCache(query: String): Flow<PagingData<SearchResultUiModel>> {
        return searchMediaPaged(query)
    }
    
    override suspend fun getBookmarkedThumbnailUrls(): List<String> {
        return bookmarkLocalDataSource.getBookmarkedThumbnailUrls()
    }
    
    override suspend fun clearSearchCache() {
        searchCacheManager.clearCache()
    }
    
    override suspend fun getCacheInfo(): Map<String, Long> {
        return searchCacheManager.getCacheInfo()
    }
    
    override suspend fun getCachedQueryList(): List<String> {
        return searchCacheManager.getCachedQueries()
    }
    
    override suspend fun getCachedQueryListWithTime(): List<CachedQueryInfo> {
        return searchCacheManager.getCachedQueriesWithTime()
    }
}
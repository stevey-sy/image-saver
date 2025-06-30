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
import com.sy.imagesaver.data.remote.paging.MediaPagingSource
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
    
    override fun searchMediaPaged(query: String): Flow<PagingData<SearchResultUiModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = 30,
                enablePlaceholders = false,
                prefetchDistance = 3
            ),
            pagingSourceFactory = {
                MediaPagingSource(
                    imageRemoteDataSource,
                    videoRemoteDataSource,
                    bookmarkLocalDataSource,
                    searchCacheManager,
                    searchResultMapper,
                    query
                )
            }
        ).flow
    }
    
    override fun searchMediaPagedWithCache(query: String): Flow<PagingData<SearchResultUiModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = 30,
                enablePlaceholders = false,
                prefetchDistance = 3
            ),
            pagingSourceFactory = {
                MediaPagingSource(
                    imageRemoteDataSource,
                    videoRemoteDataSource,
                    bookmarkLocalDataSource,
                    searchCacheManager,
                    searchResultMapper,
                    query
                )
            }
        ).flow
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
    
    override suspend fun getCachedQueries(): List<String> {
        return searchCacheManager.getCachedQueries()
    }
    
    override suspend fun getCachedQueriesWithTime(): List<CachedQueryInfo> {
        return searchCacheManager.getCachedQueriesWithTime()
    }
}
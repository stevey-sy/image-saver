package com.sy.imagesaver.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sy.imagesaver.data.cache.SearchCacheManager
import com.sy.imagesaver.data.local.datasource.BookmarkLocalDataSource
import com.sy.imagesaver.data.mapper.MediaDtoMapper
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
    private val mediaDtoMapper: MediaDtoMapper
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
                    mediaDtoMapper,
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
                    mediaDtoMapper,
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
}
package com.sy.imagesaver.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sy.imagesaver.data.local.datasource.MediaLocalDataSource
import com.sy.imagesaver.data.mapper.MediaDtoMapper
import com.sy.imagesaver.data.remote.datasource.ImageRemoteDataSource
import com.sy.imagesaver.data.remote.datasource.VideoRemoteDataSource
import com.sy.imagesaver.data.remote.paging.MediaPagingSource
import com.sy.imagesaver.presentation.model.MediaUiModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(
    private val imageRemoteDataSource: ImageRemoteDataSource,
    private val videoRemoteDataSource: VideoRemoteDataSource,
    private val mediaLocalDataSource: MediaLocalDataSource,
    private val mediaDtoMapper: MediaDtoMapper
) : MediaRepository {
    
    override fun searchMediaPaged(query: String): Flow<PagingData<MediaUiModel>> {
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
                    mediaLocalDataSource,
                    mediaDtoMapper,
                    query
                )
            }
        ).flow
    }
    
    override suspend fun getBookmarkedThumbnailUrls(): List<String> {
        return mediaLocalDataSource.getBookmarkedThumbnailUrls()
    }
}
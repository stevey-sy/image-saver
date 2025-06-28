package com.sy.imagesaver.domain.usecase

import com.sy.imagesaver.data.local.datasource.MediaLocalDataSource
import com.sy.imagesaver.data.local.entity.MediaEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBookmarkedMediaUseCase @Inject constructor(
    private val mediaLocalDataSource: MediaLocalDataSource
) {
    
    fun getAllBookmarkedMedia(): Flow<List<MediaEntity>> {
        return mediaLocalDataSource.getAllMedia()
    }
    
    fun getBookmarkedMediaByType(type: String): Flow<List<MediaEntity>> {
        return mediaLocalDataSource.getMediaByType(type)
    }
    
    suspend fun getBookmarkedMediaById(id: Int): MediaEntity? {
        return mediaLocalDataSource.getMediaById(id)
    }
    
    suspend fun getBookmarkedMediaCount(): Int {
        return mediaLocalDataSource.getMediaCount()
    }
} 
package com.sy.imagesaver.data.local.datasource

import com.sy.imagesaver.data.local.entity.MediaEntity
import kotlinx.coroutines.flow.Flow

interface MediaLocalDataSource {
    
    fun getAllMedia(): Flow<List<MediaEntity>>
    
    fun getMediaByType(type: String): Flow<List<MediaEntity>>
    
    suspend fun getMediaById(id: Int): MediaEntity?
    
    suspend fun insertMedia(media: MediaEntity): Long
    
    suspend fun insertMediaList(mediaList: List<MediaEntity>)
    
    suspend fun updateMedia(media: MediaEntity)
    
    suspend fun deleteMedia(media: MediaEntity)
    
    suspend fun deleteMediaById(id: Int)
    
    suspend fun deleteAllMedia()
    
    suspend fun getMediaCount(): Int
}
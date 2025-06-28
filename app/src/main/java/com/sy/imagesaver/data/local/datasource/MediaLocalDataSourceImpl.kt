package com.sy.imagesaver.data.local.datasource

import com.sy.imagesaver.data.local.dao.MediaDao
import com.sy.imagesaver.data.local.entity.MediaEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MediaLocalDataSourceImpl @Inject constructor(
    private val mediaDao: MediaDao
) : MediaLocalDataSource {
    
    override fun getAllMedia(): Flow<List<MediaEntity>> {
        return mediaDao.getAllMedia()
    }
    
    override fun getMediaByType(type: String): Flow<List<MediaEntity>> {
        return mediaDao.getMediaByType(type)
    }
    
    override suspend fun getMediaById(id: Int): MediaEntity? {
        return mediaDao.getMediaById(id)
    }
    
    override suspend fun insertMedia(media: MediaEntity): Long {
        return mediaDao.insertMedia(media)
    }
    
    override suspend fun insertMediaList(mediaList: List<MediaEntity>) {
        mediaDao.insertMediaList(mediaList)
    }
    
    override suspend fun updateMedia(media: MediaEntity) {
        mediaDao.updateMedia(media)
    }
    
    override suspend fun deleteMedia(media: MediaEntity) {
        mediaDao.deleteMedia(media)
    }
    
    override suspend fun deleteMediaById(id: Int) {
        mediaDao.deleteMediaById(id)
    }
    
    override suspend fun deleteAllMedia() {
        mediaDao.deleteAllMedia()
    }
    
    override suspend fun getMediaCount(): Int {
        return mediaDao.getMediaCount()
    }
}
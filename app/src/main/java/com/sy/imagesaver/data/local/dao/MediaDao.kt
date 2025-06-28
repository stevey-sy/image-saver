package com.sy.imagesaver.data.local.dao

import androidx.room.*
import com.sy.imagesaver.data.local.entity.MediaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaDao {
    
    @Query("SELECT * FROM media ORDER BY createdAt DESC")
    fun getAllMedia(): Flow<List<MediaEntity>>
    
    @Query("SELECT * FROM media WHERE type = :type ORDER BY createdAt DESC")
    fun getMediaByType(type: String): Flow<List<MediaEntity>>
    
    @Query("SELECT * FROM media WHERE id = :id")
    suspend fun getMediaById(id: Int): MediaEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedia(media: MediaEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMediaList(mediaList: List<MediaEntity>)
    
    @Update
    suspend fun updateMedia(media: MediaEntity)
    
    @Delete
    suspend fun deleteMedia(media: MediaEntity)
    
    @Query("DELETE FROM media WHERE id = :id")
    suspend fun deleteMediaById(id: Int)
    
    @Query("DELETE FROM media")
    suspend fun deleteAllMedia()
    
    @Query("SELECT COUNT(*) FROM media")
    suspend fun getMediaCount(): Int
    
    @Query("SELECT thumbnailUrl FROM media")
    suspend fun getBookmarkedThumbnailUrls(): List<String>
}


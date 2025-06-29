package com.sy.imagesaver.data.local.dao

import androidx.room.*
import com.sy.imagesaver.data.local.entity.BookmarkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    
    @Query("SELECT * FROM bookmark ORDER BY createdAt DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>
    
    @Query("SELECT * FROM bookmark WHERE type = :type ORDER BY createdAt DESC")
    fun getBookmarksByType(type: String): Flow<List<BookmarkEntity>>
    
    @Query("SELECT * FROM bookmark WHERE id = :id")
    suspend fun getBookmarkById(id: Int): BookmarkEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity): Long
    
    @Update
    suspend fun updateBookmark(bookmark: BookmarkEntity)
    
    @Delete
    suspend fun deleteBookmark(bookmark: BookmarkEntity)
    
    @Query("DELETE FROM bookmark WHERE id = :id")
    suspend fun deleteBookmarkById(id: Int)
    
    @Query("DELETE FROM bookmark")
    suspend fun deleteAllBookmarks()
    
    @Query("SELECT COUNT(*) FROM bookmark")
    suspend fun getBookmarkCount(): Int
    
    @Query("SELECT thumbnailUrl FROM bookmark")
    suspend fun getBookmarkedThumbnailUrls(): List<String>
}


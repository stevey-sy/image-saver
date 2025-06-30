package com.sy.imagesaver.data.local.datasource

import com.sy.imagesaver.data.local.entity.BookmarkEntity
import com.sy.imagesaver.domain.data.MediaType
import kotlinx.coroutines.flow.Flow

interface BookmarkLocalDataSource {
    
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>
    
    fun getBookmarksByType(type: MediaType): Flow<List<BookmarkEntity>>
    
    suspend fun getBookmarkById(id: Int): BookmarkEntity?
    
    suspend fun insertBookmark(bookmark: BookmarkEntity): Long
    
    suspend fun deleteBookmark(bookmark: BookmarkEntity)
    
    suspend fun deleteBookmarkById(id: Int)
    
    suspend fun deleteAllBookmarks()
    
    suspend fun getBookmarkCount(): Int
    
    suspend fun getBookmarkedThumbnailUrls(): List<String>
}
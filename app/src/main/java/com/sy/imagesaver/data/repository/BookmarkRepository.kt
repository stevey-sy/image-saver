package com.sy.imagesaver.data.repository

import com.sy.imagesaver.domain.data.Bookmark
import com.sy.imagesaver.domain.data.MediaType
import kotlinx.coroutines.flow.Flow

interface BookmarkRepository {
    
    fun getAllBookmarks(): Flow<List<Bookmark>>
    
    fun getBookmarksByType(type: MediaType): Flow<List<Bookmark>>
    
    suspend fun getBookmarkById(id: Int): Bookmark?
    
    suspend fun insertBookmark(media: Bookmark): Long

    suspend fun updateBookmark(media: Bookmark)
    
    suspend fun deleteBookmark(media: Bookmark)
    
    suspend fun deleteBookmarkById(id: Int)
    
    suspend fun deleteAllBookmark()
    
    suspend fun getBookmarkCount(): Int
    
    suspend fun getBookmarkThumbnailUrls(): List<String>
}
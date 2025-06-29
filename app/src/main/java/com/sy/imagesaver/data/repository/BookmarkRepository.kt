package com.sy.imagesaver.data.repository

import com.sy.imagesaver.domain.data.Bookmark
import kotlinx.coroutines.flow.Flow

interface BookmarkRepository {
    
    fun getAllBookmarkedMedia(): Flow<List<Bookmark>>
    
    fun getBookmarkedMediaByType(type: String): Flow<List<Bookmark>>
    
    suspend fun getBookmarkedMediaById(id: Int): Bookmark?
    
    suspend fun insertBookmark(media: Bookmark): Long

    suspend fun updateBookmark(media: Bookmark)
    
    suspend fun deleteBookmark(media: Bookmark)
    
    suspend fun deleteBookmarkById(id: Int)
    
    suspend fun deleteAllBookmark()
    
    suspend fun getBookmarkCount(): Int
    
    suspend fun getBookmarkThumbnailUrls(): List<String>
}
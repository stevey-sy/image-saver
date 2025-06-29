package com.sy.imagesaver.domain.usecase

import com.sy.imagesaver.data.repository.BookmarkRepository
import com.sy.imagesaver.domain.data.Bookmark
import com.sy.imagesaver.domain.data.MediaType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBookmarkedMediaUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    
    fun getAllBookmarkedMedia(): Flow<List<Bookmark>> {
        return bookmarkRepository.getAllBookmarks()
    }
    
    fun getBookmarkedMediaByType(type: MediaType): Flow<List<Bookmark>> {
        return bookmarkRepository.getBookmarksByType(type)
    }
    
    suspend fun getBookmarkedMediaById(id: Int): Bookmark? {
        return bookmarkRepository.getBookmarkById(id)
    }
    
    suspend fun getBookmarkedMediaCount(): Int {
        return bookmarkRepository.getBookmarkCount()
    }
} 
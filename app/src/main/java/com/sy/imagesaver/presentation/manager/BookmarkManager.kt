package com.sy.imagesaver.presentation.manager

import com.sy.imagesaver.data.repository.BookmarkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookmarkManager @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    private val _bookmarkedThumbnailUrls = MutableStateFlow<Set<String>>(emptySet())
    val bookmarkedThumbnailUrls: StateFlow<Set<String>> = _bookmarkedThumbnailUrls.asStateFlow()

    suspend fun loadBookmarkedItems() {
        val urls = bookmarkRepository.getBookmarkThumbnailUrls()
        _bookmarkedThumbnailUrls.value = urls.toSet()
    }

    fun updateBookmark(thumbnailUrl: String, isBookmarked: Boolean) {
        val current = _bookmarkedThumbnailUrls.value.toMutableSet()
        if (isBookmarked) {
            current.add(thumbnailUrl)
        } else {
            current.remove(thumbnailUrl)
        }
        _bookmarkedThumbnailUrls.value = current
    }

    fun removeBookmark(thumbnailUrl: String) {
        val current = _bookmarkedThumbnailUrls.value.toMutableSet()
        current.remove(thumbnailUrl)
        _bookmarkedThumbnailUrls.value = current
    }

    fun addBookmark(thumbnailUrl: String) {
        val current = _bookmarkedThumbnailUrls.value.toMutableSet()
        current.add(thumbnailUrl)
        _bookmarkedThumbnailUrls.value = current
    }
}
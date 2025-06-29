package com.sy.imagesaver.domain.usecase

import com.sy.imagesaver.data.repository.BookmarkRepository
import com.sy.imagesaver.domain.data.Bookmark
import com.sy.imagesaver.domain.data.MediaType
import com.sy.imagesaver.domain.data.SearchResult
import com.sy.imagesaver.util.toLong
import javax.inject.Inject
import kotlin.time.ExperimentalTime

class AddBookmarkUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    
    @OptIn(ExperimentalTime::class)
    suspend operator fun invoke(searchResult: SearchResult): Long {
        val bookmark = Bookmark(
            id = 0,
            thumbnailUrl = searchResult.thumbnailUrl,
            originalUrl = searchResult.originalUrl,
            datetime = searchResult.datetime.toLong(),
            createdAt = System.currentTimeMillis(),
            type = when (searchResult) {
                is SearchResult.Image -> MediaType.IMAGE
                is SearchResult.Video -> MediaType.VIDEO
            }
        )
        return bookmarkRepository.insertBookmark(bookmark)
    }
} 
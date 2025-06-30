package com.sy.imagesaver.presentation.model.mapper

import com.sy.imagesaver.domain.data.Bookmark
import com.sy.imagesaver.domain.data.MediaType
import com.sy.imagesaver.presentation.model.BookmarkUiModel
import com.sy.imagesaver.util.formatYmdHm
import com.sy.imagesaver.util.toInstant
import com.sy.imagesaver.util.toSeoulLocalDateTime
import javax.inject.Inject
import kotlin.time.ExperimentalTime

class BookmarkUiModelMapper @Inject constructor() {
    
    @OptIn(ExperimentalTime::class)
    fun toUiModel(bookmark: Bookmark): BookmarkUiModel {
        return BookmarkUiModel(
            id = bookmark.id,
            thumbnailUrl = bookmark.thumbnailUrl,
            originalUrl = bookmark.originalUrl,
            datetime = bookmark.datetime.toInstant().toSeoulLocalDateTime().formatYmdHm(),
            createdAt = bookmark.createdAt.toInstant().toSeoulLocalDateTime().formatYmdHm(),
            type = when (bookmark.type) {
                MediaType.IMAGE -> "이미지"
                MediaType.VIDEO -> "비디오"
            }
        )
    }

    fun toUiModelList(bookmarks: List<Bookmark>): List<BookmarkUiModel> {
        return bookmarks.map { toUiModel(it) }
    }
} 
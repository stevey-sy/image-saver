package com.sy.imagesaver.presentation.model

import com.sy.imagesaver.domain.data.Bookmark
import com.sy.imagesaver.util.formatYmdHm
import com.sy.imagesaver.util.toInstant
import com.sy.imagesaver.util.toSeoulLocalDateTime
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
data class BookmarkUiModel(
    val id: Int,
    val thumbnailUrl: String,
    val originalUrl: String,
    val datetime: String,
    val createdAt: String,
    val type: String
) {
    companion object {
        fun fromBookmark(bookmark: Bookmark): BookmarkUiModel {
            return BookmarkUiModel(
                id = bookmark.id,
                thumbnailUrl = bookmark.thumbnailUrl,
                originalUrl = bookmark.originalUrl,
                datetime = bookmark.datetime.toInstant().toSeoulLocalDateTime().formatYmdHm(),
                createdAt = bookmark.createdAt.toInstant().toSeoulLocalDateTime().formatYmdHm(),
                type = when (bookmark.type) {
                    com.sy.imagesaver.domain.data.MediaType.IMAGE -> "이미지"
                    com.sy.imagesaver.domain.data.MediaType.VIDEO -> "비디오"
                }
            )
        }
    }
}

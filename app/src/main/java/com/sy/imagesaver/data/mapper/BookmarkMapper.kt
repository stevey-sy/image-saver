package com.sy.imagesaver.data.mapper

import com.sy.imagesaver.data.local.entity.BookmarkEntity
import com.sy.imagesaver.domain.data.Bookmark
import javax.inject.Inject

class BookmarkMapper @Inject constructor() {
    
    fun toBookmark(bookmarkEntity: BookmarkEntity): Bookmark {
        return Bookmark(
            id = bookmarkEntity.id,
            thumbnailUrl = bookmarkEntity.thumbnailUrl,
            originalUrl = bookmarkEntity.originalUrl,
            datetime = bookmarkEntity.datetime,
            createdAt = bookmarkEntity.createdAt,
            type = bookmarkEntity.type
        )
    }
    
    fun toBookmarkList(bookmarkEntities: List<BookmarkEntity>): List<Bookmark> {
        return bookmarkEntities.map { toBookmark(it) }
    }
    
    fun toBookmarkEntity(bookmark: Bookmark): BookmarkEntity {
        return BookmarkEntity(
            id = if (bookmark.id == 0) 0 else bookmark.id,
            thumbnailUrl = bookmark.thumbnailUrl,
            originalUrl = bookmark.originalUrl,
            datetime = bookmark.datetime,
            createdAt = bookmark.createdAt,
            type = bookmark.type
        )
    }
} 
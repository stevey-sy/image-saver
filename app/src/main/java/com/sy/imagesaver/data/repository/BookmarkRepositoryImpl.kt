package com.sy.imagesaver.data.repository

import com.sy.imagesaver.data.local.datasource.BookmarkLocalDataSource
import com.sy.imagesaver.data.mapper.BookmarkMapper
import com.sy.imagesaver.domain.data.Bookmark
import com.sy.imagesaver.domain.data.MediaType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BookmarkRepositoryImpl @Inject constructor(
    private val bookmarkLocalDataSource: BookmarkLocalDataSource,
    private val bookmarkMapper: BookmarkMapper
) : BookmarkRepository {

    override fun getAllBookmarks(): Flow<List<Bookmark>> {
        return bookmarkLocalDataSource.getAllBookmarks()
            .map { bookmarkEntities ->
                bookmarkMapper.toBookmarkList(bookmarkEntities)
            }
    }

    override fun getBookmarksByType(type: MediaType): Flow<List<Bookmark>> {
        return bookmarkLocalDataSource.getBookmarksByType(type)
            .map { bookmarkEntities ->
                bookmarkMapper.toBookmarkList(bookmarkEntities)
            }
    }

    override suspend fun getBookmarkById(id: Int): Bookmark? {
        val bookmarkEntity = bookmarkLocalDataSource.getBookmarkById(id)
        return bookmarkEntity?.let { bookmarkMapper.toBookmark(it) }
    }
    
    override suspend fun insertBookmark(media: Bookmark): Long {
        val bookmarkEntity = bookmarkMapper.toBookmarkEntity(media)
        return bookmarkLocalDataSource.insertBookmark(bookmarkEntity)
    }
    
    override suspend fun updateBookmark(media: Bookmark) {
        val bookmarkEntity = bookmarkMapper.toBookmarkEntity(media)
        bookmarkLocalDataSource.updateBookmark(bookmarkEntity)
    }
    
    override suspend fun deleteBookmark(media: Bookmark) {
        val bookmarkEntity = bookmarkMapper.toBookmarkEntity(media)
        bookmarkLocalDataSource.deleteBookmark(bookmarkEntity)
    }
    
    override suspend fun deleteBookmarkById(id: Int) {
        bookmarkLocalDataSource.deleteBookmarkById(id)
    }

    override suspend fun deleteAllBookmark() {
        bookmarkLocalDataSource.deleteAllBookmarks()
    }
    
    override suspend fun getBookmarkCount(): Int {
        return bookmarkLocalDataSource.getBookmarkCount()
    }
    
    override suspend fun getBookmarkThumbnailUrls(): List<String> {
        return bookmarkLocalDataSource.getBookmarkedThumbnailUrls()
    }
}
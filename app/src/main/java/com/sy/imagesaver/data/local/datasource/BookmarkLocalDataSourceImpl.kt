package com.sy.imagesaver.data.local.datasource

import com.sy.imagesaver.data.local.dao.BookmarkDao
import com.sy.imagesaver.data.local.entity.BookmarkEntity
import com.sy.imagesaver.domain.data.MediaType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BookmarkLocalDataSourceImpl @Inject constructor(
    private val bookmarkDao: BookmarkDao
) : BookmarkLocalDataSource {
    
    override fun getAllBookmarks(): Flow<List<BookmarkEntity>> {
        return bookmarkDao.getAllBookmarks()
    }
    
    override fun getBookmarksByType(type: MediaType): Flow<List<BookmarkEntity>> {
        return bookmarkDao.getBookmarksByType(type)
    }
    
    override suspend fun getBookmarkById(id: Int): BookmarkEntity? {
        return bookmarkDao.getBookmarkById(id)
    }
    
    override suspend fun insertBookmark(bookmark: BookmarkEntity): Long {
        return bookmarkDao.insertBookmark(bookmark)
    }
    
    override suspend fun updateBookmark(bookmark: BookmarkEntity) {
        bookmarkDao.updateBookmark(bookmark)
    }
    
    override suspend fun deleteBookmark(bookmark: BookmarkEntity) {
        bookmarkDao.deleteBookmark(bookmark)
    }
    
    override suspend fun deleteBookmarkById(id: Int) {
        bookmarkDao.deleteBookmarkById(id)
    }
    
    override suspend fun deleteAllBookmarks() {
        bookmarkDao.deleteAllBookmarks()
    }
    
    override suspend fun getBookmarkCount(): Int {
        return bookmarkDao.getBookmarkCount()
    }
    
    override suspend fun getBookmarkedThumbnailUrls(): List<String> {
        return bookmarkDao.getBookmarkedThumbnailUrls()
    }
}
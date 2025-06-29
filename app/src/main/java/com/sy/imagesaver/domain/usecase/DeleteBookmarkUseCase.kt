package com.sy.imagesaver.domain.usecase

import com.sy.imagesaver.data.repository.BookmarkRepository
import javax.inject.Inject

class DeleteBookmarkUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    suspend operator fun invoke(id: Int) {
        bookmarkRepository.deleteBookmarkById(id)
    }
} 
package com.sy.imagesaver.data.repository

import androidx.paging.PagingData
import com.sy.imagesaver.presentation.model.MediaUiModel
import kotlinx.coroutines.flow.Flow

interface MediaRepository {
    fun searchMediaPaged(query: String): Flow<PagingData<MediaUiModel>>
    suspend fun getBookmarkedThumbnailUrls(): List<String>
}
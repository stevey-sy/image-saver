package com.sy.imagesaver.domain.usecase

import androidx.paging.PagingData
import com.sy.imagesaver.data.repository.MediaRepository
import com.sy.imagesaver.presentation.model.MediaUiModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchMediaUseCase @Inject constructor(
    private val mediaRepository: MediaRepository
) {
    
    fun searchMediaPaged(query: String): Flow<PagingData<MediaUiModel>> {
        return mediaRepository.searchMediaPaged(query)
    }
} 
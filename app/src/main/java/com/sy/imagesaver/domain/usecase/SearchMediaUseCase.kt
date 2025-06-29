package com.sy.imagesaver.domain.usecase

import androidx.paging.PagingData
import com.sy.imagesaver.data.repository.SearchRepository
import com.sy.imagesaver.presentation.model.SearchResultUiModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchMediaUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {
    
    fun searchMediaPaged(query: String): Flow<PagingData<SearchResultUiModel>> {
        return searchRepository.searchMediaPagedWithCache(query)
    }
} 
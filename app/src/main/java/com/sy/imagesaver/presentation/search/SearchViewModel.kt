package com.sy.imagesaver.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sy.imagesaver.domain.usecase.SearchMediaUseCase
import com.sy.imagesaver.presentation.model.MediaUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchMediaUseCase: SearchMediaUseCase
) : ViewModel() {
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun clearSearchQuery() {
        _searchQuery.value = ""
        _error.value = null
    }
    
    fun searchMedia(query: String) {
        // PagingData는 Composable에서 Flow로 직접 구독하므로 별도 구현 필요 없음
        _error.value = null
    }
    
    fun getSearchResultFlow(query: String): Flow<PagingData<MediaUiModel>> {
        return if (query.isNotBlank()) {
            searchMediaUseCase.searchMediaPaged(query).cachedIn(viewModelScope)
        } else {
            kotlinx.coroutines.flow.flowOf(PagingData.empty())
        }
    }
    
    fun refreshSearch() {
        val query = _searchQuery.value
        if (query.isNotBlank()) {
            searchMedia(query)
        }
    }
}
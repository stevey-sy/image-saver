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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchMediaUseCase: SearchMediaUseCase
) : ViewModel() {
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    // 검색 결과를 StateFlow로 관리
    private val _searchResult = MutableStateFlow<PagingData<MediaUiModel>>(PagingData.empty())
    val searchResult: StateFlow<PagingData<MediaUiModel>> = _searchResult.asStateFlow()
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun clearSearchQuery() {
        _searchQuery.value = ""
        _error.value = null
        _searchResult.value = PagingData.empty()
    }
    
    fun searchMedia(query: String) {
        if (query.isBlank()) {
            _searchResult.value = PagingData.empty()
            return
        }
        
        _isLoading.value = true
        _error.value = null
        
        viewModelScope.launch {
            try {
                // 새로운 검색어로 PagingData 생성
                searchMediaUseCase.searchMediaPaged(query)
                    .cachedIn(viewModelScope)
                    .collect { pagingData ->
                        _searchResult.value = pagingData
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                _error.value = e.message ?: "검색 중 오류가 발생했습니다."
                _isLoading.value = false
            }
        }
    }
    
    // PagingData를 Flow로 반환하는 함수
    fun getSearchResultFlow(): Flow<PagingData<MediaUiModel>> {
        return if (_searchQuery.value.isNotBlank()) {
            searchMediaUseCase.searchMediaPaged(_searchQuery.value)
                .cachedIn(viewModelScope)
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
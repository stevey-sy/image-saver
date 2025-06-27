package com.sy.imagesaver.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sy.imagesaver.domain.usecase.SearchMediaUseCase
import com.sy.imagesaver.data.remote.dto.KakaoResponseDto
import com.sy.imagesaver.domain.data.Media
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchMediaUseCase: SearchMediaUseCase
) : ViewModel() {
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _searchResult = MutableStateFlow<KakaoResponseDto<Media>?>(null)
    val searchResult: StateFlow<KakaoResponseDto<Media>?> = _searchResult.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private var currentPage = 1
    private val pageSize = 30
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun clearSearchQuery() {
        _searchQuery.value = ""
        _searchResult.value = null
        _error.value = null
        currentPage = 1
    }
    
    fun searchMedia(query: String, page: Int = 1) {
        if (query.isBlank()) return
        
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            searchMediaUseCase(query, page, pageSize)
                .catch { exception ->
                    _error.value = exception.message ?: "검색 중 오류가 발생했습니다."
                    _isLoading.value = false
                }
                .collect { result ->
                    if (page == 1) {
                        // 첫 페이지인 경우 기존 결과를 대체
                        _searchResult.value = result
                    } else {
                        // 다음 페이지인 경우 기존 결과에 추가
                        val currentResult = _searchResult.value
                        if (currentResult != null) {
                            val updatedMediaList = currentResult.documents + result.documents
                            _searchResult.value = currentResult.copy(
                                documents = updatedMediaList,
                                meta = result.meta
                            )
                        }
                    }
                    currentPage = page
                    _isLoading.value = false
                }
        }
    }
    
    fun loadNextPage() {
        val query = _searchQuery.value
        val currentResult = _searchResult.value
        
        if (query.isNotBlank() && currentResult != null && !currentResult.meta.isEnd) {
            searchMedia(query, currentPage + 1)
        }
    }
    
    fun refreshSearch() {
        val query = _searchQuery.value
        if (query.isNotBlank()) {
            currentPage = 1
            searchMedia(query, 1)
        }
    }
}
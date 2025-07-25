package com.sy.imagesaver.presentation.search

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sy.imagesaver.R
import com.sy.imagesaver.data.cache.CachedQueryInfo
import com.sy.imagesaver.presentation.model.mapper.SearchUiModelMapper
import com.sy.imagesaver.domain.usecase.SearchMediaUseCase
import com.sy.imagesaver.domain.usecase.AddBookmarkUseCase
import com.sy.imagesaver.presentation.model.SearchResultUiModel
import com.sy.imagesaver.data.repository.SearchRepository
import com.sy.imagesaver.presentation.manager.BookmarkManager
import com.sy.imagesaver.util.NetworkUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchMediaUseCase: SearchMediaUseCase,
    private val addBookmarkUseCase: AddBookmarkUseCase,
    private val searchRepository: SearchRepository,
    private val bookmarkManager: BookmarkManager,
    private val searchUiModelMapper: SearchUiModelMapper,
    private val networkUtil: NetworkUtil,
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    companion object {
        private const val TAG = "SearchViewModel"
    }
    
    // 내부 상태 관리용 (외부 노출 안함)
    private val _searchQuery = MutableStateFlow("")
    private val _debouncedSearchQuery = MutableStateFlow("")
    private val _isSearching = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)
    private val _cachedQueries = MutableStateFlow<List<CachedQueryInfo>>(emptyList())
    private val _isSearchFocused = MutableStateFlow(false)
    
    // SnackBar 메시지를 위한 이벤트
    private val _snackBarEvent = MutableSharedFlow<SnackBarEvent>()
    val snackBarEvent = _snackBarEvent.asSharedFlow()
    
    // UI 상태를 하나로 통합 - 외부에는 이것만 노출
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    
    init {
        // 앱 시작 시 기존 북마크된 아이템들 로드
        loadBookmarkedItems()
        
        // 캐시된 검색어 목록 로드
        loadCachedQueries()
        
        // 디바운싱 로직 설정
        setupDebouncing()
        
        // 북마크 상태 변경 감지
        setupBookmarkUpdates()
    }
    
    private fun updateUiState(update: (UiState) -> UiState) {
        _uiState.value = update(_uiState.value)
    }
    
    private fun setupDebouncing() {
        viewModelScope.launch {
            _searchQuery.collect { query ->
                updateUiState { it.copy(searchQuery = query) }
                
                if (query.isNotBlank()) {
                    _isSearching.value = true
                    updateUiState { it.copy(isSearching = true) }
                } else {
                    _isSearching.value = false
                    _debouncedSearchQuery.value = ""
                    updateUiState { 
                        it.copy(
                            isSearching = false,
                            debouncedSearchQuery = ""
                        )
                    }
                }
            }
        }
        
        viewModelScope.launch {
            _searchQuery
                .debounce(1200) // 1.2초 디바운싱
                .collect { query ->
                    _debouncedSearchQuery.value = query
                    _isSearching.value = false
                    updateUiState { 
                        it.copy(
                            debouncedSearchQuery = query,
                            isSearching = false
                        )
                    }
                }
        }
    }
    
    private fun setupBookmarkUpdates() {
        viewModelScope.launch {
            bookmarkManager.bookmarkedThumbnailUrls.collect { bookmarkedThumbnailUrls ->
                updateUiState { it.copy(bookmarkedThumbnailUrls = bookmarkedThumbnailUrls) }
            }
        }
    }
    
    private fun loadBookmarkedItems() {
        viewModelScope.launch {
            try {
                bookmarkManager.loadBookmarkedItems()
            } catch (e: Exception) {
                // 에러 처리 (선택사항)
            }
        }
    }
    
    private fun loadCachedQueries() {
        viewModelScope.launch {
            try {
                val queries = searchRepository.getCachedQueryListWithTime()
                Log.d(TAG, "Loaded cached queries: $queries")
                _cachedQueries.value = queries
                updateUiState { it.copy(cachedQueries = queries) }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading cached queries", e)
            }
        }
    }
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun clearSearchQuery() {
        _searchQuery.value = ""
        _error.value = null
        updateUiState { it.copy(error = null) }
    }
    
    fun setError(errorMessage: String) {
        _error.value = errorMessage
        updateUiState { it.copy(error = errorMessage) }
    }
    
    fun clearError() {
        _error.value = null
        updateUiState { it.copy(error = null) }
    }
    
    // 캐시된 검색어 선택
    fun selectCachedQuery(query: String) {
        _searchQuery.value = query
        _debouncedSearchQuery.value = query
        _isSearching.value = false
        _error.value = null
        updateUiState { 
            it.copy(
                searchQuery = query,
                debouncedSearchQuery = query,
                isSearching = false,
                error = null
            )
        }
    }
    
    // 검색창 포커스 상태 설정
    fun setSearchFocus(focused: Boolean) {
        _isSearchFocused.value = focused
        updateUiState { it.copy(isSearchFocused = focused) }
    }
    
    // 캐시된 검색어 목록 새로고침
    fun refreshCachedQueries() {
        loadCachedQueries()
    }
    
    fun retrySearch() {
        _error.value = null
        updateUiState { it.copy(error = null) }
        
        // 현재 검색어로 다시 검색을 트리거하기 위해 searchQuery를 다시 설정
        val currentQuery = _searchQuery.value
        if (currentQuery.isNotBlank()) {
            _searchQuery.value = ""
            _searchQuery.value = currentQuery
        }
    }
    
    fun getSearchResultFlow(query: String): Flow<PagingData<SearchResultUiModel>> {
        Log.d(TAG, "Starting search with query: $query")
        return if (query.isNotBlank()) {
            searchMediaUseCase.searchMediaPaged(query)
                .onEach { pagingData ->
                    Log.d(TAG, "Search results loaded, refreshing cached queries")
                    refreshCachedQueries()
                }
                .cachedIn(viewModelScope)
        } else {
            Log.d(TAG, "Empty query, returning empty PagingData")
            kotlinx.coroutines.flow.flowOf(PagingData.empty())
        }
    }
    
    fun clearSearchCache() {
        viewModelScope.launch {
            Log.d(TAG, "Clearing search cache")
            searchRepository.clearSearchCache()
            Log.d(TAG, "Search cache cleared")
        }
    }
    
    fun getCacheInfo() {
        viewModelScope.launch {
            val cacheInfo = searchRepository.getCacheInfo()
            Log.d(TAG, "Cache Info - Number of cached queries: ${cacheInfo.size}")
            cacheInfo.forEach { (query, time) ->
                Log.d(TAG, "Cached query: $query, Time remaining: $time minutes")
            }
        }
    }
    
    fun addBookmark(searchResultUiModel: SearchResultUiModel) {
        viewModelScope.launch {
            try {
                // uiModel을 Domain model로 변환
                val searchResult = searchUiModelMapper.toSearchResult(searchResultUiModel)
                
                val id = addBookmarkUseCase(searchResult)
                
                // BookmarkManager를 통해 북마크 상태 업데이트
                bookmarkManager.addBookmark(searchResultUiModel.thumbnailUrl)
                
            } catch (e: Exception) {
                // SnackBar 이벤트 발생
                _snackBarEvent.emit(
                    SnackBarEvent.Error(context.getString(R.string.bookmark_save_failed, e.message))
                )
            }
        }
    }
    
    // 네트워크 상태 확인
    fun isNetworkAvailable(): Boolean {
        return networkUtil.isNetworkAvailable()
    }
    
    // 에러 처리 로직
    fun handleLoadStateError(error: Throwable) {
        val errorMessage = when {
            !isNetworkAvailable() ->
                context.getString(R.string.network_error)
            error.message?.contains("Unable to resolve host") == true ->
                context.getString(R.string.network_error)
            error.message?.contains("timeout") == true ->
                context.getString(R.string.timeout_error)
            error.message?.contains("500") == true ->
                context.getString(R.string.server_error)
            error.message?.contains("404") == true ->
                context.getString(R.string.not_found_error)
            else -> context.getString(R.string.search_error, error.message)
        }
        setError(errorMessage)
    }
    
    sealed class SnackBarEvent {
        data class Success(val message: String) : SnackBarEvent()
        data class Error(val message: String) : SnackBarEvent()
    }
    
    data class UiState(
        val searchQuery: String = "",
        val debouncedSearchQuery: String = "",
        val isSearching: Boolean = false,
        val error: String? = null,
        val bookmarkedThumbnailUrls: Set<String> = emptySet(),
        val cachedQueries: List<CachedQueryInfo> = emptyList(),
        val isSearchFocused: Boolean = false
    )
}
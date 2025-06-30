package com.sy.imagesaver.presentation.search

import android.content.Context
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
import com.sy.imagesaver.di.BookmarkManager
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
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    // 디바운싱된 검색어
    private val _debouncedSearchQuery = MutableStateFlow("")
    val debouncedSearchQuery: StateFlow<String> = _debouncedSearchQuery.asStateFlow()
    
    // 검색 대기 상태 (타이핑 중)
    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    // SnackBar 메시지를 위한 이벤트
    private val _snackBarEvent = MutableSharedFlow<SnackBarEvent>()
    val snackBarEvent = _snackBarEvent.asSharedFlow()
    
    // 캐시된 검색어 목록
    private val _cachedQueries = MutableStateFlow<List<CachedQueryInfo>>(emptyList())
    val cachedQueries: StateFlow<List<CachedQueryInfo>> = _cachedQueries.asStateFlow()
    
    // 검색창 포커스 상태
    private val _isSearchFocused = MutableStateFlow(false)
    val isSearchFocused: StateFlow<Boolean> = _isSearchFocused.asStateFlow()
    
    // UI 상태를 하나로 통합
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    
    init {
        // 앱 시작 시 기존 북마크된 아이템들 로드
        loadBookmarkedItems()
        
        // 캐시된 검색어 목록 로드
        loadCachedQueries()
        
        // 디바운싱 로직 설정
        setupDebouncing()
        
        // 개별 상태들을 UiState로 통합
        setupUiStateUpdates()
    }
    
    private fun setupDebouncing() {
        viewModelScope.launch {
            searchQuery
                .collect { query ->
                    if (query.isNotBlank()) {
                        _isSearching.value = true
                    } else {
                        _isSearching.value = false
                        _debouncedSearchQuery.value = ""
                    }
                }
        }
        
        viewModelScope.launch {
            searchQuery
                .debounce(500) // 5초 디바운싱
                .collect { query ->
                    _debouncedSearchQuery.value = query
                    _isSearching.value = false
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
                _cachedQueries.value = queries
            } catch (e: Exception) {
                // 에러 처리 (선택사항)
            }
        }
    }
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun clearSearchQuery() {
        _searchQuery.value = ""
        _error.value = null
    }
    
    fun setError(errorMessage: String) {
        _error.value = errorMessage
    }
    
    fun clearError() {
        _error.value = null
    }
    
    // 캐시된 검색어 선택
    fun selectCachedQuery(query: String) {
        _searchQuery.value = query
        _debouncedSearchQuery.value = query
        _isSearching.value = false
        _error.value = null
    }
    
    // 검색창 포커스 상태 설정
    fun setSearchFocus(focused: Boolean) {
        _isSearchFocused.value = focused
    }
    
    // 캐시된 검색어 목록 새로고침
    fun refreshCachedQueries() {
        loadCachedQueries()
    }
    
    fun retrySearch() {
        _error.value = null
        // 현재 검색어로 다시 검색을 트리거하기 위해 searchQuery를 다시 설정
        val currentQuery = _searchQuery.value
        if (currentQuery.isNotBlank()) {
            _searchQuery.value = ""
            _searchQuery.value = currentQuery
        }
    }
    
    fun getSearchResultFlow(query: String): Flow<PagingData<SearchResultUiModel>> {
        return if (query.isNotBlank()) {
            searchMediaUseCase.searchMediaPaged(query)
                .onEach { pagingData ->
                    // 검색 결과가 로드되면 캐시된 쿼리 목록 갱신
                    refreshCachedQueries()
                }
                .cachedIn(viewModelScope)
        } else {
            kotlinx.coroutines.flow.flowOf(PagingData.empty())
        }
    }
    
    fun clearSearchCache() {
        viewModelScope.launch {
            searchRepository.clearSearchCache()
        }
    }
    
    fun getCacheInfo() {
        viewModelScope.launch {
            val cacheInfo = searchRepository.getCacheInfo()
            // 디버그용 로그 출력
            println("Cache Info: $cacheInfo")
        }
    }
    
    fun saveMedia(searchResultUiModel: SearchResultUiModel) {
        viewModelScope.launch {
            try {
                // MediaUiModel을 Media로 변환
                val media = searchUiModelMapper.toSearchResult(searchResultUiModel)
                
                val id = addBookmarkUseCase(media)
                
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
    
    private fun setupUiStateUpdates() {
        viewModelScope.launch {
            _searchQuery.collect { searchQuery ->
                updateUiState { it.copy(searchQuery = searchQuery) }
            }
        }
        
        viewModelScope.launch {
            _debouncedSearchQuery.collect { debouncedSearchQuery ->
                updateUiState { it.copy(debouncedSearchQuery = debouncedSearchQuery) }
            }
        }
        
        viewModelScope.launch {
            _isSearching.collect { isSearching ->
                updateUiState { it.copy(isSearching = isSearching) }
            }
        }
        
        viewModelScope.launch {
            _error.collect { error ->
                updateUiState { it.copy(error = error) }
            }
        }
        
        viewModelScope.launch {
            bookmarkManager.bookmarkedThumbnailUrls.collect { bookmarkedThumbnailUrls ->
                updateUiState { it.copy(bookmarkedThumbnailUrls = bookmarkedThumbnailUrls) }
            }
        }
        
        viewModelScope.launch {
            _cachedQueries.collect { cachedQueries ->
                updateUiState { it.copy(cachedQueries = cachedQueries) }
            }
        }
        
        viewModelScope.launch {
            _isSearchFocused.collect { isSearchFocused ->
                updateUiState { it.copy(isSearchFocused = isSearchFocused) }
            }
        }
    }
    
    private fun updateUiState(update: (UiState) -> UiState) {
        _uiState.value = update(_uiState.value)
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
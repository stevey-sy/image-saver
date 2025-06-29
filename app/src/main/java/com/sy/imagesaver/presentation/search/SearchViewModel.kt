package com.sy.imagesaver.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sy.imagesaver.data.mapper.MediaUiModelMapper
import com.sy.imagesaver.domain.usecase.SearchMediaUseCase
import com.sy.imagesaver.domain.usecase.AddBookmarkUseCase
import com.sy.imagesaver.presentation.model.SearchResultUiModel
import com.sy.imagesaver.data.repository.SearchRepository
import com.sy.imagesaver.data.repository.BookmarkRepository
import com.sy.imagesaver.util.NetworkUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.delay

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchMediaUseCase: SearchMediaUseCase,
    private val addBookmarkUseCase: AddBookmarkUseCase,
    private val searchRepository: SearchRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val mediaUiModelMapper: MediaUiModelMapper,
    private val networkUtil: NetworkUtil
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
    
    // 북마크된 아이템의 thumbnailUrl을 추적
    private val _bookmarkedThumbnailUrls = MutableStateFlow<Set<String>>(emptySet())
    val bookmarkedThumbnailUrls: StateFlow<Set<String>> = _bookmarkedThumbnailUrls.asStateFlow()
    
    // SnackBar 메시지를 위한 이벤트
    private val _snackBarEvent = MutableSharedFlow<SnackBarEvent>()
    val snackBarEvent = _snackBarEvent.asSharedFlow()
    
    init {
        // 앱 시작 시 기존 북마크된 아이템들 로드
        loadBookmarkedItems()
        
        // 디바운싱 로직 설정
        setupDebouncing()
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
                val bookmarkedUrls = bookmarkRepository.getBookmarkThumbnailUrls()
                _bookmarkedThumbnailUrls.value = bookmarkedUrls.toSet()
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
            searchMediaUseCase.searchMediaPaged(query).cachedIn(viewModelScope)
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
                val media = mediaUiModelMapper.toMedia(searchResultUiModel)
                
                val id = addBookmarkUseCase(media)
                
                // 저장 성공 시 해당 아이템을 북마크 목록에 추가
                _bookmarkedThumbnailUrls.value = _bookmarkedThumbnailUrls.value + searchResultUiModel.thumbnailUrl
                
                // SnackBar 이벤트 발생
                _snackBarEvent.emit(
                    SnackBarEvent.Success("보관함에 저장되었습니다.")
                )
                
            } catch (e: Exception) {
                // SnackBar 이벤트 발생
                _snackBarEvent.emit(
                    SnackBarEvent.Error("저장에 실패했습니다: ${e.message}")
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
                "네트워크 연결을 확인해주세요."
            error.message?.contains("Unable to resolve host") == true ->
                "네트워크 연결을 확인해주세요."
            error.message?.contains("timeout") == true ->
                "요청 시간이 초과되었습니다."
            error.message?.contains("500") == true ->
                "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
            error.message?.contains("404") == true ->
                "요청한 리소스를 찾을 수 없습니다."
            else -> "검색 중 오류가 발생했습니다: ${error.message}"
        }
        setError(errorMessage)
    }
    
    sealed class SnackBarEvent {
        data class Success(val message: String) : SnackBarEvent()
        data class Error(val message: String) : SnackBarEvent()
    }
}
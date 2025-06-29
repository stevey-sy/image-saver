package com.sy.imagesaver.presentation.bookmark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sy.imagesaver.domain.usecase.GetBookmarkedMediaUseCase
import com.sy.imagesaver.domain.usecase.DeleteBookmarkUseCase
import com.sy.imagesaver.presentation.model.BookmarkUiModel
import com.sy.imagesaver.domain.data.MediaType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class BookMarkViewModel @Inject constructor(
    private val getBookmarkedMediaUseCase: GetBookmarkedMediaUseCase,
    private val deleteBookmarkUseCase: DeleteBookmarkUseCase
) : ViewModel() {
    
    private val _bookmarkedMedia = MutableStateFlow<List<BookmarkUiModel>>(emptyList())
    val bookmarkList: StateFlow<List<BookmarkUiModel>> = _bookmarkedMedia.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    // 삭제 모드 관련 상태
    private val _isDeleteMode = MutableStateFlow(false)
    val isDeleteMode: StateFlow<Boolean> = _isDeleteMode.asStateFlow()
    
    private val _selectedItems = MutableStateFlow<Set<Int>>(emptySet())
    val selectedItems: StateFlow<Set<Int>> = _selectedItems.asStateFlow()
    
    // 필터 관련 상태
    private val _selectedFilter = MutableStateFlow<MediaType?>(null)
    val selectedFilter: StateFlow<MediaType?> = _selectedFilter.asStateFlow()
    
    // SnackBar 메시지를 위한 이벤트
    private val _snackBarEvent = MutableSharedFlow<SnackBarEvent>()
    val snackBarEvent = _snackBarEvent.asSharedFlow()
    
    // UI 상태를 하나로 통합
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    
    init {
        loadBookmarkedMedia()
        setupUiStateUpdates()
    }
    
    private fun setupUiStateUpdates() {
        viewModelScope.launch {
            // 각 상태 변경을 감지하여 UiState 업데이트
            _bookmarkedMedia.collect { bookmarkList ->
                updateUiState { it.copy(bookmarkList = bookmarkList) }
            }
        }
        
        viewModelScope.launch {
            _isLoading.collect { isLoading ->
                updateUiState { it.copy(isLoading = isLoading) }
            }
        }
        
        viewModelScope.launch {
            _error.collect { error ->
                updateUiState { it.copy(error = error) }
            }
        }
        
        viewModelScope.launch {
            _isDeleteMode.collect { isDeleteMode ->
                updateUiState { it.copy(isDeleteMode = isDeleteMode) }
            }
        }
        
        viewModelScope.launch {
            _selectedItems.collect { selectedItems ->
                updateUiState { it.copy(selectedItems = selectedItems) }
            }
        }
        
        viewModelScope.launch {
            _selectedFilter.collect { selectedFilter ->
                updateUiState { it.copy(selectedFilter = selectedFilter) }
            }
        }
    }
    
    private fun updateUiState(update: (UiState) -> UiState) {
        _uiState.value = update(_uiState.value)
    }
    
    private fun loadBookmarkedMedia() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                getBookmarkedMediaUseCase.getAllBookmarkedMedia()
                    .map { bookmarks ->
                        bookmarks.map { BookmarkUiModel.fromBookmark(it) }
                    }
                    .collect { bookmarkUiModels ->
                        _bookmarkedMedia.value = bookmarkUiModels
                        _isLoading.value = false
                    }
                    
            } catch (e: Exception) {
                _error.value = "북마크된 미디어를 불러오는데 실패했습니다: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    private fun loadBookmarkedMediaByType(type: MediaType) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                getBookmarkedMediaUseCase.getBookmarkedMediaByType(type)
                    .map { bookmarks ->
                        bookmarks.map { BookmarkUiModel.fromBookmark(it) }
                    }
                    .collect { bookmarkUiModels ->
                        _bookmarkedMedia.value = bookmarkUiModels
                        _isLoading.value = false
                    }
                    
            } catch (e: Exception) {
                _error.value = "북마크된 미디어를 불러오는데 실패했습니다: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    fun refreshBookmarkedMedia() {
        loadBookmarkedMedia()
    }
    
    fun clearError() {
        _error.value = null
    }
    
    // 삭제 모드 관련 함수들
    fun toggleDeleteMode() {
        _isDeleteMode.value = !_isDeleteMode.value
        if (!_isDeleteMode.value) {
            _selectedItems.value = emptySet()
        }
    }
    
    fun toggleItemSelection(itemId: Int) {
        val currentSelected = _selectedItems.value.toMutableSet()
        if (currentSelected.contains(itemId)) {
            currentSelected.remove(itemId)
        } else {
            currentSelected.add(itemId)
        }
        _selectedItems.value = currentSelected
    }
    
    fun selectAllItems() {
        val allIds = _bookmarkedMedia.value.map { it.id }.toSet()
        _selectedItems.value = allIds
    }
    
    fun clearSelection() {
        _selectedItems.value = emptySet()
    }
    
    fun updateFilter(filter: String) {
        val mediaType = when (filter) {
            "전체" -> null
            "이미지" -> MediaType.IMAGE
            "영상" -> MediaType.VIDEO
            else -> null
        }
        _selectedFilter.value = mediaType
        
        when (filter) {
            "전체" -> loadBookmarkedMedia()
            "이미지" -> loadBookmarkedMediaByType(MediaType.IMAGE)
            "영상" -> loadBookmarkedMediaByType(MediaType.VIDEO)
        }
    }
    
    fun clearFilter() {
        _selectedFilter.value = null
        loadBookmarkedMedia()
    }
    
    fun deleteSelectedItems() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                val selectedIds = _selectedItems.value
                if (selectedIds.isNotEmpty()) {
                    // 선택된 아이템들을 삭제
                    selectedIds.forEach { itemId ->
                        deleteBookmarkUseCase(itemId)
                    }
                    
                    // 삭제 후 목록 새로고침
                    loadBookmarkedMedia()
                    
                    // 삭제 모드 종료
                    _isDeleteMode.value = false
                    _selectedItems.value = emptySet()
                    
                    // SnackBar 이벤트 발생
                    _snackBarEvent.emit(
                        SnackBarEvent.Success("${selectedIds.size}개의 아이템이 삭제되었습니다.")
                    )
                }
                
                _isLoading.value = false
                
            } catch (e: Exception) {
                _error.value = "선택된 미디어 삭제에 실패했습니다: ${e.message}"
                _isLoading.value = false
                
                // SnackBar 이벤트 발생
                _snackBarEvent.emit(
                    SnackBarEvent.Error("삭제에 실패했습니다: ${e.message}")
                )
            }
        }
    }
    
    sealed class SnackBarEvent {
        data class Success(val message: String) : SnackBarEvent()
        data class Error(val message: String) : SnackBarEvent()
    }
    
    data class UiState(
        val bookmarkList: List<BookmarkUiModel> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val isDeleteMode: Boolean = false,
        val selectedItems: Set<Int> = emptySet(),
        val selectedFilter: MediaType? = null
    )
}


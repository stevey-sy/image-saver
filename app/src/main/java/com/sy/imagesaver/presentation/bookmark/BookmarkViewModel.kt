package com.sy.imagesaver.presentation.bookmark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sy.imagesaver.domain.usecase.GetBookmarkListUseCase
import com.sy.imagesaver.domain.usecase.DeleteBookmarkUseCase
import com.sy.imagesaver.presentation.model.BookmarkUiModel
import com.sy.imagesaver.presentation.model.mapper.BookmarkUiModelMapper
import com.sy.imagesaver.domain.data.MediaType
import com.sy.imagesaver.presentation.manager.BookmarkManager
import com.sy.imagesaver.presentation.model.BookmarkFilterType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val getBookmarkListUseCase: GetBookmarkListUseCase,
    private val deleteBookmarkUseCase: DeleteBookmarkUseCase,
    private val bookmarkManager: BookmarkManager,
    private val bookmarkUiModelMapper: BookmarkUiModelMapper
) : ViewModel() {

    // SnackBar 메시지를 위한 이벤트
    private val _snackBarEvent = MutableSharedFlow<SnackBarEvent>()
    val snackBarEvent = _snackBarEvent.asSharedFlow()

    // UI 상태를 하나로 통합 - 외부에는 이것만 노출
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadBookmarkedMedia()
    }

    private fun updateUiState(update: (UiState) -> UiState) {
        _uiState.value = update(_uiState.value)
    }

    private fun loadBookmarkedMedia() {
        viewModelScope.launch {
            try {
                updateUiState { it.copy(isLoading = true, error = null) }

                getBookmarkListUseCase.getAllBookmarkedMedia()
                    .map { bookmarks ->
                        bookmarkUiModelMapper.toUiModelList(bookmarks)
                    }
                    .collect { bookmarkUiModels ->
                        updateUiState { 
                            it.copy(
                                bookmarkList = bookmarkUiModels,
                                isLoading = false
                            )
                        }
                    }

            } catch (e: Exception) {
                val errorMessage = "북마크된 미디어를 불러오는데 실패했습니다: ${e.message}"
                updateUiState { 
                    it.copy(
                        error = errorMessage,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun loadBookmarkedMediaByType(type: MediaType) {
        viewModelScope.launch {
            try {
                updateUiState { it.copy(isLoading = true, error = null) }

                getBookmarkListUseCase.getBookmarkedMediaByType(type)
                    .map { bookmarks ->
                        bookmarkUiModelMapper.toUiModelList(bookmarks)
                    }
                    .collect { bookmarkUiModels ->
                        updateUiState { 
                            it.copy(
                                bookmarkList = bookmarkUiModels,
                                isLoading = false
                            )
                        }
                    }

            } catch (e: Exception) {
                val errorMessage = "북마크된 미디어를 불러오는데 실패했습니다: ${e.message}"
                updateUiState { 
                    it.copy(
                        error = errorMessage,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun refreshBookmarkedMedia() {
        loadBookmarkedMedia()
    }

    fun clearError() {
        updateUiState { it.copy(error = null) }
    }

    // 삭제 모드 관련 함수들
    fun toggleDeleteMode() {
        val currentState = _uiState.value
        val newDeleteMode = !currentState.isDeleteMode
        val newSelectedItems = if (!newDeleteMode) emptySet() else currentState.selectedItems
        
        updateUiState { 
            it.copy(
                isDeleteMode = newDeleteMode,
                selectedItems = newSelectedItems
            )
        }
    }

    fun toggleItemSelection(itemId: Int) {
        val currentSelected = _uiState.value.selectedItems.toMutableSet()
        if (currentSelected.contains(itemId)) {
            currentSelected.remove(itemId)
        } else {
            currentSelected.add(itemId)
        }
        updateUiState { it.copy(selectedItems = currentSelected) }
    }

    fun selectAllItems() {
        val allIds = _uiState.value.bookmarkList.map { it.id }.toSet()
        updateUiState { it.copy(selectedItems = allIds) }
    }

    fun clearSelection() {
        updateUiState { it.copy(selectedItems = emptySet()) }
    }

    fun updateFilter(filter: String) {
        val filterType = BookmarkFilterType.fromString(filter)
        updateUiState { it.copy(selectedFilter = filterType) }

        when (filterType) {
            BookmarkFilterType.ALL -> loadBookmarkedMedia()
            BookmarkFilterType.IMAGE -> loadBookmarkedMediaByType(MediaType.IMAGE)
            BookmarkFilterType.VIDEO -> loadBookmarkedMediaByType(MediaType.VIDEO)
        }
    }

    fun updateFilter(filterType: BookmarkFilterType) {
        updateUiState { it.copy(selectedFilter = filterType) }

        when (filterType) {
            BookmarkFilterType.ALL -> loadBookmarkedMedia()
            BookmarkFilterType.IMAGE -> loadBookmarkedMediaByType(MediaType.IMAGE)
            BookmarkFilterType.VIDEO -> loadBookmarkedMediaByType(MediaType.VIDEO)
        }
    }

    fun clearFilter() {
        updateUiState { it.copy(selectedFilter = BookmarkFilterType.ALL) }
        loadBookmarkedMedia()
    }

    fun deleteSelectedItems() {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                updateUiState { it.copy(isLoading = true, error = null) }

                val selectedIds = currentState.selectedItems
                if (selectedIds.isNotEmpty()) {
                    // 선택된 아이템들의 thumbnailUrl을 저장
                    val selectedBookmarks = currentState.bookmarkList.filter { it.id in selectedIds }
                    val thumbnailUrlsToRemove = selectedBookmarks.map { it.thumbnailUrl }

                    // 선택된 아이템들을 삭제
                    selectedIds.forEach { itemId ->
                        deleteBookmarkUseCase(itemId)
                    }

                    // BookmarkManager에서 해당 thumbnailUrl들 제거
                    thumbnailUrlsToRemove.forEach { thumbnailUrl ->
                        bookmarkManager.removeBookmark(thumbnailUrl)
                    }

                    // 삭제 후 목록 새로고침
                    loadBookmarkedMedia()
                    
                    updateUiState { 
                        it.copy(
                            isDeleteMode = false,
                            selectedItems = emptySet()
                        )
                    }

                    // SnackBar 이벤트 발생
                    _snackBarEvent.emit(
                        SnackBarEvent.Success("${selectedIds.size}개의 아이템이 삭제되었습니다.")
                    )
                }

            } catch (e: Exception) {
                val errorMessage = "선택된 미디어 삭제에 실패했습니다: ${e.message}"
                updateUiState { 
                    it.copy(
                        error = errorMessage,
                        isLoading = false
                    )
                }

                // SnackBar 이벤트 발생
                _snackBarEvent.emit(
                    SnackBarEvent.Error("삭제에 실패했습니다: ${e.message}")
                )
            }
        }
    }

    // 이미지 팝업 관련 함수들
    fun showImagePopup(imageUrl: String) {
        updateUiState { 
            it.copy(
                selectedImageUrl = imageUrl,
                showImagePopup = true
            )
        }
    }

    fun hideImagePopup() {
        updateUiState { 
            it.copy(
                showImagePopup = false,
                selectedImageUrl = null
            )
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
        val selectedFilter: BookmarkFilterType = BookmarkFilterType.ALL,
        val showImagePopup: Boolean = false,
        val selectedImageUrl: String? = null,
    )
}


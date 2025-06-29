package com.sy.imagesaver.presentation.bookmark

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sy.imagesaver.domain.data.MediaType
import com.sy.imagesaver.presentation.bookmark.component.BookmarkList
import com.sy.imagesaver.presentation.bookmark.component.DeleteView
import com.sy.imagesaver.presentation.bookmark.component.EmptyView
import com.sy.imagesaver.presentation.bookmark.component.FilterStatusView
import com.sy.imagesaver.presentation.bookmark.component.Header
import com.sy.imagesaver.presentation.common.CircularProgress
import com.sy.imagesaver.presentation.common.ErrorMessageView
import com.sy.imagesaver.presentation.manager.SnackBarManager
import com.sy.imagesaver.presentation.model.BookmarkUiModel

@Composable
fun BookMarkScreen(
    viewModel: BookMarkViewModel,
    snackBarManager: SnackBarManager
) {
    // 상태 수집
    val uiState = collectUiState(viewModel)
    
    // SnackBar 이벤트 구독
    LaunchedEffect(Unit) {
        subscribeToSnackBarEvents(viewModel, snackBarManager)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 헤더
        Header(uiState.bookmarkList.size)
        
        // 삭제 모드 UI
        DeleteView(
            isDeleteMode = uiState.isDeleteMode,
            selectedItems = uiState.selectedItems,
            bookmarkList = uiState.bookmarkList,
            viewModel = viewModel
        )

        // 필터 상태 UI
        FilterStatusView(
            filterType = uiState.selectedFilter
        ) {
            viewModel.clearFilter()
        }

        // 메인 콘텐츠
        BookMarkContent(
            uiState = uiState,
            viewModel = viewModel
        )
    }
}

@Composable
private fun BookMarkContent(
    uiState: BookMarkUiState,
    viewModel: BookMarkViewModel
) {
    when {
        uiState.isLoading -> {
            CircularProgress()
        }
        uiState.error != null -> {
            ErrorMessageView(
                errorMessage = uiState.error,
                onRetry = { viewModel.refreshBookmarkedMedia() }
            )
        }
        uiState.bookmarkList.isEmpty() -> {
            EmptyView()
        }
        else -> {
            BookmarkList(
                bookmarkList = uiState.bookmarkList,
                isDeleteMode = uiState.isDeleteMode,
                selectedItems = uiState.selectedItems,
                viewModel = viewModel,
            )
        }
    }
}

@Composable
private fun collectUiState(viewModel: BookMarkViewModel): BookMarkUiState {
    return BookMarkUiState(
        bookmarkList = viewModel.bookmarkList.collectAsState().value,
        isLoading = viewModel.isLoading.collectAsState().value,
        error = viewModel.error.collectAsState().value,
        isDeleteMode = viewModel.isDeleteMode.collectAsState().value,
        selectedItems = viewModel.selectedItems.collectAsState().value,
        selectedFilter = viewModel.selectedFilter.collectAsState().value
    )
}

private suspend fun subscribeToSnackBarEvents(
    viewModel: BookMarkViewModel,
    snackBarManager: SnackBarManager
) {
    viewModel.snackBarEvent.collect { event ->
        when (event) {
            is BookMarkViewModel.SnackBarEvent.Success -> {
                snackBarManager.showSuccessSnackbar(event.message)
            }
            is BookMarkViewModel.SnackBarEvent.Error -> {
                snackBarManager.showErrorSnackbar(event.message)
            }
        }
    }
}

private data class BookMarkUiState(
    val bookmarkList: List<BookmarkUiModel>,
    val isLoading: Boolean,
    val error: String?,
    val isDeleteMode: Boolean,
    val selectedItems: Set<Int>,
    val selectedFilter: MediaType?
)




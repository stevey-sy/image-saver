package com.sy.imagesaver.presentation.bookmark

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.activity.compose.BackHandler
import com.sy.imagesaver.presentation.bookmark.component.BookmarkList
import com.sy.imagesaver.presentation.bookmark.component.DeleteView
import com.sy.imagesaver.presentation.bookmark.component.EmptyView
import com.sy.imagesaver.presentation.bookmark.component.FilterStatusView
import com.sy.imagesaver.presentation.bookmark.component.Header
import com.sy.imagesaver.presentation.bookmark.component.ImagePopup
import com.sy.imagesaver.presentation.common.CircularProgress
import com.sy.imagesaver.presentation.common.ErrorMessageView
import com.sy.imagesaver.presentation.manager.SnackBarManager

@Composable
fun BookMarkScreen(
    viewModel: BookmarkViewModel,
    snackBarManager: SnackBarManager
) {
    // ViewModel의 uiState 사용
    val uiState = viewModel.uiState.collectAsState().value
    
    // SnackBar 이벤트 구독
    LaunchedEffect(Unit) {
        subscribeToSnackBarEvents(viewModel, snackBarManager)
    }

    // 삭제 모드일 때 뒤로가기 버튼 처리
    BackHandler(enabled = uiState.isDeleteMode) {
        viewModel.toggleDeleteMode()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .animateContentSize(
                animationSpec = tween(durationMillis = 200) // 빠른 애니메이션
            )
    ) {
        // 헤더
        Header(uiState.bookmarkList.size)
        
        // 삭제 모드 UI - DeleteView 내부 AnimatedVisibility가 애니메이션 처리
        DeleteView(
            isDeleteMode = uiState.isDeleteMode,
            selectedItems = uiState.selectedItems,
            bookmarkList = uiState.bookmarkList,
            viewModel = viewModel,
        )

        // 필터 상태 UI
        FilterStatusView(
            filterType = uiState.selectedFilter
        ) {
            viewModel.clearFilter()
        }

        BookMarkContent(
            uiState = uiState,
            viewModel = viewModel
        )
    }
    
    // 이미지 팝업 (이미지 또는 비디오 썸네일)
    if (uiState.showImagePopup && (uiState.selectedImageUrl != null)) {
        ImagePopup(
            imageUrl = uiState.selectedImageUrl,
            onDismiss = { viewModel.hideImagePopup() }
        )
    }
}

@Composable
private fun BookMarkContent(
    uiState: BookmarkViewModel.UiState,
    viewModel: BookmarkViewModel
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

private suspend fun subscribeToSnackBarEvents(
    viewModel: BookmarkViewModel,
    snackBarManager: SnackBarManager
) {
    viewModel.snackBarEvent.collect { event ->
        when (event) {
            is BookmarkViewModel.SnackBarEvent.Success -> {
                snackBarManager.showSuccessSnackbar(event.message)
            }
            is BookmarkViewModel.SnackBarEvent.Error -> {
                snackBarManager.showErrorSnackbar(event.message)
            }
        }
    }
}




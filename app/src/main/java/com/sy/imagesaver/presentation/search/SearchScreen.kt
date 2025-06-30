package com.sy.imagesaver.presentation.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.CombinedLoadStates
import com.sy.imagesaver.presentation.manager.SnackBarManager
import com.sy.imagesaver.presentation.common.CircularProgress
import com.sy.imagesaver.presentation.common.ErrorMessageView
import com.sy.imagesaver.presentation.search.component.SearchResultList
import com.sy.imagesaver.presentation.search.component.SearchTextArea
import kotlinx.coroutines.FlowPreview

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel()
) {
    // ViewModel의 uiState 사용
    val uiState = viewModel.uiState.collectAsState().value
    val focusManager = LocalFocusManager.current

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // 검색 입력 영역
            SearchTextArea(
                searchQuery = uiState.searchQuery,
                viewModel = viewModel,
                focusManager = focusManager,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 에러 메시지 표시
            uiState.error?.let { errorMessage ->
                ErrorMessageView(
                    errorMessage = errorMessage,
                    onRetry = { viewModel.retrySearch() }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // 검색 결과 영역
            SearchContent(
                uiState = uiState,
                viewModel = viewModel
            )
        }
    }
}

@Composable
private fun SearchContent(
    uiState: SearchViewModel.UiState,
    viewModel: SearchViewModel
) {
    when {
        // 검색 대기 상태
        uiState.isSearching && uiState.searchQuery.isNotBlank() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                CircularProgress(
                    isLoading = true,
                    alignment = Alignment.Center
                )
            }
        }
        // 검색 결과 표시
        uiState.debouncedSearchQuery.isNotBlank() -> {
            SearchResultContent(
                debouncedSearchQuery = uiState.debouncedSearchQuery,
                bookmarkedThumbnailUrls = uiState.bookmarkedThumbnailUrls,
                viewModel = viewModel
            )
        }
    }
}

@Composable
private fun SearchResultContent(
    debouncedSearchQuery: String,
    bookmarkedThumbnailUrls: Set<String>,
    viewModel: SearchViewModel
) {
    val pagingFlow = remember(debouncedSearchQuery) {
        viewModel.getSearchResultFlow(debouncedSearchQuery)
    }
    val lazyPagingItems = pagingFlow.collectAsLazyPagingItems()
    val gridState = rememberLazyGridState()

    // 검색어가 변경될 때 스크롤 위치 초기화
    LaunchedEffect(debouncedSearchQuery) {
        gridState.animateScrollToItem(0)
    }

    // 에러 상태 처리
    LaunchedEffect(lazyPagingItems.loadState) {
        handleLoadStateChanges(lazyPagingItems.loadState, viewModel)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        SearchResultList(
            gridState = gridState,
            lazyPagingItems = lazyPagingItems,
            bookmarkedThumbnailUrls = bookmarkedThumbnailUrls,
            viewModel = viewModel
        )

        // 로딩 상태 표시
        SearchLoadingIndicators(lazyPagingItems.loadState)
    }
}

@Composable
private fun SearchLoadingIndicators(loadState: CombinedLoadStates) {
    val isRefreshing = loadState.refresh is LoadState.Loading
    val isAppending = loadState.append is LoadState.Loading

    // 초기 로딩(refresh) 상태일 때만 중앙에 인디케이터 표시
    CircularProgress(
        isLoading = isRefreshing,
        alignment = Alignment.Center
    )
    
    // 추가 로딩(append) 상태일 때 하단에 인디케이터 표시
    CircularProgress(
        isLoading = isAppending,
        alignment = Alignment.BottomCenter
    )
}

private suspend fun subscribeToSnackBarEvents(
    viewModel: SearchViewModel,
    snackBarManager: SnackBarManager
) {
    viewModel.snackBarEvent.collect { event ->
        when (event) {
            is SearchViewModel.SnackBarEvent.Success -> {
                snackBarManager.showSuccessSnackbar(event.message)
                    }
            is SearchViewModel.SnackBarEvent.Error -> {
                snackBarManager.showErrorSnackbar(event.message)
            }
        }
    }
}

private suspend fun handleLoadStateChanges(
    loadState: CombinedLoadStates,
    viewModel: SearchViewModel
) {
    when (loadState.refresh) {
        is LoadState.Error -> {
            val errorState = loadState.refresh as LoadState.Error
            viewModel.handleLoadStateError(errorState.error)
        }
        is LoadState.NotLoading -> {
            viewModel.clearError()
        }
        else -> {}
                }
            }







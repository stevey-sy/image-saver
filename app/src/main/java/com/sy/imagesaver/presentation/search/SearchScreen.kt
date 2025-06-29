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
import com.sy.imagesaver.presentation.manager.SnackBarManager
import com.sy.imagesaver.presentation.search.component.CircularProgress
import com.sy.imagesaver.presentation.search.component.ErrorMessageView
import com.sy.imagesaver.presentation.search.component.SearchResultList
import com.sy.imagesaver.presentation.search.component.SearchTextArea
import kotlinx.coroutines.FlowPreview

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    snackBarManager: SnackBarManager
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val debouncedSearchQuery by viewModel.debouncedSearchQuery.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val error by viewModel.error.collectAsState()
    val bookmarkedThumbnailUrls by viewModel.bookmarkedThumbnailUrls.collectAsState()
    val focusManager = LocalFocusManager.current

    // SnackBar 이벤트 구독
    LaunchedEffect(Unit) {
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

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            SearchTextArea(
                searchQuery,
                viewModel,
                focusManager
            )

            Spacer(modifier = Modifier.height(16.dp))

            error?.let { errorMessage ->
                ErrorMessageView(
                    errorMessage = errorMessage,
                    onRetry = { viewModel.retrySearch() }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // 검색 대기 상태 표시
            if (isSearching && searchQuery.isNotBlank()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgress(
                        isLoading = true,
                        alignment = Alignment.Center
                    )
                }
            } else if (debouncedSearchQuery.isNotBlank()) {
                val pagingFlow = remember(debouncedSearchQuery) {
                    viewModel.getSearchResultFlow(debouncedSearchQuery)
                }
                val lazyPagingItems = pagingFlow.collectAsLazyPagingItems()
                val isAppending = lazyPagingItems.loadState.append is LoadState.Loading
                val isRefreshing = lazyPagingItems.loadState.refresh is LoadState.Loading
                val gridState = rememberLazyGridState()

                // 검색어가 변경될 때 스크롤 위치 초기화
                LaunchedEffect(debouncedSearchQuery) {
                    gridState.animateScrollToItem(0)
                }

                // 에러 상태 처리
                LaunchedEffect(lazyPagingItems.loadState) {
                    when (lazyPagingItems.loadState.refresh) {
                        is LoadState.Error -> {
                            val errorState = lazyPagingItems.loadState.refresh as LoadState.Error
                            viewModel.handleLoadStateError(errorState.error)
                        }
                        is LoadState.NotLoading -> {
                            viewModel.clearError()
                        }
                        else -> {}
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    SearchResultList(gridState, lazyPagingItems, bookmarkedThumbnailUrls, viewModel)

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
            }
        }
    }
}







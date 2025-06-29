package com.sy.imagesaver.presentation.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import com.sy.imagesaver.presentation.manager.SnackBarManager
import com.sy.imagesaver.presentation.search.component.SearchResultCard
import kotlinx.coroutines.FlowPreview

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    snackBarManager: SnackBarManager
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val error by viewModel.error.collectAsState()
    val bookmarkedThumbnailUrls by viewModel.bookmarkedThumbnailUrls.collectAsState()
    val focusManager = LocalFocusManager.current

    // SnackBar 이벤트 구독
    LaunchedEffect(Unit) {
        viewModel.snackBarEvent.collect { event ->
            println("SearchScreen: SnackBar 이벤트 수신: $event")
            when (event) {
                is SearchViewModel.SnackBarEvent.Success -> {
                    println("SearchScreen: Success SnackBar 표시 시도: ${event.message}")
                    snackBarManager.showSuccessSnackbar(event.message)
                }
                is SearchViewModel.SnackBarEvent.Error -> {
                    println("SearchScreen: Error SnackBar 표시 시도: ${event.message}")
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
            // 기존 SearchBar
            SearchTextArea(
                searchQuery,
                viewModel,
                focusManager
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (searchQuery.isNotBlank()) {
                val pagingFlow = remember(searchQuery) {
                    viewModel.getSearchResultFlow(searchQuery)
                }
                val lazyPagingItems = pagingFlow.collectAsLazyPagingItems()
                val isRefreshing = lazyPagingItems.loadState.refresh is androidx.paging.LoadState.Loading
                val gridState = rememberLazyGridState()

                // 검색어가 변경될 때 스크롤 위치 초기화
                LaunchedEffect(searchQuery) {
                    gridState.animateScrollToItem(0)
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(0.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        state = gridState
                    ) {
                        items(
                            count = lazyPagingItems.itemCount,
                            key = lazyPagingItems.itemKey { it.id }
                        ) { index ->
                            lazyPagingItems[index]?.let { mediaItem ->
                                SearchResultCard(
                                    media = mediaItem,
                                    isBookmarked = bookmarkedThumbnailUrls.contains(mediaItem.thumbnailUrl),
                                    showBookmarkIcon = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    onItemClick = {
                                        viewModel.saveMedia(mediaItem)
                                    }
                                )
                            }
                        }
                    }
                    
                    // Paging의 추가 로딩(append) 상태일 때 중앙에 인디케이터
                    if (lazyPagingItems.loadState.append is androidx.paging.LoadState.Loading) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    
                    // refresh(최초/새로고침) 로딩 시 중앙에 인디케이터 오버레이
                    if (isRefreshing) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }

            // 에러 상태
            error?.let { errorMessage ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = errorMessage,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchTextArea(
    query: String,
    viewModel: SearchViewModel,
    focusManager: FocusManager
) {
    TextField(
        value = query,
        onValueChange = {
            viewModel.updateSearchQuery(it)
        },
        modifier = Modifier
            .fillMaxWidth(),
        placeholder = { Text("검색어를 입력해주세요") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "검색"
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = {
                    viewModel.updateSearchQuery("")
                }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "지우기"
                    )
                }
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                focusManager.clearFocus()
            }
        ),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}




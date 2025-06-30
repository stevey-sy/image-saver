package com.sy.imagesaver.presentation.search.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.sy.imagesaver.presentation.model.SearchResultUiModel
import com.sy.imagesaver.presentation.search.SearchViewModel

@Composable
fun SearchResultList(
    gridState: LazyGridState,
    lazyPagingItems: LazyPagingItems<SearchResultUiModel>,
    bookmarkedThumbnailUrls: Set<String>,
    viewModel: SearchViewModel,
) {
    val focusManager = LocalFocusManager.current
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        // 검색 결과 영역 터치 시 포커스 해제
                        focusManager.clearFocus()
                    }
                )
            },
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
                        viewModel.addBookmark(mediaItem)
                    }
                )
            }
        }
    }
}
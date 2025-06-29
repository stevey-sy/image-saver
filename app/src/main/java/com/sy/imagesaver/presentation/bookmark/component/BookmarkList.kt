package com.sy.imagesaver.presentation.bookmark.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sy.imagesaver.presentation.bookmark.BookMarkViewModel
import com.sy.imagesaver.presentation.model.BookmarkUiModel

@Composable
fun BookmarkList(
    bookmarkList: List<BookmarkUiModel>,
    isDeleteMode: Boolean,
    selectedItems: Set<Int>,
    viewModel: BookMarkViewModel
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(0.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(bookmarkList) { bookmarkItem ->
            BookmarkCard(
                bookmark = bookmarkItem,
                isDeleteMode = isDeleteMode,
                isSelected = selectedItems.contains(bookmarkItem.id),
                modifier = Modifier.fillMaxWidth(),
                onSelectionChanged = {
                    viewModel.toggleItemSelection(bookmarkItem.id)
                }
            )
        }
    }
}
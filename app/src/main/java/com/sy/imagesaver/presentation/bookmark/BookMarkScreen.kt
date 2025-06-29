package com.sy.imagesaver.presentation.bookmark

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sy.imagesaver.presentation.bookmark.component.BookmarkCard
import com.sy.imagesaver.presentation.theme.AppIcons
import com.sy.imagesaver.presentation.theme.Orange
import com.sy.imagesaver.presentation.manager.SnackBarManager

@Composable
fun BookMarkScreen(
    viewModel: BookMarkViewModel,
    snackBarManager: SnackBarManager
) {
    val bookmarkedMedia by viewModel.bookmarkedMedia.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isDeleteMode by viewModel.isDeleteMode.collectAsState()
    val selectedItems by viewModel.selectedItems.collectAsState()

    // SnackBar 이벤트 구독
    LaunchedEffect(Unit) {
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 헤더
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Icon(
                painter = AppIcons.BookmarkFilled,
                contentDescription = "북마크",
                tint = Orange,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "나의 보관함",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Total: ${bookmarkedMedia.size}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // 삭제 모드일 때 선택된 아이템 개수와 액션 버튼들 표시
        AnimatedVisibility(
            visible = isDeleteMode,
            enter = slideInVertically(
                initialOffsetY = { -it },
                animationSpec = tween(durationMillis = 300)
            ) + fadeIn(
                animationSpec = tween(durationMillis = 300)
            ),
            exit = slideOutVertically(
                targetOffsetY = { -it },
                animationSpec = tween(durationMillis = 300)
            ) + fadeOut(
                animationSpec = tween(durationMillis = 300)
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = "${selectedItems.size}개 선택됨",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.weight(1f))
//                TextButton(
//                    onClick = { viewModel.selectAllItems() }
//                ) {
//                    Text("All")
//                }
//                TextButton(
//                    onClick = { viewModel.clearSelection() }
//                ) {
//                    Text("선택 해제")
//                }
                TextButton(
                    onClick = { viewModel.toggleDeleteMode() }
                ) {
                    Text("취소")
                }
                Button(
                    onClick = { viewModel.deleteSelectedItems() },
                    enabled = selectedItems.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Orange,
                        contentColor = Color.Black
                    )
                ) {
                    Text("삭제")
                }
            }
        }

        // 로딩 상태
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        // 에러 상태
        else if (error != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = error!!,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { viewModel.refreshBookmarkedMedia() }
                    ) {
                        Text("다시 시도")
                    }
                }
            }
        }
        // 빈 상태
        else if (bookmarkedMedia.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = AppIcons.Bookmark,
                        contentDescription = "북마크",
                        modifier = Modifier.size(64.dp),
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "저장된 미디어가 없습니다",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "검색 화면에서 미디어를 저장해보세요",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        // 미디어 리스트
        else {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                verticalItemSpacing = 8.dp,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                items(bookmarkedMedia) { bookmarkItem ->
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
    }
}


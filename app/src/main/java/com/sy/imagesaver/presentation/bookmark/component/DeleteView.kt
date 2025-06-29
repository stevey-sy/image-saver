package com.sy.imagesaver.presentation.bookmark.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sy.imagesaver.R
import com.sy.imagesaver.presentation.bookmark.BookMarkViewModel
import com.sy.imagesaver.presentation.model.BookmarkUiModel
import com.sy.imagesaver.presentation.theme.Orange

@Composable
fun ColumnScope.DeleteView(
    isDeleteMode: Boolean,
    selectedItems: Set<Int>,
    bookmarkList: List<BookmarkUiModel>,
    viewModel: BookMarkViewModel
) {
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
            // 전체 선택 체크박스
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Checkbox(
                    checked = selectedItems.size == bookmarkList.size && bookmarkList.isNotEmpty(),
                    onCheckedChange = { isChecked ->
                        if (isChecked) {
                            viewModel.selectAllItems()
                        } else {
                            viewModel.clearSelection()
                        }
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Orange,
                        uncheckedColor = Color.Gray
                    )
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.select_all),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Text(
                text = stringResource(R.string.selected_count_format, selectedItems.size),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            TextButton(
                onClick = { viewModel.toggleDeleteMode() }
            ) {
                Text(stringResource(R.string.cancel))
            }
            Button(
                onClick = { viewModel.deleteSelectedItems() },
                enabled = selectedItems.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Orange,
                    contentColor = Color.Black
                )
            ) {
                Text(stringResource(R.string.delete))
            }
        }
    }
}
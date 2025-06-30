package com.sy.imagesaver.presentation.bookmark.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sy.imagesaver.presentation.model.BookmarkFilterType

@Composable
fun FilterStatusView(
    filterType: BookmarkFilterType,
    onClearFilter: () -> Unit
) {
    AnimatedVisibility(
        visible = filterType != BookmarkFilterType.ALL,
        enter = slideInVertically(
            animationSpec = tween(300),
            initialOffsetY = { -it }
        ) + fadeIn(
            animationSpec = tween(300)
        ),
        exit = slideOutVertically(
            animationSpec = tween(300),
            targetOffsetY = { -it }
        ) + fadeOut(
            animationSpec = tween(300)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .background(
                    color = Color.Gray,
                    shape = MaterialTheme.shapes.small
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when (filterType) {
                        BookmarkFilterType.IMAGE -> "이미지만 보여요."
                        BookmarkFilterType.VIDEO -> "영상만 보여요."
                        BookmarkFilterType.ALL -> ""
                    },
                    style = typography.bodyMedium,
                    color = Color.White
                )
                IconButton(
                    onClick = onClearFilter,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "필터 해제",
                        tint = Color.White
                    )
                }
            }
        }
    }
}
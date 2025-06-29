package com.sy.imagesaver.presentation.bookmark.component

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
import com.sy.imagesaver.domain.data.MediaType

@Composable
fun FilterStatusView(
    filterType: MediaType?,
    onClearFilter: () -> Unit
) {
    // 필터가 선택되지 않았으면 표시하지 않음
    if (filterType == null) {
        return
    }
    
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
                    MediaType.IMAGE -> "이미지만 보여요."
                    MediaType.VIDEO -> "영상만 보여요."
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
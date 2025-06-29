package com.sy.imagesaver.presentation.bookmark.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.sy.imagesaver.presentation.model.BookmarkUiModel
import com.sy.imagesaver.presentation.theme.AppIcons
import com.sy.imagesaver.presentation.theme.Orange

@Composable
fun BookmarkCard(
    bookmark: BookmarkUiModel,
    modifier: Modifier = Modifier,
    isDeleteMode: Boolean = false,
    isSelected: Boolean = false,
    onItemClick: () -> Unit = {},
    onSelectionChanged: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .height(200.dp)
            .clickable { 
                if (isDeleteMode) {
                    onSelectionChanged()
                } else {
                    onItemClick()
                }
            },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // 미디어 컨텐츠 - 고정 높이로 설정
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // 남은 공간을 모두 차지
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(bookmark.thumbnailUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "북마크 썸네일",
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                // 삭제 모드일 때 체크박스 표시
                if (isDeleteMode) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(24.dp)
                            .background(
                                color = if (isSelected) Orange else Color.White.copy(alpha = 0.8f),
                                shape = RoundedCornerShape(4.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "선택됨",
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // 북마크 정보 - 고정 높이
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.DarkGray)
                    .padding(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (bookmark.type == "이미지") AppIcons.ImageType else AppIcons.VideoType,
                        contentDescription = bookmark.type,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = bookmark.datetime,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
} 
package com.sy.imagesaver.presentation.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.sy.imagesaver.domain.data.Media
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    videoPlayerManager: VideoPlayerManager
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResult by viewModel.searchResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val focusManager = LocalFocusManager.current
    
    val currentPlayingVideoId by videoPlayerManager.currentPlayingVideoId.collectAsState()

    // 검색어 입력 후 0.5초 후 자동 검색
    var isFirstLaunch by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        snapshotFlow { searchQuery }
            .debounce(500)
            .distinctUntilChanged()
            .collectLatest { searchText ->
                if (searchText.isNotBlank() && !isFirstLaunch) {
                    viewModel.searchMedia(searchText)
                }
                isFirstLaunch = false
            }
    }

    // 검색 결과가 변경될 때 비디오 아이템 등록
    LaunchedEffect(searchResult) {
        videoPlayerManager.clearVideoItems()
        searchResult?.documents?.forEachIndexed { index, media ->
            if (media is Media.Video) {
                videoPlayerManager.addVideoItem(media.id, media.originalUrl, index)
            }
        }
    }

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

        // 검색 결과 - Pinterest 스타일 2열 그리드
        searchResult?.let { result ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(result.documents) { index, media ->
                    MediaCard(
                        media = media,
                        isPlaying = currentPlayingVideoId == media.id,
                        videoPlayerManager = videoPlayerManager,
                        modifier = Modifier.fillMaxWidth()
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

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
private fun MediaCard(
    media: Media,
    isPlaying: Boolean,
    videoPlayerManager: VideoPlayerManager,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // 미디어 컨텐츠 - Pinterest 스타일로 높이 자동 조정
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            ) {
                when (media) {
                    is Media.Image -> {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(media.thumbnailUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "이미지",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                    is Media.Video -> {
                        VideoPreview(
                            videoUrl = media.originalUrl,
                            isPlaying = isPlaying,
                            videoPlayerManager = videoPlayerManager,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp))
                        )
                    }
                }
            }

            // 미디어 정보
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                when (media) {
                    is Media.Image -> {
                        Text(
                            text = "이미지",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    is Media.Video -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "재생",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "비디오 • ${media.playTime}초",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = media.id,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@UnstableApi
@Composable
private fun VideoPreview(
    videoUrl: String,
    isPlaying: Boolean,
    videoPlayerManager: VideoPlayerManager,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        // ExoPlayer를 사용한 비디오 미리보기
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = videoPlayerManager.initializePlayer()
                    useController = false // 컨트롤러 숨김
                    setShowBuffering(PlayerView.SHOW_BUFFERING_NEVER)
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // 재생 버튼 오버레이 (재생 중이 아닐 때만 표시)
        if (!isPlaying) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                    ),
                    shape = RoundedCornerShape(50)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "재생",
                        modifier = Modifier
                            .size(32.dp)
                            .padding(8.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}




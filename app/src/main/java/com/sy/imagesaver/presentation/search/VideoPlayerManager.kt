package com.sy.imagesaver.presentation.search

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoPlayerManager @Inject constructor(
    private val context: Context
) {
    private var exoPlayer: ExoPlayer? = null
    private val _currentPlayingVideoId = MutableStateFlow<String?>(null)
    val currentPlayingVideoId: StateFlow<String?> = _currentPlayingVideoId.asStateFlow()
    
    private var isScrolling = false
    private val videoItems = mutableMapOf<String, VideoItem>()
    
    @UnstableApi
    fun initializePlayer(): ExoPlayer {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(context).build().apply {
                repeatMode = Player.REPEAT_MODE_ONE
                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        if (playbackState == Player.STATE_READY && !isScrolling) {
                            play()
                        }
                    }
                })
            }
        }
        return exoPlayer!!
    }
    
    @UnstableApi
    fun playVideo(videoId: String, videoUrl: String) {
        if (_currentPlayingVideoId.value == videoId) return
        
        val player = initializePlayer()
        player.setMediaItem(MediaItem.fromUri(videoUrl))
        player.prepare()
        _currentPlayingVideoId.value = videoId
    }
    
    fun stopVideo() {
        exoPlayer?.stop()
        _currentPlayingVideoId.value = null
    }
    
    fun pauseVideo() {
        exoPlayer?.pause()
    }
    
    fun resumeVideo() {
        if (!isScrolling) {
            exoPlayer?.play()
        }
    }
    
    fun setScrolling(scrolling: Boolean) {
        isScrolling = scrolling
        if (scrolling) {
            pauseVideo()
        } else {
            resumeVideo()
        }
    }
    
    fun addVideoItem(videoId: String, videoUrl: String, position: Int) {
        videoItems[videoId] = VideoItem(videoId, videoUrl, position)
    }
    
    fun findClosestVideoItem(currentPosition: Int): String? {
        if (videoItems.isEmpty()) return null
        
        return videoItems.values
            .minByOrNull { kotlin.math.abs(it.position - currentPosition) }
            ?.videoId
    }
    
    fun clearVideoItems() {
        videoItems.clear()
    }
    
    fun release() {
        exoPlayer?.release()
        exoPlayer = null
        _currentPlayingVideoId.value = null
        videoItems.clear()
    }
    
    data class VideoItem(
        val videoId: String,
        val videoUrl: String,
        val position: Int
    )
} 
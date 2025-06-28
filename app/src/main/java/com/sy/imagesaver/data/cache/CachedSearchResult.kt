package com.sy.imagesaver.data.cache

import androidx.paging.PagingData
import com.sy.imagesaver.presentation.model.MediaUiModel
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class CachedSearchResult @OptIn(ExperimentalTime::class) constructor(
    val query: String,
    val mediaList: List<MediaUiModel> = emptyList(),
    val cachedAt: Instant = Clock.System.now()
) {
    companion object {
        private const val CACHE_DURATION_MINUTES = 5L
    }
    
    @OptIn(ExperimentalTime::class)
    fun isExpired(): Boolean {
        val currentTime = Clock.System.now()
        val expirationTime = cachedAt.plus(
            Duration.parse("PT${CACHE_DURATION_MINUTES}M")
        )
        return currentTime >= expirationTime
    }
    
    @OptIn(ExperimentalTime::class)
    fun getRemainingTimeMinutes(): Long {
        val currentTime = Clock.System.now()
        val expirationTime = cachedAt.plus(
            Duration.parse("PT${CACHE_DURATION_MINUTES}M")
        )
        val remainingDuration = expirationTime - currentTime
        return remainingDuration.inWholeMinutes.coerceAtLeast(0)
    }
} 
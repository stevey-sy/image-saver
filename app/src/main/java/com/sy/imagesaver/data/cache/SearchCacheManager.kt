package com.sy.imagesaver.data.cache

import com.sy.imagesaver.presentation.model.MediaUiModel
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.ExperimentalTime

@Singleton
class SearchCacheManager @Inject constructor() {
    
    private val cache = mutableMapOf<String, CachedSearchResult>()
    
    fun isCacheValid(query: String): Boolean {
        val cachedResult = cache[query]
        return cachedResult != null && !cachedResult.isExpired()
    }
    
    fun getCachedMediaList(query: String): List<MediaUiModel>? {
        val cachedResult = cache[query]
        return if (cachedResult != null && !cachedResult.isExpired()) {
            cachedResult.mediaList
        } else {
            // 만료된 캐시 제거
            if (cachedResult != null) {
                cache.remove(query)
            }
            null
        }
    }
    
    @OptIn(ExperimentalTime::class)
    fun cacheMediaList(query: String, mediaList: List<MediaUiModel>) {
        val cachedResult = CachedSearchResult(
            query = query,
            mediaList = mediaList
        )
        cache[query] = cachedResult
        
        // 캐시 크기 제한 (메모리 관리)
        if (cache.size > MAX_CACHE_SIZE) {
            removeOldestCache()
        }
    }
    
    fun getRemainingTime(query: String): Long {
        val cachedResult = cache[query]
        return cachedResult?.getRemainingTimeMinutes() ?: 0L
    }
    
    fun clearCache() {
        cache.clear()
    }
    
    fun removeExpiredCache() {
        val expiredQueries = cache.filterValues { it.isExpired() }.keys
        expiredQueries.forEach { cache.remove(it) }
    }
    
    @OptIn(ExperimentalTime::class)
    private fun removeOldestCache() {
        if (cache.isEmpty()) return
        
        val oldestQuery = cache.minByOrNull { it.value.cachedAt }?.key
        oldestQuery?.let { cache.remove(it) }
    }
    
    fun getCacheSize(): Int = cache.size
    
    fun getCacheInfo(): Map<String, Long> {
        return cache.mapValues { (_, cachedResult) ->
            cachedResult.getRemainingTimeMinutes()
        }
    }
    
    companion object {
        private const val MAX_CACHE_SIZE = 20 // 최대 캐시 개수 제한
    }
} 
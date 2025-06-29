package com.sy.imagesaver.data.mapper

import com.sy.imagesaver.data.remote.dto.ImageDto
import com.sy.imagesaver.data.remote.dto.VideoDto
import com.sy.imagesaver.domain.data.SearchResult
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import java.security.MessageDigest
import javax.inject.Inject

class MediaDtoMapper @Inject constructor() {
    @OptIn(ExperimentalTime::class)
    fun fromImageDto(dto: ImageDto): SearchResult {
        // 안정적인 unique id 생성: thumbnailUrl + datetime 조합
        val uniqueId = "${dto.thumbnailUrl}_${dto.datetime}"
        
        return SearchResult.Image(
            id = uniqueId,
            thumbnailUrl = dto.thumbnailUrl,
            originalUrl = dto.imageUrl,
            datetime = Instant.parse(dto.datetime)
        )
    }

    @OptIn(ExperimentalTime::class)
    fun fromVideoDto(dto: VideoDto): SearchResult {
        // 안정적인 unique id 생성: thumbnail + datetime 조합
//        val uniqueId = generateStableId("${dto.thumbnail}_${dto.datetime}")
        val uniqueId = "${dto.thumbnail}_${dto.datetime}"
        
        return SearchResult.Video(
            id = uniqueId,
            thumbnailUrl = dto.thumbnail,
            originalUrl = dto.url,
            datetime = Instant.parse(dto.datetime),
            title = dto.title,
            playTime = dto.playTime
        )
    }
    
    private fun generateStableId(input: String): String {
        // SHA-256 해시를 사용하여 안정적이고 unique한 ID 생성
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }.take(16) // 16자리로 제한
    }
}
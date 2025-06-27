package com.sy.imagesaver.data.mapper

import com.sy.imagesaver.data.remote.dto.ImageDto
import com.sy.imagesaver.data.remote.dto.VideoDto
import com.sy.imagesaver.domain.data.Media
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import javax.inject.Inject

class MediaDtoMapper @Inject constructor() {
    @OptIn(ExperimentalTime::class)
    fun fromImageDto(dto: ImageDto): Media =
        Media.Image(
            id = dto.docUrl,
            thumbnailUrl = dto.thumbnailUrl,
            originalUrl = dto.imageUrl,
            datetime = Instant.parse(dto.datetime)
        )


    @OptIn(ExperimentalTime::class)
    fun fromVideoDto(dto: VideoDto): Media =
        Media.Video(
            id = dto.url,
            thumbnailUrl = dto.thumbnail,
            originalUrl = dto.url,
            datetime = Instant.parse(dto.datetime),
            title = dto.title,
            playTime = dto.playTime
        )
}
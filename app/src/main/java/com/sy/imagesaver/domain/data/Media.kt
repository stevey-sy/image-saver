package com.sy.imagesaver.domain.data

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
sealed class Media {
    abstract val id: String
    abstract val thumbnailUrl: String
    abstract val originalUrl: String
    abstract val datetime: Instant

    data class Image(
        override val id: String,
        override val thumbnailUrl: String,
        override val originalUrl: String,
        override val datetime: Instant
    ) : Media()

    data class Video(
        override val id: String,
        override val thumbnailUrl: String,
        override val originalUrl: String,
        override val datetime: Instant,
        val title: String,
        val playTime: Int
    ) : Media()
}
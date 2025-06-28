package com.sy.imagesaver.presentation.model

import com.sy.imagesaver.domain.data.Media
import com.sy.imagesaver.util.formatYmdHm
import com.sy.imagesaver.util.toSeoulLocalDateTime
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.ZoneId
import kotlin.time.ExperimentalTime
import java.time.format.DateTimeFormatter
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
sealed class MediaUiModel {
    abstract val id: String
    abstract val thumbnailUrl: String
    abstract val originalUrl: String
    abstract val datetime: String

    data class Image(
        override val id: String,
        override val thumbnailUrl: String,
        override val originalUrl: String,
        override val datetime: String
    ) : MediaUiModel()

    data class Video(
        override val id: String,
        override val thumbnailUrl: String,
        override val originalUrl: String,
        override val datetime: String,
        val title: String,
        val playTime: Int
    ) : MediaUiModel()

    companion object {
        fun fromMedia(media: Media): MediaUiModel {
            val formattedDateTime = formatDateTime(media.datetime)
            
            return when (media) {
                is Media.Image -> Image(
                    id = media.id,
                    thumbnailUrl = media.thumbnailUrl,
                    originalUrl = media.originalUrl,
                    datetime = formattedDateTime
                )
                is Media.Video -> Video(
                    id = media.id,
                    thumbnailUrl = media.thumbnailUrl,
                    originalUrl = media.originalUrl,
                    datetime = formattedDateTime,
                    title = media.title,
                    playTime = media.playTime
                )
            }
        }

        private fun formatDateTime(instant: Instant): String {
            return instant
                .toSeoulLocalDateTime()
                .formatYmdHm()
        }
    }
}

package com.sy.imagesaver.presentation.model

import com.sy.imagesaver.domain.data.SearchResult
import com.sy.imagesaver.util.formatYmdHm
import com.sy.imagesaver.util.toSeoulLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
sealed class SearchResultUiModel {
    abstract val id: String
    abstract val thumbnailUrl: String
    abstract val originalUrl: String
    abstract val datetime: String
    abstract val isBookmarked: Boolean

    data class Image(
        override val id: String,
        override val thumbnailUrl: String,
        override val originalUrl: String,
        override val datetime: String,
        override val isBookmarked: Boolean = false
    ) : SearchResultUiModel()

    data class Video(
        override val id: String,
        override val thumbnailUrl: String,
        override val originalUrl: String,
        override val datetime: String,
        val title: String,
        val playTime: Int,
        override val isBookmarked: Boolean = false
    ) : SearchResultUiModel()

    companion object {
        fun fromMedia(searchResult: SearchResult, isBookmarked: Boolean = false): SearchResultUiModel {
            val formattedDateTime = formatDateTime(searchResult.datetime)
            
            return when (searchResult) {
                is SearchResult.Image -> Image(
                    id = searchResult.id,
                    thumbnailUrl = searchResult.thumbnailUrl,
                    originalUrl = searchResult.originalUrl,
                    datetime = formattedDateTime,
                    isBookmarked = isBookmarked
                )
                is SearchResult.Video -> Video(
                    id = searchResult.id,
                    thumbnailUrl = searchResult.thumbnailUrl,
                    originalUrl = searchResult.originalUrl,
                    datetime = formattedDateTime,
                    title = searchResult.title,
                    playTime = searchResult.playTime,
                    isBookmarked = isBookmarked
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

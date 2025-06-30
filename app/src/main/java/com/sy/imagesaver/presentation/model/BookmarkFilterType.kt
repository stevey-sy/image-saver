package com.sy.imagesaver.presentation.model

import com.sy.imagesaver.domain.data.MediaType

enum class BookmarkFilterType(val displayName: String) {
    ALL("전체"),
    IMAGE("이미지"),
    VIDEO("영상");

    fun toMediaType(): MediaType? {
        return when (this) {
            ALL -> null
            IMAGE -> MediaType.IMAGE
            VIDEO -> MediaType.VIDEO
        }
    }

    companion object {
        fun fromString(filterString: String): BookmarkFilterType {
            return when (filterString) {
                "전체" -> ALL
                "이미지" -> IMAGE
                "영상" -> VIDEO
                else -> ALL
            }
        }
    }
} 
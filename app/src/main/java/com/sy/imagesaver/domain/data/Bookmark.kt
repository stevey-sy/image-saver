package com.sy.imagesaver.domain.data

data class Bookmark(
    val id: Int = 0,
    val thumbnailUrl: String,
    val originalUrl: String,
    val datetime: Long,
    val createdAt: Long,
    val type: MediaType
)

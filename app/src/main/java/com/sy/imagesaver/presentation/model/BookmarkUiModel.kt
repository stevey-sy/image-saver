package com.sy.imagesaver.presentation.model

data class BookmarkUiModel(
    val id: Int,
    val thumbnailUrl: String,
    val originalUrl: String,
    val datetime: String,
    val createdAt: String,
    val type: String
)

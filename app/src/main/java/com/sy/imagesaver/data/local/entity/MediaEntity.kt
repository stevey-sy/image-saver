package com.sy.imagesaver.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class MediaType {
    IMAGE, VIDEO
}

@Entity(tableName = "media")
data class MediaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val thumbnailUrl: String,
    val originalUrl: String,
    val datetime: Long,
    val createdAt: Long,
    val type: MediaType
)


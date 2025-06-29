package com.sy.imagesaver.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sy.imagesaver.domain.data.MediaType

@Entity(tableName = "bookmark")
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val thumbnailUrl: String,
    val originalUrl: String,
    val datetime: Long,
    val createdAt: Long,
    val type: MediaType
)


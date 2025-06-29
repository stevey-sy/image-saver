package com.sy.imagesaver.data.mapper

import com.sy.imagesaver.data.local.entity.BookmarkEntity
import com.sy.imagesaver.domain.data.MediaType
import com.sy.imagesaver.presentation.model.SearchResultUiModel
import com.sy.imagesaver.util.formatYmdHm
import com.sy.imagesaver.util.toInstant
import com.sy.imagesaver.util.toSeoulLocalDateTime
import kotlin.time.ExperimentalTime
import javax.inject.Inject

class BookmarkEntityMapper @Inject constructor() {
    
    @OptIn(ExperimentalTime::class)
    fun toMediaUiModel(bookmarkEntity: BookmarkEntity): SearchResultUiModel {
        return when (bookmarkEntity.type) {
            MediaType.IMAGE -> SearchResultUiModel.Image(
                id = bookmarkEntity.id.toString(),
                thumbnailUrl = bookmarkEntity.thumbnailUrl,
                originalUrl = bookmarkEntity.originalUrl,
                datetime = bookmarkEntity.datetime.toInstant().toSeoulLocalDateTime().formatYmdHm()
            )
            MediaType.VIDEO -> SearchResultUiModel.Video(
                id = bookmarkEntity.id.toString(),
                thumbnailUrl = bookmarkEntity.thumbnailUrl,
                originalUrl = bookmarkEntity.originalUrl,
                datetime = bookmarkEntity.datetime.toInstant().toSeoulLocalDateTime().formatYmdHm(),
                title = "저장된 비디오", // BookmarkEntity에는 title이 없으므로 기본값 사용
                playTime = 0 // BookmarkEntity에는 playTime이 없으므로 기본값 사용
            )
        }
    }
    
    fun toMediaUiModelList(bookmarkEntities: List<BookmarkEntity>): List<SearchResultUiModel> {
        return bookmarkEntities.map { toMediaUiModel(it) }
    }
} 
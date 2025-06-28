package com.sy.imagesaver.data.mapper

import com.sy.imagesaver.data.local.entity.MediaEntity
import com.sy.imagesaver.data.local.entity.MediaType
import com.sy.imagesaver.presentation.model.MediaUiModel
import com.sy.imagesaver.util.formatYmdHm
import com.sy.imagesaver.util.toInstant
import com.sy.imagesaver.util.toSeoulLocalDateTime
import kotlin.time.ExperimentalTime
import javax.inject.Inject

class MediaEntityMapper @Inject constructor() {
    
    @OptIn(ExperimentalTime::class)
    fun toMediaUiModel(mediaEntity: MediaEntity): MediaUiModel {
        return when (mediaEntity.type) {
            MediaType.IMAGE -> MediaUiModel.Image(
                id = mediaEntity.id.toString(),
                thumbnailUrl = mediaEntity.thumbnailUrl,
                originalUrl = mediaEntity.originalUrl,
                datetime = mediaEntity.datetime.toInstant().toSeoulLocalDateTime().formatYmdHm()
            )
            MediaType.VIDEO -> MediaUiModel.Video(
                id = mediaEntity.id.toString(),
                thumbnailUrl = mediaEntity.thumbnailUrl,
                originalUrl = mediaEntity.originalUrl,
                datetime = mediaEntity.datetime.toInstant().toSeoulLocalDateTime().formatYmdHm(),
                title = "저장된 비디오", // MediaEntity에는 title이 없으므로 기본값 사용
                playTime = 0 // MediaEntity에는 playTime이 없으므로 기본값 사용
            )
        }
    }
    
    fun toMediaUiModelList(mediaEntities: List<MediaEntity>): List<MediaUiModel> {
        return mediaEntities.map { toMediaUiModel(it) }
    }
} 
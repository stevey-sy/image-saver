package com.sy.imagesaver.data.mapper

import com.sy.imagesaver.domain.data.Media
import com.sy.imagesaver.presentation.model.MediaUiModel
import com.sy.imagesaver.util.parseToInstant
import kotlin.time.ExperimentalTime
import javax.inject.Inject

class MediaUiModelMapper @Inject constructor() {
    
    @OptIn(ExperimentalTime::class)
    fun toMedia(mediaUiModel: MediaUiModel): Media {
        return when (mediaUiModel) {
            is MediaUiModel.Image -> Media.Image(
                id = mediaUiModel.id,
                thumbnailUrl = mediaUiModel.thumbnailUrl,
                originalUrl = mediaUiModel.originalUrl,
                datetime = mediaUiModel.datetime.parseToInstant()
            )
            is MediaUiModel.Video -> Media.Video(
                id = mediaUiModel.id,
                thumbnailUrl = mediaUiModel.thumbnailUrl,
                originalUrl = mediaUiModel.originalUrl,
                datetime = mediaUiModel.datetime.parseToInstant(),
                title = mediaUiModel.title,
                playTime = mediaUiModel.playTime
            )
        }
    }
    
    fun toMediaList(mediaUiModels: List<MediaUiModel>): List<Media> {
        return mediaUiModels.map { toMedia(it) }
    }
} 
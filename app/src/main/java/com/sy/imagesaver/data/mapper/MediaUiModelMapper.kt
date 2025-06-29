package com.sy.imagesaver.data.mapper

import com.sy.imagesaver.domain.data.SearchResult
import com.sy.imagesaver.presentation.model.SearchResultUiModel
import com.sy.imagesaver.util.parseToInstant
import kotlin.time.ExperimentalTime
import javax.inject.Inject

class MediaUiModelMapper @Inject constructor() {
    
    @OptIn(ExperimentalTime::class)
    fun toMedia(searchResultUiModel: SearchResultUiModel): SearchResult {
        return when (searchResultUiModel) {
            is SearchResultUiModel.Image -> SearchResult.Image(
                id = searchResultUiModel.id,
                thumbnailUrl = searchResultUiModel.thumbnailUrl,
                originalUrl = searchResultUiModel.originalUrl,
                datetime = searchResultUiModel.datetime.parseToInstant()
            )
            is SearchResultUiModel.Video -> SearchResult.Video(
                id = searchResultUiModel.id,
                thumbnailUrl = searchResultUiModel.thumbnailUrl,
                originalUrl = searchResultUiModel.originalUrl,
                datetime = searchResultUiModel.datetime.parseToInstant(),
                title = searchResultUiModel.title,
                playTime = searchResultUiModel.playTime
            )
        }
    }
    
    fun toMediaList(searchResultUiModels: List<SearchResultUiModel>): List<SearchResult> {
        return searchResultUiModels.map { toMedia(it) }
    }
} 
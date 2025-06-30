package com.sy.imagesaver.presentation.model.mapper

import com.sy.imagesaver.domain.data.SearchResult
import com.sy.imagesaver.presentation.model.SearchResultUiModel
import com.sy.imagesaver.util.parseToInstant
import javax.inject.Inject
import kotlin.time.ExperimentalTime

class SearchUiModelMapper @Inject constructor() {

    @OptIn(ExperimentalTime::class)
    fun toSearchResult(searchResultUiModel: SearchResultUiModel): SearchResult {
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
}
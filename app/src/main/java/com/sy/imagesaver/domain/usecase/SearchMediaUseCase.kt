package com.sy.imagesaver.domain.usecase

import androidx.paging.PagingData
import com.sy.imagesaver.data.repository.MediaRepository
import com.sy.imagesaver.data.remote.dto.KakaoResponseDto
import com.sy.imagesaver.domain.data.Media
import com.sy.imagesaver.presentation.model.MediaUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.time.ExperimentalTime

class SearchMediaUseCase @Inject constructor(
    private val mediaRepository: MediaRepository
) {
    @OptIn(ExperimentalTime::class)
    operator fun invoke(
        query: String,
        page: Int = 1,
        size: Int = 30
    ): Flow<KakaoResponseDto<Media>> {
        return mediaRepository.searchMedia(query, page, size)
            .map { response ->
                // datetime 기준으로 정렬
                val sortedDocuments = response.documents.sortedByDescending { media ->
                    when (media) {
                        is Media.Image -> media.datetime
                        is Media.Video -> media.datetime
                    }
                }
                response.copy(documents = sortedDocuments)
            }
    }
    
    fun searchMediaPaged(query: String): Flow<PagingData<MediaUiModel>> {
        return mediaRepository.searchMediaPaged(query)
    }
} 
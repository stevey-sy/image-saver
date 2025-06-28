package com.sy.imagesaver.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sy.imagesaver.data.mapper.MediaDtoMapper
import com.sy.imagesaver.data.remote.datasource.ImageRemoteDataSource
import com.sy.imagesaver.data.remote.datasource.VideoRemoteDataSource
import com.sy.imagesaver.data.remote.dto.MetaDto
import com.sy.imagesaver.data.remote.dto.KakaoResponseDto
import com.sy.imagesaver.domain.data.Media
import com.sy.imagesaver.presentation.model.MediaUiModel
import javax.inject.Inject
import kotlin.time.ExperimentalTime

class MediaPagingSource @Inject constructor(
    private val imageRemoteDataSource: ImageRemoteDataSource,
    private val videoRemoteDataSource: VideoRemoteDataSource,
    private val mediaDtoMapper: MediaDtoMapper,
    private val query: String
) : PagingSource<Int, MediaUiModel>() {

    override fun getRefreshKey(state: PagingState<Int, MediaUiModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MediaUiModel> {
        return try {
            val page = params.key ?: 1
            val pageSize = params.loadSize

            // 이미지와 비디오를 병렬로 검색
            val imageResponse = imageRemoteDataSource.searchImages(query, page, pageSize)
            val videoResponse = videoRemoteDataSource.searchVideos(query, page, pageSize)
            
            // 이미지와 비디오 미디어 리스트를 합침
            val imageMediaList = imageResponse.documents.map { mediaDtoMapper.fromImageDto(it) }
            val videoMediaList = videoResponse.documents.map { mediaDtoMapper.fromVideoDto(it) }
            val combinedMediaList = imageMediaList + videoMediaList
            
            // datetime 기준으로 정렬
            val sortedMediaList = combinedMediaList.sortedByDescending { media ->
                when (media) {
                    is Media.Image -> media.datetime
                    is Media.Video -> media.datetime
                }
            }
            
            // MediaUiModel로 변환
            val mediaUiModels = sortedMediaList.map { media ->
                MediaUiModel.fromMedia(media)
            }
            
            // 메타데이터는 이미지 기준으로 사용
            val isEnd = imageResponse.meta.isEnd && videoResponse.meta.isEnd
            
            val nextKey = if (isEnd) {
                null
            } else {
                page + 1
            }

            LoadResult.Page(
                data = mediaUiModels,
                prevKey = if (page == 1) null else page - 1,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
} 
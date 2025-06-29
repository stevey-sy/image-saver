package com.sy.imagesaver.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sy.imagesaver.data.cache.SearchCacheManager
import com.sy.imagesaver.data.local.datasource.BookmarkLocalDataSource
import com.sy.imagesaver.data.mapper.MediaDtoMapper
import com.sy.imagesaver.data.remote.datasource.ImageRemoteDataSource
import com.sy.imagesaver.data.remote.datasource.VideoRemoteDataSource
import com.sy.imagesaver.domain.data.SearchResult
import com.sy.imagesaver.presentation.model.SearchResultUiModel
import javax.inject.Inject
import kotlin.time.ExperimentalTime

class MediaPagingSource @Inject constructor(
    private val imageRemoteDataSource: ImageRemoteDataSource,
    private val videoRemoteDataSource: VideoRemoteDataSource,
    private val bookmarkLocalDataSource: BookmarkLocalDataSource,
    private val searchCacheManager: SearchCacheManager,
    private val mediaDtoMapper: MediaDtoMapper,
    private val query: String
) : PagingSource<Int, SearchResultUiModel>() {

    override fun getRefreshKey(state: PagingState<Int, SearchResultUiModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SearchResultUiModel> {
        return try {
            val page = params.key ?: 1
            val pageSize = params.loadSize

            // 첫 페이지이고 캐시가 유효한 경우 캐시된 데이터 사용
            if (page == 1 && searchCacheManager.isCacheValid(query)) {
                val cachedMediaList = searchCacheManager.getCachedMediaList(query)
                if (cachedMediaList != null) {
                    return LoadResult.Page(
                        data = cachedMediaList,
                        prevKey = null,
                        nextKey = null // 캐시된 데이터는 한 페이지로 간주
                    )
                }
            }

            // 북마크된 미디어의 thumbnailUrl 목록 조회
            val bookmarkedThumbnailUrls = bookmarkLocalDataSource.getBookmarkedThumbnailUrls()

            // 이미지와 비디오를 병렬로 검색
            val imageResponse = imageRemoteDataSource.searchImages(query, page, pageSize)
            val videoResponse = videoRemoteDataSource.searchVideos(query, page, pageSize)
            
            // 이미지와 비디오 미디어 리스트를 합침
            val imageMediaList = imageResponse.documents.map { mediaDtoMapper.fromImageDto(it) }
            val videoMediaList = videoResponse.documents.map { mediaDtoMapper.fromVideoDto(it) }
            val combinedMediaList = imageMediaList + videoMediaList
            
            // datetime 기준으로 정렬
            val sortedSearchResultLists = combinedMediaList.sortedByDescending { media ->
                when (media) {
                    is SearchResult.Image -> media.datetime
                    is SearchResult.Video -> media.datetime
                }
            }
            
            // MediaUiModel로 변환하면서 북마크 상태 확인
            val searchResultUiModels = sortedSearchResultLists.map { media ->
                val isBookmarked = bookmarkedThumbnailUrls.contains(media.thumbnailUrl)
                SearchResultUiModel.fromMedia(media, isBookmarked)
            }
            
            // 첫 페이지인 경우 캐시에 저장
            if (page == 1) {
                searchCacheManager.cacheMediaList(query, searchResultUiModels)
            }
            
            // 메타데이터는 이미지 기준으로 사용
            val isEnd = imageResponse.meta.isEnd && videoResponse.meta.isEnd
            
            val nextKey = if (isEnd) {
                null
            } else {
                page + 1
            }

            LoadResult.Page(
                data = searchResultUiModels,
                prevKey = if (page == 1) null else page - 1,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
} 
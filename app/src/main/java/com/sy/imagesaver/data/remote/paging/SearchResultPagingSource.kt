package com.sy.imagesaver.data.remote.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sy.imagesaver.data.cache.SearchCacheManager
import com.sy.imagesaver.data.local.datasource.BookmarkLocalDataSource
import com.sy.imagesaver.data.mapper.SearchResultMapper
import com.sy.imagesaver.data.remote.datasource.ImageRemoteDataSource
import com.sy.imagesaver.data.remote.datasource.VideoRemoteDataSource
import com.sy.imagesaver.domain.data.SearchResult
import com.sy.imagesaver.presentation.model.SearchResultUiModel
import javax.inject.Inject
import kotlin.time.ExperimentalTime

class SearchResultPagingSource @Inject constructor(
    private val imageRemoteDataSource: ImageRemoteDataSource,
    private val videoRemoteDataSource: VideoRemoteDataSource,
    private val bookmarkLocalDataSource: BookmarkLocalDataSource,
    private val searchCacheManager: SearchCacheManager,
    private val searchResultMapper: SearchResultMapper,
    private val query: String,
    private val pageSize: Int // 각 API 당 요청할 아이템 수
) : PagingSource<Int, SearchResultUiModel>() {

    companion object {
        private const val TAG = "SearchPagingSource"
    }

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

            Log.d(TAG, "Loading page $page for query: $query")

            // 첫 페이지이고 캐시가 유효한 경우 캐시된 데이터 사용
            if (page == 1 && searchCacheManager.isCacheValid(query)) {
                Log.d(TAG, "Cache is valid for query: $query, using cached data")
                val cachedMediaList = searchCacheManager.getCachedMediaList(query)
                if (cachedMediaList != null) {
                    // Domain 모델을 UI 모델로 변환
                    val cachedUiModels = cachedMediaList.map { media ->
                        val isBookmarked = bookmarkLocalDataSource.getBookmarkedThumbnailUrls().contains(media.thumbnailUrl)
                        SearchResultUiModel.fromMedia(media, isBookmarked)
                    }
                    Log.d(TAG, "Returned ${cachedUiModels.size} items from cache")
                    return LoadResult.Page(
                        data = cachedUiModels,
                        prevKey = null,
                        nextKey = null // 캐시된 데이터는 한 페이지로 간주
                    )
                }
            }

            Log.d(TAG, "Making network request for query: $query, page: $page")

            // 북마크된 미디어의 thumbnailUrl 목록 조회
            val bookmarkedThumbnailUrls = bookmarkLocalDataSource.getBookmarkedThumbnailUrls()

            // 이미지와 비디오를 병렬로 검색
            val imageResponse = imageRemoteDataSource.searchImages(query, page, pageSize)
            val videoResponse = videoRemoteDataSource.searchVideos(query, page, pageSize)
            
            Log.d(TAG, "Received ${imageResponse.documents.size} images and ${videoResponse.documents.size} videos from network")
            
            // 이미지와 비디오 미디어 리스트를 합침
            val imageMediaList = imageResponse.documents.map { searchResultMapper.fromImageDto(it) }
            val videoMediaList = videoResponse.documents.map { searchResultMapper.fromVideoDto(it) }
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
            
            // 첫 페이지인 경우 캐시에 저장 (Domain 모델 저장)
            if (page == 1) {
                Log.d(TAG, "Caching ${sortedSearchResultLists.size} items for query: $query")
                searchCacheManager.cacheMediaList(query, sortedSearchResultLists)
            }
            
            // 메타데이터는 이미지 기준으로 사용
            val isEnd = imageResponse.meta.isEnd && videoResponse.meta.isEnd
            
            val nextKey = if (isEnd) {
                null
            } else {
                page + 1
            }

            Log.d(TAG, "Returning ${searchResultUiModels.size} items from network, nextKey: $nextKey")

            LoadResult.Page(
                data = searchResultUiModels,
                prevKey = if (page == 1) null else page - 1,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error loading data", e)
            LoadResult.Error(e)
        }
    }
} 
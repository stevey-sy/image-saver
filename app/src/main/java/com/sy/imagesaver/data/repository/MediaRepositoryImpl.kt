package com.sy.imagesaver.data.repository

import com.sy.imagesaver.data.mapper.MediaDtoMapper
import com.sy.imagesaver.data.remote.datasource.ImageRemoteDataSource
import com.sy.imagesaver.data.remote.datasource.VideoRemoteDataSource
import com.sy.imagesaver.data.remote.dto.MetaDto
import com.sy.imagesaver.data.remote.dto.KakaoResponseDto
import com.sy.imagesaver.domain.data.Media
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(
    private val imageRemoteDataSource: ImageRemoteDataSource,
    private val videoRemoteDataSource: VideoRemoteDataSource,
    private val mediaDtoMapper: MediaDtoMapper
) : MediaRepository {
    
    override fun searchMedia(
        query: String,
        page: Int,
        size: Int
    ): Flow<KakaoResponseDto<Media>> = flow {
        // 이미지와 비디오를 병렬로 검색
        val imageResponse = imageRemoteDataSource.searchImages(query, page, size)
        val videoResponse = videoRemoteDataSource.searchVideos(query, page, size)
        
        // 이미지와 비디오 미디어 리스트를 합침 (정렬은 UseCase에서 처리)
        val imageMediaList = imageResponse.documents.map { mediaDtoMapper.fromImageDto(it) }
        val videoMediaList = videoResponse.documents.map { mediaDtoMapper.fromVideoDto(it) }
        val combinedMediaList = imageMediaList + videoMediaList
        
        // 메타데이터는 이미지 기준으로 사용 (또는 더 적절한 로직으로 결정)
        val combinedMeta = MetaDto(
            totalCount = imageResponse.meta.totalCount + videoResponse.meta.totalCount,
            pageableCount = imageResponse.meta.pageableCount + videoResponse.meta.pageableCount,
            isEnd = imageResponse.meta.isEnd && videoResponse.meta.isEnd
        )
        
        emit(KakaoResponseDto(combinedMeta, combinedMediaList))
    }
}
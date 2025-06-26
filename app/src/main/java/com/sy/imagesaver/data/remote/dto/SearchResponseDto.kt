package com.sy.imagesaver.data.remote.dto

import com.squareup.moshi.Json

data class SearchResponseDto<T>(
    @Json(name = "meta")
    val meta: MetaDto,

    @Json(name = "documents")
    val documents: List<T>
)

// Type aliases for specific search responses
typealias ImageSearchResponseDto = SearchResponseDto<ImageDto>
typealias VideoSearchResponseDto = SearchResponseDto<VideoDto> 
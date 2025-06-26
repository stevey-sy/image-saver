package com.sy.imagesaver.data.remote.dto

import com.squareup.moshi.Json

data class MetaDto(
    @Json(name = "total_count")
    val totalCount: Int,
    
    @Json(name = "pageable_count")
    val pageableCount: Int,
    
    @Json(name = "is_end")
    val isEnd: Boolean
) 
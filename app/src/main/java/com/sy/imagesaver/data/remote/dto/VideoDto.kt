package com.sy.imagesaver.data.remote.dto

import com.squareup.moshi.Json

data class VideoDto(
    @Json(name = "title")
    val title: String,
    
    @Json(name = "play_time")
    val playTime: Int,
    
    @Json(name = "thumbnail")
    val thumbnail: String,
    
    @Json(name = "url")
    val url: String,
    
    @Json(name = "datetime")
    val datetime: String,
    
    @Json(name = "author")
    val author: String
)

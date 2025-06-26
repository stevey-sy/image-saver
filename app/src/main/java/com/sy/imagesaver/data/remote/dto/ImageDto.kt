package com.sy.imagesaver.data.remote.dto

import com.squareup.moshi.Json

data class ImageDto(
    @Json(name = "collection")
    val collection: String,
    
    @Json(name = "thumbnail_url")
    val thumbnailUrl: String,
    
    @Json(name = "image_url")
    val imageUrl: String,
    
    @Json(name = "width")
    val width: Int,
    
    @Json(name = "height")
    val height: Int,
    
    @Json(name = "display_sitename")
    val displaySitename: String,
    
    @Json(name = "doc_url")
    val docUrl: String,
    
    @Json(name = "datetime")
    val datetime: String
)
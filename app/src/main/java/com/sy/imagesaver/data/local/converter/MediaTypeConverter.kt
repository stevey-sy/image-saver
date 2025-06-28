package com.sy.imagesaver.data.local.converter

import androidx.room.TypeConverter
import com.sy.imagesaver.data.local.entity.MediaType

class MediaTypeConverter {
    
    @TypeConverter
    fun fromMediaType(mediaType: MediaType): String {
        return mediaType.name
    }
    
    @TypeConverter
    fun toMediaType(mediaTypeString: String): MediaType {
        return try {
            MediaType.valueOf(mediaTypeString)
        } catch (e: IllegalArgumentException) {
            MediaType.IMAGE // 기본값
        }
    }
} 
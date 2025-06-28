package com.sy.imagesaver.domain.usecase

import com.sy.imagesaver.data.local.datasource.MediaLocalDataSource
import com.sy.imagesaver.data.local.entity.MediaEntity
import com.sy.imagesaver.data.local.entity.MediaType
import com.sy.imagesaver.domain.data.Media
import com.sy.imagesaver.util.toLong
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlin.time.ExperimentalTime

class SaveMediaUseCase @Inject constructor(
    private val mediaLocalDataSource: MediaLocalDataSource
) {
    
    @OptIn(ExperimentalTime::class)
    suspend operator fun invoke(media: Media): Long {
        val mediaEntity = MediaEntity(
            thumbnailUrl = media.thumbnailUrl,
            originalUrl = media.originalUrl,
            datetime = media.datetime.toLong(),
            createdAt = System.currentTimeMillis(),
            type = when (media) {
                is Media.Image -> MediaType.IMAGE
                is Media.Video -> MediaType.VIDEO
            }
        )
        
        return mediaLocalDataSource.insertMedia(mediaEntity)
    }
    
    @OptIn(ExperimentalTime::class)
    suspend fun saveMediaList(mediaList: List<Media>) {
        val mediaEntities = mediaList.map { media ->
            MediaEntity(
                thumbnailUrl = media.thumbnailUrl,
                originalUrl = media.originalUrl,
                datetime = media.datetime.toLong(),
                createdAt = System.currentTimeMillis(),
                type = when (media) {
                    is Media.Image -> MediaType.IMAGE
                    is Media.Video -> MediaType.VIDEO
                }
            )
        }
        
        mediaLocalDataSource.insertMediaList(mediaEntities)
    }
    
    fun getAllSavedMedia(): Flow<List<MediaEntity>> {
        return mediaLocalDataSource.getAllMedia()
    }
    
    fun getMediaByType(type: String): Flow<List<MediaEntity>> {
        return mediaLocalDataSource.getMediaByType(type)
    }
} 
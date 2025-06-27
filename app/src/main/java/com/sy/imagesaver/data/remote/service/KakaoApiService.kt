package com.sy.imagesaver.data.remote.service

import com.sy.imagesaver.data.remote.dto.KakaoImageResponseDto
import com.sy.imagesaver.data.remote.dto.KakaoVideoResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface KakaoApiService {
    @GET("v2/search/image")
    suspend fun searchImages(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("size") size: Int = 30
    ): KakaoImageResponseDto

    @GET("v2/search/vclip")
    suspend fun searchVideos(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("size") size: Int = 15
    ): KakaoVideoResponseDto
}
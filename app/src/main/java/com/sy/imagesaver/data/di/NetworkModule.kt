package com.sy.imagesaver.data.di

import com.sy.imagesaver.BuildConfig
import com.sy.imagesaver.data.remote.service.KakaoApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }
    
    @Provides
    @Singleton
    fun provideKakaoApiInterceptor(): Interceptor {
        return Interceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .header("Authorization", "KakaoAK ${BuildConfig.KAKAO_API_KEY}")
                .method(original.method, original.body)
                .build()
            chain.proceed(request)
        }
    }
    
    @Provides
    @Singleton
    fun provideOkHttpClient(interceptor: Interceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    }
    
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://dapi.kakao.com/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }
    
    @Provides
    @Singleton
    fun provideKakaoApiService(retrofit: Retrofit): KakaoApiService {
        return retrofit.create(KakaoApiService::class.java)
    }
}

@Module
@InstallIn(SingletonComponent::class)
interface NetworkModuleBinds {
    @Binds
    @Singleton
    fun bindImageRemoteDataSource(
        imageRemoteDataSourceImpl: com.sy.imagesaver.data.remote.datasource.ImageRemoteDataSourceImpl
    ): com.sy.imagesaver.data.remote.datasource.ImageRemoteDataSource
    
    @Binds
    @Singleton
    fun bindVideoRemoteDataSource(
        videoRemoteDataSourceImpl: com.sy.imagesaver.data.remote.datasource.VideoRemoteDataSourceImpl
    ): com.sy.imagesaver.data.remote.datasource.VideoRemoteDataSource
    
    @Binds
    @Singleton
    fun bindMediaRepository(
        mediaRepositoryImpl: com.sy.imagesaver.data.repository.MediaRepositoryImpl
    ): com.sy.imagesaver.data.repository.MediaRepository
}
import com.sy.imagesaver.data.remote.dto.ImageDto
import com.sy.imagesaver.data.remote.dto.SearchResponseDto
import com.sy.imagesaver.data.remote.dto.VideoDto
import retrofit2.http.GET
import retrofit2.http.Query

interface KakaoApiService {
    @GET("/image")
    suspend fun searchImages(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("size") size: Int = 30
    ): SearchResponseDto<ImageDto>

    @GET("/vclip")
    suspend fun searchVideos(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("size") size: Int = 15
    ): SearchResponseDto<VideoDto>
}
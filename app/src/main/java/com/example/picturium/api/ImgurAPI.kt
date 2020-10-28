package com.example.picturium.api

import com.example.picturium.BuildConfig
import com.example.picturium.api.request.RefreshTokenRequest
import com.example.picturium.models.Image
import com.example.picturium.models.RefreshToken
import com.example.picturium.models.Submission
import com.example.picturium.models.UserData
import com.example.picturium.viewmodels.UserViewModel
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.io.IOException
import java.util.concurrent.TimeUnit

interface ImgurAPI {
    private class ImgurInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            val req = chain.request()

            if (req.header("Authorization") != null)
                return chain.proceed(req)

            val reqBuilder = req.newBuilder()
            if (UserViewModel.isLoggedIn())
                reqBuilder.header("Authorization", "Bearer ${UserViewModel.accessToken}")
            else
                reqBuilder.header("Authorization", "Client-ID ${BuildConfig.CLIENT_ID}")
            return chain.proceed(reqBuilder.build())
        }
    }

    sealed class CallResult<out T> {
        object NetworkError : CallResult<Nothing>()

        data class SuccessResponse<out T>(
            val data: T,
            val success: Boolean,
            val status: Int,
        ) : CallResult<T>()

        data class ErrorResponse(
            val data: Data?,
            val success: Boolean,
            val status: Int,
        ) : CallResult<Nothing>() {
            data class Data(
                val error: String,
                val request: String,
                val method: String,
            )
        }
    }

    companion object {
        private const val BASE_URL = "https://api.imgur.com/"
        const val LOGIN_URL = "$BASE_URL/oauth2/authorize?response_type=token&client_id=${BuildConfig.CLIENT_ID}"

        private val _httpCltBuilder = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(ImgurInterceptor())
        val instance: ImgurAPI = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(_httpCltBuilder.build())
            .build()
            .create(ImgurAPI::class.java)

        suspend fun <T> safeCall(
            dispatcher: CoroutineDispatcher = Dispatchers.IO,
            apiCall: suspend () -> CallResult<T>
        ): CallResult<T> {
            return withContext(dispatcher) {
                try {
                    apiCall()
                } catch (throwable: Throwable) {
                    when (throwable) {
                        is IOException -> CallResult.NetworkError
                        is HttpException -> Gson().fromJson(throwable.message(), CallResult.ErrorResponse::class.java)
                        else -> CallResult.ErrorResponse(null, false, -1)
                    }
                }
            }
        }
    }

    @GET("3/gallery/{section}/{sort}/{window}/{page}")
    suspend fun getGallery(
        @Path("section") section: String,
        @Path("sort") sort: String,
        @Path("window") window: String,
        @Path("page") page: Int,
        @Query("showViral") showViral: Boolean = false,
        @Query("mature") mature: Boolean = false,
        @Query("album_previews") albumPreview: Boolean = false,
    ): CallResult.SuccessResponse<List<Submission>>

    @GET("3/gallery/search/{sort}/{window}/{page}")
    suspend fun getSearchGallery(
        @Path("sort") sort: String,
        @Path("window") window: String,
        @Path("page") page: Int,
        @Query("q") query: String,
    ): CallResult.SuccessResponse<List<Submission>>

    @GET("oauth2/secret")
    suspend fun checkAccessToken()

    @Headers("Authorization: Client-ID ${BuildConfig.CLIENT_ID}")
    @POST("oauth2/token")
    suspend fun refreshAccessToken(@Body refreshTokenRequest: RefreshTokenRequest = RefreshTokenRequest()): CallResult.SuccessResponse<RefreshToken>

    @GET("3/account/{userName}")
    suspend fun getUserData(@Path("userName") userName: String = "me"): CallResult.SuccessResponse<UserData>

    @GET("3/gallery/{id}")
    suspend fun getSubmission(@Path("id") id: String): CallResult.SuccessResponse<Submission>

    @GET("3/account/{userName}/submissions")
    suspend fun getSubmissionsFrom(@Path("userName") userName: String = "me"): CallResult.SuccessResponse<List<Submission>>

    @GET("3/account/{userName}/favorites")
    suspend fun getFavoritesFrom(@Path("userName") userName: String = "me"): CallResult.SuccessResponse<List<Submission>>

    @GET("3/image/{imageId}")
    suspend fun getImage(@Path("imageId") imageId: String): CallResult.SuccessResponse<Image>

    @POST("3/album/{id}/favorite")
    suspend fun toggleFavoriteAlbum(@Path("id") id: String): CallResult.SuccessResponse<String?>

    @POST("3/image/{id}/favorite")
    suspend fun toggleFavoriteImage(@Path("id") id: String): CallResult.SuccessResponse<String?>

    @POST("3/gallery/{id}/vote/{vote}")
    suspend fun voteOnSubmission(@Path("id") id: String, @Path("vote") vote: String = "veto"): CallResult.SuccessResponse<Any?>

    @Multipart
    @JvmSuppressWildcards
    @POST("3/album")
    suspend fun newAlbum(
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("ids[]") ids: List<RequestBody>,
        @Part("cover") cover: RequestBody,
    ): CallResult.SuccessResponse<Submission>

    @Multipart
    @POST("3/image")
    suspend fun newImage(@Part images: MultipartBody.Part): CallResult.SuccessResponse<Image>
}
package com.example.picturium.api

import com.example.picturium.BuildConfig
import com.example.picturium.User
import com.example.picturium.api.request.RefreshTokenRequest
import com.example.picturium.api.response.GalleryResponse
import com.example.picturium.api.response.RefreshTokenResponse
import com.example.picturium.api.response.UserDataResponse
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ImgurAPI {

    private class ImgurInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            val req = chain.request()

            if (req.header("Authorization") != null)
                return chain.proceed(req)

            val reqBuilder = req.newBuilder()
            if (User.isLoggedIn())
                reqBuilder.header("Authorization", "Bearer ${User.accessToken}")
            else
                reqBuilder.header("Authorization", "Client-ID ${BuildConfig.CLIENT_ID}")
            return chain.proceed(reqBuilder.build())
        }
    }

    companion object {
        const val BASE_URL = "https://api.imgur.com/"
        const val LOGIN_URL = "$BASE_URL/oauth2/authorize?response_type=token&client_id=${BuildConfig.CLIENT_ID}"

        private val _httpCltBuilder = OkHttpClient.Builder()
            .addInterceptor(ImgurInterceptor())
        val instance: ImgurAPI = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(_httpCltBuilder.build())
            .build()
            .create(ImgurAPI::class.java)
    }

    @GET("3/gallery/{section}/{sort}/{window}/{page}")
    suspend fun getGallery(
        @Path("section") section: String,
        @Path("sort") sort: String,
        @Path("window") window: String,
        @Path("page") page: Int,
        @Query("showViral") showViral: Boolean,
        @Query("mature") mature: Boolean,
        @Query("album_previews") albumPreview: Boolean
    ): Response<GalleryResponse>

    @GET("3/gallery/search/{sort}/{window}/{page}")
    suspend fun getSearchGallery(
        @Path("sort") sort: String,
        @Path("window") window: String,
        @Path("page") page: Int,
        @Query("q") query: String
    ): Response<GalleryResponse>

    @GET("oauth2/secret")
    suspend fun checkAccessToken(): Response<Void>

    @Headers("Authorization: Client-ID ${BuildConfig.CLIENT_ID}")
    @POST("oauth2/token")
    suspend fun refreshAccessToken(@Body refreshTokenRequest: RefreshTokenRequest = RefreshTokenRequest()): Response<RefreshTokenResponse>

    @GET("3/account/{userName}")
    suspend fun getUserData(@Path("userName") userName: String): Response<UserDataResponse>
}
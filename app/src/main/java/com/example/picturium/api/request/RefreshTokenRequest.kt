package com.example.picturium.api.request

import com.example.picturium.BuildConfig
import com.example.picturium.viewmodels.UserViewModel
import com.google.gson.annotations.SerializedName

data class RefreshTokenRequest(
    @SerializedName("refresh_token")
    val refreshToken: String = UserViewModel.refreshToken!!,

    @SerializedName("client_id")
    val clientID: String = BuildConfig.CLIENT_ID,

    @SerializedName("client_secret")
    val clientSecret: String = BuildConfig.CLIENT_SECRET,

    @SerializedName("grant_type")
    val grantType: String = "refresh_token",
)

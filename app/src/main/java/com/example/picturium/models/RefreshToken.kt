package com.example.picturium.models

import com.google.gson.annotations.SerializedName

data class RefreshToken(
    @SerializedName("access_token")
    val accessToken: String,

    @SerializedName("account_username")
    val username: String,

    @SerializedName("refresh_token")
    val refreshToken: String,
)

package com.example.picturium.api.response

import com.example.picturium.models.RefreshToken

data class RefreshTokenResponse(
    val data: RefreshToken,
    val status: Int,
    val success: Boolean
)

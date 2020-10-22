package com.example.picturium.api.response

import com.example.picturium.models.Image

data class ImageResponse(
    val data: Image,
    val status: Int,
    val success: Boolean
)
package com.example.picturium.api.response

import com.example.picturium.models.MediaResource

data class MediaResourceResponse(
    val data: MediaResource,
    val status: Int,
    val success: Boolean
)
package com.example.picturium.api.response

import com.example.picturium.models.Submission

data class GalleryResponse(
    val data: List<Submission>
)
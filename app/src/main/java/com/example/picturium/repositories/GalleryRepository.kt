package com.example.picturium.repositories

import com.example.picturium.api.ImgurAPI
import com.example.picturium.models.Submission

class GalleryRepository {

    suspend fun getGallery(section: String, sort: String, window: String): List<Submission> {

        val res = ImgurAPI.safeCall {
            ImgurAPI.instance.getGallery(section, sort, window, 1)
        }
        return when (res) {
            is ImgurAPI.CallResult.SuccessResponse -> res.body.data
            is ImgurAPI.CallResult.ErrorResponse -> emptyList()
            is ImgurAPI.CallResult.NetworkError -> {
                TODO("Toast no internet")
                emptyList()
            }
        }
    }

    suspend fun getSearchGallery(sort: String, window: String, query: String): List<Submission> {

        val res = ImgurAPI.safeCall {
            ImgurAPI.instance.getSearchGallery(sort, window, 1, query)
        }
        return when (res) {
            is ImgurAPI.CallResult.SuccessResponse -> res.body.data
            is ImgurAPI.CallResult.ErrorResponse -> emptyList()
            is ImgurAPI.CallResult.NetworkError -> {
                TODO("Toast no internet")
                emptyList()
            }
        }
    }
}
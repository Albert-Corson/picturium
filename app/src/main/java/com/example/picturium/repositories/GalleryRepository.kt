package com.example.picturium.repositories

import androidx.lifecycle.LiveData
import com.example.picturium.api.ImgurAPI
import com.example.picturium.models.ThreadData

class GalleryRepository {

    suspend fun getGallery(option: String): List<ThreadData> {

        val response = ImgurAPI.instance
            .getGallery("hot/viral/day", 1, false, false, false)

        if (response.isSuccessful) {
            return response.body()!!.data
        }
        return emptyList()
    }
}
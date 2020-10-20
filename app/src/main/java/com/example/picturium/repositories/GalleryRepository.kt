package com.example.picturium.repositories

import androidx.lifecycle.LiveData
import com.example.picturium.api.ImgurAPI
import com.example.picturium.models.ThreadData

class GalleryRepository {

    suspend fun getGallery(section: String, sort: String, window: String): List<ThreadData> {

        val response = ImgurAPI.instance
            .getGallery(section, sort, window, 1, false, false, false)

        if (response.isSuccessful) {
            return response.body()!!.data
        }
        return emptyList()
    }
}
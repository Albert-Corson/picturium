package com.example.picturium.viewmodels

import androidx.lifecycle.*
import com.example.picturium.models.ThreadData
import com.example.picturium.repositories.GalleryRepository

class GalleryViewModel(): ViewModel() {

    private val currentOption = MutableLiveData(mutableMapOf("section" to "hot", "sort" to "viral", "window" to "day"))
    private var mRepo: GalleryRepository = GalleryRepository()

    val threads: LiveData<List<ThreadData>> = currentOption.switchMap { options ->
        val section = options["section"] ?: error("no section key")
        val sort = options["sort"] ?: error("no sort key")
        val window = options["window"] ?: error("no window key")

        liveData {
            emit(mRepo.getGallery(section, sort, window))
        }
    }

    fun changeOption(section: String, sort: String, window: String) {
        val tmp = currentOption.value!!

        tmp["section"] = section
        tmp["sort"] = sort
        tmp["window"] = window
        currentOption.value = tmp
    }
}
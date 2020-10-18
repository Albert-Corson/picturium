package com.example.picturium.viewmodels

import androidx.lifecycle.*
import com.example.picturium.models.ThreadData
import com.example.picturium.repositories.GalleryRepository

class GalleryViewModel(): ViewModel() {

    private val currentOption: MutableLiveData<String> = MutableLiveData("caca")
    private var mRepo: GalleryRepository = GalleryRepository();

    val threads: LiveData<List<ThreadData>> = currentOption.switchMap {
        liveData {
            emit(mRepo.getGallery(it))
        }
    }
}
package com.example.picturium.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.example.picturium.models.Submission

class GalleryFilterViewModel : GalleryViewModel() {

    private val currentSection: MutableLiveData<String> = MutableLiveData("hot")
    private val currentOption = TripleLiveData(currentSection, currentSort, currentWindow)
    val submissions: LiveData<List<Submission>> = currentOption.switchMap { options ->
        val section = options.first ?: error("no section value")
        val sort = options.second ?: error("no sort value")
        val window = options.third ?: error("no window value")

        liveData {
            emit(mRepo.getGallery(section, sort, window))
        }
    }

    fun setSection(section: String) {
        currentSection.value = section
    }
}
package com.example.picturium.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.picturium.models.Submission

class GalleryFilterViewModel : GalleryViewModel() {

    private val currentSection: MutableLiveData<String> = MutableLiveData("hot")
    private val currentOption = TripleLiveData(currentSection, currentSort, currentWindow)
    val submissions: LiveData<PagingData<Submission>> = currentOption.switchMap { options ->
        val section = options.first ?: error("no section value")
        val sort = options.second ?: error("no sort value")
        val window = options.third ?: error("no window value")

        mRepo.getGallery(section, sort, window).cachedIn(viewModelScope)
    }

    fun setSection(section: String) {
        currentSection.value = section
    }
}
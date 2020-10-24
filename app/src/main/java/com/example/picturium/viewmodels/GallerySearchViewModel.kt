package com.example.picturium.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.paging.PagingData
import com.example.picturium.models.Submission

class GallerySearchViewModel : GalleryViewModel() {

    private val currentQuery: MutableLiveData<String> = MutableLiveData("")
    private val currentOption = TripleLiveData(currentSort, currentWindow, currentQuery)
    val submissions: LiveData<PagingData<Submission>> = currentOption.switchMap { options ->
        val sort = options.first ?: error("no sort value")
        val window = options.second ?: error("no window value")
        val query = options.third ?: error("no query value")

        mRepo.getSearchGallery(sort, window, query)
    }

    fun setQuery(query: String) {
        currentQuery.value = query
    }
}
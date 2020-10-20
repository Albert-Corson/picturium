package com.example.picturium.viewmodels

import androidx.lifecycle.*
import com.example.picturium.models.ThreadData
import com.example.picturium.repositories.GalleryRepository

open class GalleryViewModel(): ViewModel() {

    protected val currentSort : MutableLiveData<String> = MutableLiveData("viral")
    protected val currentWindow : MutableLiveData<String> = MutableLiveData("day")
    protected var mRepo: GalleryRepository = GalleryRepository()


    fun setSort(sort: String) {
        currentSort.value = sort
    }

    fun setWindow(window: String) {
        currentSort.value = window
    }

    inner class TripleLiveData<A, B, C>(
        first: LiveData<A>,
        second: LiveData<B>,
        third: LiveData<C>
    ): MediatorLiveData<Triple<A?, B?, C?>>() {
        init {
            this.addSource(first) { value = Triple(it, second.value, third.value) }
            this.addSource(second) { value = Triple(first.value, it, third.value) }
            this.addSource(third) { value = Triple(first.value, second.value, it) }
        }
    }
}
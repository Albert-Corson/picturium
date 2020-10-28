package com.example.picturium.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.picturium.api.ImgurAPI
import com.example.picturium.models.Submission
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailsPageViewModel : ViewModel() {
    val submission: MutableLiveData<Submission> = MutableLiveData()
    val isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoadingError: MutableLiveData<Boolean> = MutableLiveData(false)

    fun loadSubmission(id: String) {
        isLoading.value = true
        isLoadingError.value = false
        viewModelScope.launch(Dispatchers.IO) {
            val res = ImgurAPI.safeCall {
                ImgurAPI.instance.getSubmission(id)
            }
            withContext(Dispatchers.Main) {
                when (res) {
                    is ImgurAPI.CallResult.SuccessResponse -> {
                        submission.value = res.data
                        isLoadingError.value = false
                    }
                    else -> {
                        isLoadingError.value = true
                    }
                }
                isLoading.value = false
            }
        }
    }
}
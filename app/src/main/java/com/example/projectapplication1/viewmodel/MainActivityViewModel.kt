package com.example.projectapplication1.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.projectapplication1.repository.VideoModel
import com.example.projectapplication1.repository.VideoUrlRepository
import com.example.projectapplication1.util.SuccessErrorListner

class MainActivityViewModel : ViewModel() {

    fun getVideoUrl(context: Context?): MutableLiveData<List<VideoModel>> {
        val videoUrlRepository = VideoUrlRepository()
        return videoUrlRepository.fetchVideUrl(context)
    }

    fun checkIfEverythingIsFineWithdata(videoModelList: List<VideoModel?>?, successErrorListner: SuccessErrorListner) {
        if (videoModelList != null && videoModelList.isNotEmpty()) {
            //Everything is fine.
            successErrorListner.onSuccess(videoModelList)
        } else if (videoModelList != null && videoModelList.isEmpty()) {
            // There is no data
            successErrorListner.emptyDataList()
        } else {
            //Something went wrong
            successErrorListner.onError()
        }
    }
}
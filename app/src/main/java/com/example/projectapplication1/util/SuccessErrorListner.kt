package com.example.projectapplication1.util

import com.example.projectapplication1.repository.VideoModel

interface SuccessErrorListner {
    fun onSuccess(videoModelList: List<VideoModel?>?)
    fun emptyDataList()
    fun onError()
}
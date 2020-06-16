package com.example.projectapplication1.repository

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.projectapplication1.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import java.util.*

class VideoUrlRepository {
    var context: Context? = null

    @SuppressLint("CheckResult")
    fun fetchVideUrl(context: Context?): MutableLiveData<List<VideoModel>> {
        this.context = context
        val listMutableLiveData = MutableLiveData<List<VideoModel>>()
        val updateObservable = galleryVideoData
        updateObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<List<VideoModel>?>() {
                    override fun onSuccess(galleryModelList: List<VideoModel>) {
                        listMutableLiveData.value = galleryModelList
                    }

                    override fun onError(e: Throwable) {}
                })
        return listMutableLiveData
    }

    // checking for Bookmark in the videolist;
    @get:SuppressLint("InlinedApi")
    val galleryVideoData: Single<List<VideoModel>>
        get() = Single.create { emitter ->
            val sharedPreferences = context!!.getSharedPreferences(context!!.resources.getString(R.string.videoplayer_sharedData), Context.MODE_PRIVATE)
            val gson = Gson()
            val type = object : TypeToken<List<VideoModel?>?>() {}.type
            val listImages = gson.fromJson<List<VideoModel>>(sharedPreferences.getString("videolist_flag", null), type)
            val oldListImages: MutableList<VideoModel> = ArrayList()
            if (listImages != null && !listImages.isEmpty()) oldListImages.addAll(listImages)
            try {
                val cursor: Cursor?
                val column_index_data: Int
                var absolutePathOfImage: String? = null
                val uri: Uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                val projection = arrayOf(MediaStore.Video.VideoColumns.DATA,
                        MediaStore.Video.Media.DISPLAY_NAME)
                cursor = context!!.contentResolver.query(uri, projection, null,
                        null, MediaStore.Video.Media.DATE_TAKEN + " DESC")
                column_index_data = cursor!!.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DATA)
                val listOfAllImages: MutableList<VideoModel> = ArrayList()
                while (cursor.moveToNext()) {
                    val galleryModel = VideoModel()
                    absolutePathOfImage = cursor.getString(column_index_data)
                    val ab = absolutePathOfImage.split("/").toTypedArray()
                    val length = ab.size
                    galleryModel.videoUrl = absolutePathOfImage
                    Log.d("cjhdshc", absolutePathOfImage)
                    galleryModel.imagename = ab[length - 1]
                    listOfAllImages.add(galleryModel)
                }

                // checking for Bookmark in the videolist;
                if (listImages != null && !listImages.isEmpty()) for (videoModel in listOfAllImages) {
                    for (videoModel_old in oldListImages) {
                        if (videoModel.imagename.equals(videoModel_old.imagename, ignoreCase = true) && videoModel_old.isBookmarked) {
                            videoModel.isBookmarked = true
                            oldListImages.remove(videoModel_old)
                            break
                        }
                    }
                }
                emitter.onSuccess(listOfAllImages)
            } catch (e: NullPointerException) {
                e.printStackTrace()
            }
        }
}
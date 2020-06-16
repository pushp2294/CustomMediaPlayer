package com.example.projectapplication1.UI

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.example.projectapplication1.R
import com.example.projectapplication1.UI.DisplayVideoAdapter.FirstVideoInterface
import com.example.projectapplication1.databinding.ActivityMainBinding
import com.example.projectapplication1.repository.VideoModel
import com.example.projectapplication1.util.CustomRecyclerView
import com.example.projectapplication1.util.SuccessErrorListner
import com.example.projectapplication1.viewmodel.MainActivityViewModel
import com.google.gson.Gson

class MainActivity : AppCompatActivity(), FirstVideoInterface, SuccessErrorListner {
    private var rv_custom: CustomRecyclerView? = null
    var progressBar: ProgressBar? = null
    var mViewModel: MainActivityViewModel? = null
    var videoModelMutableLiveData: MutableLiveData<List<VideoModel>>? = null
    var videoListModel: List<VideoModel?>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.statusBarColor = resources.getColor(android.R.color.transparent)
        val activityMainBinding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mViewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)
        rv_custom = activityMainBinding.rvCustom
        progressBar = activityMainBinding.progressCircular
        progressBar?.visibility = View.VISIBLE
        checkPermissions()
    }

    fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this@MainActivity,
                            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        2000)
            } else {
                videoModelMutableLiveData = mViewModel?.getVideoUrl(applicationContext)
                observeLiveData()
            }
        } else {
            videoModelMutableLiveData = mViewModel?.getVideoUrl(applicationContext)
            observeLiveData()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == 2000 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            videoModelMutableLiveData = mViewModel?.getVideoUrl(applicationContext)
            observeLiveData()
        } else {
            Toast.makeText(this, "Permission not granted", Toast.LENGTH_LONG).show()
        }
    }

    fun observeLiveData() {
        videoModelMutableLiveData?.observe(this, Observer<List<VideoModel?>?> { videoModels ->
            val successErrorListner = this@MainActivity as SuccessErrorListner
            mViewModel?.checkIfEverythingIsFineWithdata(videoModels, successErrorListner)
        })
    }

    override fun onSuccess(videoListModel: List<VideoModel?>?) {
        progressBar?.visibility = View.GONE
        this.videoListModel = videoListModel
        val linearLayoutManager = LinearLayoutManager(this)
        rv_custom?.setHasFixedSize(false)
        rv_custom?.isMotionEventSplittingEnabled = false
        val snapHelper: SnapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(rv_custom)
        rv_custom?.layoutManager = linearLayoutManager
        rv_custom?.setMediaObjects(videoListModel as List<VideoModel>)
        val displayVideoAdapter = DisplayVideoAdapter(this, videoListModel as List<VideoModel>)
        rv_custom?.adapter = displayVideoAdapter
    }

    override fun emptyDataList() {
        progressBar?.visibility = View.GONE
        Toast.makeText(this, "There is no any video to play.", Toast.LENGTH_LONG).show()
    }

    override fun onError() {
        progressBar?.visibility = View.GONE
        Toast.makeText(this, "Something went wrong. Please try again.", Toast.LENGTH_LONG).show()
    }

    override fun firstVideoInterface(parent: View?) {
        rv_custom?.Set_Player(0, parent)
    }

    override fun onPause() {
        super.onPause()
        val editor = getSharedPreferences(resources.getString(R.string.videoplayer_sharedData), Context.MODE_PRIVATE).edit()
        val gson = Gson()
        val videolist_flag = gson.toJson(videoListModel)
        editor.putString("videolist_flag", videolist_flag)
        editor.apply()
        if (CustomRecyclerView.privious_player != null && CustomRecyclerView.privious_player?.playWhenReady!!) {
            CustomRecyclerView.privious_player?.playWhenReady = false
        }
    }

    override fun onResume() {
        super.onResume()
        if (CustomRecyclerView.privious_player != null && CustomRecyclerView.privious_player?.playWhenReady!!) {
            CustomRecyclerView.privious_player?.playWhenReady = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (CustomRecyclerView.privious_player != null && CustomRecyclerView.privious_player?.playWhenReady!!) {
            CustomRecyclerView.privious_player = null
        }
    }
}
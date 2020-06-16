package com.example.projectapplication1.UI

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.projectapplication1.R
import com.example.projectapplication1.UI.DisplayVideoAdapter.MyHandler
import com.example.projectapplication1.databinding.DisplayVideoAdapterBinding
import com.example.projectapplication1.repository.VideoModel
import com.example.projectapplication1.util.ItemClickListener
import com.google.android.exoplayer2.ui.PlayerView

class DisplayVideoAdapter(var context: Context, var videoListModel: List<VideoModel>) : RecyclerView.Adapter<MyHandler>() {

    private var displayVideoAdapterBinding: DisplayVideoAdapterBinding? = null
    var firsttime = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHandler {
        displayVideoAdapterBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.display_video_adapter, parent, false)
        return MyHandler(displayVideoAdapterBinding!!)
    }

    override fun onBindViewHolder(holder: MyHandler, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int {
        return videoListModel.size
    }

    inner class MyHandler(displayVideoAdapterBinding: DisplayVideoAdapterBinding) : RecyclerView.ViewHolder(displayVideoAdapterBinding.root), ItemClickListener {
        @kotlin.jvm.JvmField
        var displayVideoAdapterBinding: DisplayVideoAdapterBinding = displayVideoAdapterBinding
        private var iv_bookmarked: ImageView = displayVideoAdapterBinding.ivBookmarked
        private var iv_unbookmarked: ImageView = displayVideoAdapterBinding.ivUnbookmarked
        var parent: View = displayVideoAdapterBinding.root
        var adapterposition = 0
        open var exoPlayer: PlayerView = displayVideoAdapterBinding.playerview
        fun onBind(position: Int) {
            displayVideoAdapterBinding.itemclicklistener = this
            adapterposition = position
            parent.tag = this

            //setting the View for playing first video when app is opened.
            if (firsttime) {
                firsttime = false
                val firstVideoInterface = context as FirstVideoInterface
                firstVideoInterface.firstVideoInterface(parent)
            }
            if (videoListModel[adapterposition].isBookmarked) {
                iv_bookmarked.visibility = View.VISIBLE
                iv_unbookmarked.visibility = View.GONE
            } else {
                iv_unbookmarked.visibility = View.VISIBLE
                iv_bookmarked.visibility = View.GONE
            }
        }

        override fun clickToBookmark() {
            videoListModel[adapterposition].isBookmarked = true
            iv_bookmarked.visibility = View.VISIBLE
            iv_unbookmarked.visibility = View.GONE
        }

        override fun cliclToRemoveBookmark() {
            videoListModel[adapterposition].isBookmarked = false
            iv_unbookmarked.visibility = View.VISIBLE
            iv_bookmarked.visibility = View.GONE
        }

    }

    interface FirstVideoInterface {
        fun firstVideoInterface(parent: View?)
    }

}
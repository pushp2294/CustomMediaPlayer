package com.example.projectapplication1.util

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectapplication1.UI.DisplayVideoAdapter.MyHandler
import com.example.projectapplication1.repository.VideoModel
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelection
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import java.util.*

class CustomRecyclerView : RecyclerView, Player.EventListener {
    private var playerView: PlayerView? = null
    var videoListModel: List<VideoModel> = ArrayList()

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    fun init(context: Context) {
        addOnScrollListener(object : OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == SCROLL_STATE_IDLE) {
                    if (privious_player != null) {
                        privious_player?.release()
                        privious_player = null
                    }
                    val position = (recyclerView.layoutManager as LinearLayoutManager?)!!.findFirstVisibleItemPosition()
                    Set_Player(position, null)
                } else {
                    Release_Privious_Player()
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }

    fun Set_Player(position: Int, parent: View?) {
        var child = layoutManager?.findViewByPosition(position)
        if (child == null) {
            child = parent
        }
        val myHandler = child?.tag as MyHandler
        playerView = myHandler.exoPlayer
        val adaptiveTrackSelection: TrackSelection.Factory = AdaptiveTrackSelection.Factory(DefaultBandwidthMeter())
        val player = ExoPlayerFactory.newSimpleInstance(
                DefaultRenderersFactory(context),
                DefaultTrackSelector(adaptiveTrackSelection),
                DefaultLoadControl())
        val defaultBandwidthMeter = DefaultBandwidthMeter()
        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(context, Util.getUserAgent(context, "Woovly"), defaultBandwidthMeter)
        val bucketVideo = videoListModel[position].videoUrl
        val videoSource: MediaSource = ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(bucketVideo))
        player.prepare(videoSource)
        playerView?.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
        player.repeatMode = Player.REPEAT_MODE_ALL
        playerView?.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
        playerView?.player = player
        player.playWhenReady = true
        privious_player = player
    }

    fun Release_Privious_Player() {
        if (privious_player != null) {
            privious_player?.release()
        }
    }

    fun setMediaObjects(mediaObjects: List<VideoModel>) {
        videoListModel = mediaObjects
    }

    override fun onTimelineChanged(timeline: Timeline, manifest: Any?, reason: Int) {}
    override fun onTracksChanged(trackGroups: TrackGroupArray, trackSelections: TrackSelectionArray) {}
    override fun onLoadingChanged(isLoading: Boolean) {}
    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {}
    override fun onRepeatModeChanged(repeatMode: Int) {}
    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {}
    override fun onPlayerError(error: ExoPlaybackException) {}
    override fun onPositionDiscontinuity(reason: Int) {}
    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {}
    override fun onSeekProcessed() {}

    companion object {
        @JvmField
        var privious_player: SimpleExoPlayer? = null
    }
}
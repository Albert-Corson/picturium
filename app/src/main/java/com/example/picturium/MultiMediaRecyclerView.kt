package com.example.picturium

import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.picturium.adapters.MultiMediaAdapter
import com.example.picturium.models.DetailsItem
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

class MultiMediaRecyclerView : RecyclerView {
    private enum class VolumeState {
        ON, OFF
    }

    // ui
    private var videoSurfaceView: PlayerView
    private var videoPlayer: SimpleExoPlayer? = null
    private var thumbnail: ImageView? = null
    private var volumeControl: ImageView? = null
    private var progressBar: ProgressBar? = null
    private var viewHolderParent: View? = null
    private var frameLayout: FrameLayout? = null

    // vars
    private var items: ArrayList<DetailsItem> = ArrayList()
    private var videoSurfaceDefaultHeight = 0
    private var screenDefaultHeight = 0
    private val appContext: Context = context.applicationContext
    private var playPosition = -1
    private var isVideoViewAdded = false
    private var requestManager: RequestManager? = null

    companion object {
        // controlling playback state
        private var volumeState: VolumeState? = null
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    init {
        val point = Point()
        val windowManager = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            val metric = windowManager.currentWindowMetrics
            point.y = metric.bounds.width()
            point.x = metric.bounds.height()
        } else {
            windowManager.defaultDisplay.getSize(point)
        }
        videoSurfaceDefaultHeight = point.x
        screenDefaultHeight = point.y

        videoSurfaceView = PlayerView(this.appContext)
        videoSurfaceView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM

        // Create the player
        val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory()
        val trackSelector = DefaultTrackSelector(appContext, videoTrackSelectionFactory)
        videoPlayer = SimpleExoPlayer.Builder(context).setTrackSelector(trackSelector).build()

        // Bind the player to the view.
        videoSurfaceView.useController = false
        videoSurfaceView.player = videoPlayer
        setVolumeControl(VolumeState.ON)

        addOnScrollListener(object : OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == SCROLL_STATE_IDLE) {
                    playVideo(!recyclerView.canScrollVertically(1))
                }
            }
        })

        addOnChildAttachStateChangeListener(object : OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(view: View) {}
            override fun onChildViewDetachedFromWindow(view: View) {
                if (viewHolderParent != null && viewHolderParent == view)
                    resetVideoView()
            }
        })

        videoPlayer!!.addListener(object : Player.EventListener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> progressBar?.visibility = View.VISIBLE
                    Player.STATE_ENDED -> videoPlayer?.seekTo(0)
                    Player.STATE_READY -> {
                        progressBar?.visibility = View.GONE
                        thumbnail?.visibility = View.INVISIBLE
                        if (!isVideoViewAdded)
                            addVideoView()
                    }
                    else -> {
                    }
                }
            }
        })
    }

    fun playVideo(isEndOfList: Boolean) {
        val targetPosition: Int = getTargetPosition(isEndOfList)

        println(targetPosition)
        // video is already playing so return
        if (targetPosition == -1 || targetPosition == playPosition)
            return

        // set the position of the list-item that is to be played
        playPosition = targetPosition

        // remove any old surface views from previously playing videos
        videoSurfaceView.visibility = View.VISIBLE
        removeVideoView(videoSurfaceView)
        val manager = layoutManager as LinearLayoutManager
        val child = manager.findViewByPosition(targetPosition)
        val holder: MultiMediaAdapter.ViewHolder? = child?.tag as MultiMediaAdapter.ViewHolder?
        if (holder == null) {
            playPosition = -1
            return
        }
        if (items[targetPosition].image?.isAnimated != true)
            return
        thumbnail = holder.thumbnail
        progressBar = holder.progressBar
        volumeControl = holder.volumeControl
        viewHolderParent = holder.itemView
        requestManager = holder.requestManager
        frameLayout = holder.mediaContainer
        videoSurfaceView.player = videoPlayer
        viewHolderParent!!.setOnClickListener { toggleVolume() }
        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(appContext, Util.getUserAgent(appContext, "Picturium"))
        val mediaUrl: String? = items[targetPosition].image?.link
        if (mediaUrl != null) {
            val mediaItem = MediaItem.fromUri(mediaUrl)
            val videoSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
            videoPlayer?.setMediaSource(videoSource)
            videoPlayer?.prepare()
            videoPlayer?.playWhenReady = true
        }
    }

    private fun getTargetPosition(isEndOfList: Boolean): Int {
        if (isEndOfList)
            return findLastVisibleItemPosition()

        val startPosition = findFirstVisibleItemPosition()
        if (startPosition == -1)
            return -1
        val endPosition = findNextVisibleItemPosition(startPosition)
        // if there is more than 2 list-items on the screen, set the difference to be 1
        if (endPosition == -1)
            return startPosition

        // if there is more than 1 list-item on the screen
        val startPositionVideoHeight = getVisibleVideoSurfaceHeight(startPosition)
        val endPositionVideoHeight = getVisibleVideoSurfaceHeight(endPosition)
        return if (startPositionVideoHeight >= endPositionVideoHeight) startPosition else endPosition
    }

    private fun findFirstVisibleItemPosition(): Int {
        val manager = layoutManager as LinearLayoutManager

        for (i in 0 until items.size) {
            val child = manager.findViewByPosition(i) ?: continue
            if (items[i].type != DetailsItem.Type.ITEM
                || (!manager.isViewPartiallyVisible(child, false, true)
                        && !manager.isViewPartiallyVisible(child, true, true))
            )
                continue
            return i
        }
        return -1
    }

    private fun findLastVisibleItemPosition(): Int {
        val manager = layoutManager as LinearLayoutManager

        for (i in items.size - 1 downTo 0) {
            val child = manager.findViewByPosition(i) ?: continue
            if (items[i].type != DetailsItem.Type.ITEM
                || (!manager.isViewPartiallyVisible(child, false, true)
                        && !manager.isViewPartiallyVisible(child, true, true))
            )
                continue
            return i
        }
        return -1
    }

    private fun findNextVisibleItemPosition(start: Int): Int {
        val manager = layoutManager as LinearLayoutManager

        for (i in start + 1 until items.size) {
            val child = manager.findViewByPosition(i) ?: continue
            if (items[i].type != DetailsItem.Type.ITEM
                || (!manager.isViewPartiallyVisible(child, false, true)
                        && !manager.isViewPartiallyVisible(child, true, true))
            )
                continue
            return i
        }
        return -1
    }

    private fun getVisibleVideoSurfaceHeight(playPosition: Int): Int {
        val manager = layoutManager as LinearLayoutManager
        val child = manager.findViewByPosition(playPosition) ?: return 0
        val location = IntArray(2)

        child.getLocationInWindow(location)
        return if (location[1] < 0) {
            location[1] + videoSurfaceDefaultHeight
        } else {
            screenDefaultHeight - location[1]
        }
    }

    private fun removeVideoView(videoView: PlayerView?) {
        val parent = videoView?.parent as ViewGroup?
        val index = parent?.indexOfChild(videoView)
        if (index != null && index >= 0) {
            parent.removeViewAt(index)
            isVideoViewAdded = false
            viewHolderParent?.setOnClickListener(null)
            thumbnail?.visibility = View.VISIBLE
        }
    }

    private fun addVideoView() {
        if (frameLayout == null)
            return
        isVideoViewAdded = true
        frameLayout!!.addView(videoSurfaceView)
        videoSurfaceView.requestFocus()
        videoSurfaceView.visibility = View.VISIBLE
        videoSurfaceView.alpha = 1f
    }

    private fun resetVideoView() {
        if (isVideoViewAdded) {
            removeVideoView(videoSurfaceView)
            playPosition = -1
            videoSurfaceView.visibility = View.INVISIBLE
        }
    }

    private fun toggleVolume() {
        if (videoPlayer != null) {
            setVolumeControl(if (volumeState == VolumeState.OFF) VolumeState.ON else VolumeState.OFF)
        }
    }

    private fun setVolumeControl(state: VolumeState) {
        volumeState = state
        videoPlayer?.volume = if (state == VolumeState.OFF) 0f else 1f
        animateVolumeControl()
    }

    private fun animateVolumeControl() {
        if (volumeControl != null) {
            volumeControl!!.bringToFront()
            if (volumeState == VolumeState.OFF) {
                requestManager?.load(R.drawable.ic_volume_off)?.into(volumeControl!!)
            } else if (volumeState == VolumeState.ON) {
                requestManager?.load(R.drawable.ic_volume_on)?.into(volumeControl!!)
            }
            volumeControl!!.animate().cancel()
            volumeControl!!.alpha = 1f
            volumeControl!!.animate()
                .alpha(0f)
                .setDuration(600).startDelay = 1000
        }
    }

    fun releasePlayer() {
        if (videoPlayer != null) {
            videoPlayer!!.release()
            videoPlayer = null
        }
        viewHolderParent = null
    }

    fun pausePlayback() {
        videoPlayer?.pause()
    }

    fun resumePlayback() {
        videoPlayer?.play()
    }

    fun setImages(items: ArrayList<DetailsItem>) {
        this.items = items
    }

    fun startPlayback() {
        playVideo(false)
    }
}

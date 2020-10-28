package com.example.picturium.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.picturium.R
import com.example.picturium.models.DetailsItem
import kotlinx.android.synthetic.main.details_page_media_item.view.*

class MultiMediaAdapter(private val _requestManager: RequestManager, private val items: ArrayList<DetailsItem>) : RecyclerView.Adapter<MultiMediaAdapter.ViewHolder>() {
    override fun getItemViewType(position: Int): Int {
        return items[position].type.value
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(items[position])
    }

    inner class ViewHolder(private val parent: View) : RecyclerView.ViewHolder(parent) {

        var mediaContainer: FrameLayout? = null
        var thumbnail: ImageView? = null
        var volumeControl: ImageView? = null
        var progressBar: ProgressBar? = null
        val requestManager
            get() = _requestManager

        init {
            parent.tag = this
            mediaContainer = parent.findViewById(R.id.mediaItem_flVideoContainer)
            thumbnail = parent.findViewById(R.id.mediaItem_ivThumbnail)
            volumeControl = parent.findViewById(R.id.mediaItem_ivVolumeControl)
            progressBar = parent.findViewById(R.id.mediaItem_pbLoading)
        }

        fun onBind(item: DetailsItem) {
            if (item.type == DetailsItem.Type.FOOTER || item.type == DetailsItem.Type.HEADER) {
                (parent as TextView).text = item.text ?: ""
            } else {
                requestManager.load(item.image!!.link)
                    .placeholder(R.color.primaryDark)
                    .fallback(R.drawable.error)
                    .transition(DrawableTransitionOptions.withCrossFade(250))
                    .into(parent.mediaItem_ivThumbnail)
                parent.mediaItem_ivThumbnail.visibility = View.VISIBLE
            }
        }
    }
}

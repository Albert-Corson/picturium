package com.example.picturium.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.picturium.R
import com.example.picturium.models.DetailsItem
import com.example.picturium.models.Image
import kotlinx.android.synthetic.main.details_page_media_item.view.*

class ImageListAdapter(private val glideRequest: RequestManager, private val items: ArrayList<DetailsItem>) : RecyclerView.Adapter<ImageListAdapter.ViewHolder>() {
    override fun getItemViewType(position: Int): Int {
        return items[position].type.value
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return ViewHolder(view, glideRequest)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(items[position])
    }

    inner class ViewHolder(private val parent: View, private val glideRequest: RequestManager) : RecyclerView.ViewHolder(parent) {

        fun onBind(item: DetailsItem) {
            if (item.type == DetailsItem.Type.FOOTER || item.type == DetailsItem.Type.HEADER) {
                _bindHeaderOrFooter(item.text ?: "")
            } else {
                val image = item.image!!
                _bindThumbnail(image.link)
                if (image.isAnimated == true) {
                    _displayVideo(parent, image)
                }
            }
        }

        private fun _bindHeaderOrFooter(text: String) {
            (parent as TextView).text = text
        }

        private fun _bindThumbnail(link: String?) {
            parent.mediaItem_ivThumbnail.visibility = View.VISIBLE
            glideRequest
                .load(link)
                .placeholder(R.color.primaryDark)
                .fallback(R.drawable.error)
                .transition(DrawableTransitionOptions.withCrossFade(250))
                .into(parent.mediaItem_ivThumbnail)
        }

        private fun _displayVideo(view: View, image: Image) {

        }
    }
}

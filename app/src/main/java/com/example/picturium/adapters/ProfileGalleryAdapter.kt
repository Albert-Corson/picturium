package com.example.picturium.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.picturium.R
import com.example.picturium.models.Submission
import kotlinx.android.synthetic.main.profile_image.view.*
import kotlinx.coroutines.*

class ProfileGalleryAdapter(private var _gallery: List<Submission>, private val _coroutineScope: CoroutineScope) :
    RecyclerView.Adapter<ProfileGalleryAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    fun setGallery(gallery: List<Submission>) {
        _gallery = gallery
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.profile_image, parent, false)
        val params = view.layoutParams
        params.height = (parent.measuredWidth.toFloat() / 3f).toInt()
        view.layoutParams = params
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        _coroutineScope.launch(Dispatchers.IO) {
            val link = _gallery[position].getCoverImage()?.link

            withContext(Dispatchers.Main) {
                Glide.with(holder.itemView)
                    .load(link)
                    .transition(DrawableTransitionOptions.withCrossFade(1000))
                    .into(holder.itemView.profile_ivGalleryImage)
            }
        }
    }

    override fun getItemCount(): Int {
        return _gallery.size
    }
}
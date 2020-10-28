package com.example.picturium.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.picturium.R
import com.example.picturium.models.Submission
import kotlinx.android.synthetic.main.profile_page_image_item.view.*
import kotlinx.android.synthetic.main.upload_page_image_item.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class UploadAdapter() :
    RecyclerView.Adapter<UploadAdapter.UploadViewHolder>() {

    private var _uris: MutableList<Uri> = mutableListOf()

    inner class UploadViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    }

    fun setUris(uris: MutableList<Uri>) {
        _uris = uris
        notifyDataSetChanged()
    }

    fun getUris(): List<Uri> {
        return _uris
    }

    fun addUri(uri: Uri) {
        _uris.add(uri)
        notifyDataSetChanged()
    }

    fun delItem(position: Int) {
        _uris.removeAt(position)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UploadViewHolder {
        return UploadViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.upload_page_image_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: UploadViewHolder, position: Int) {
        holder.itemView.upload_ibDelItem.setOnClickListener { delItem(position) }
        Glide.with(holder.itemView)
            .load(_uris[position])
            .transition(DrawableTransitionOptions.withCrossFade(1000))
            .into(holder.itemView.upload_ivGalleryImage)
    }

    override fun getItemCount(): Int {
        return _uris.size
    }
}
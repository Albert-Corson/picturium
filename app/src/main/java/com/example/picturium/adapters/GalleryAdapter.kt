package com.example.picturium.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.picturium.R
import com.example.picturium.databinding.ItemThreadBinding
import com.example.picturium.models.ThreadData

class GalleryAdapter(private var mData: List<ThreadData>) : RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {

    fun setData(list : List<ThreadData>) {
        mData = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val binding: ItemThreadBinding = ItemThreadBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return GalleryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        val currentThread = mData[position]

        holder.bind(currentThread)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    inner class GalleryViewHolder(private val binding: ItemThreadBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(thread: ThreadData) {
            var url: ThreadData.Image? = null
            if (thread.cover != null)
                url = thread.images?.find { it.id == thread.cover }
            if (url != null)
                binding.apply {
                    Glide.with(itemView)
                        .load(url.link)
                        .fitCenter()
                        .error(R.drawable.error)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(rowImg)
                    textViewTitle.text = thread.title
                    textViewUpVote.text = thread.ups.toString()
                    textViewDownVote.text = thread.downs.toString()
                }
            else {
                binding.rowImg.visibility = View.GONE
                binding.textViewDownVote.visibility = View.GONE
                binding.textViewUpVote.visibility = View.GONE
                binding.textViewTitle.visibility = View.GONE
            }
        }
    }
}
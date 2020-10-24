package com.example.picturium.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.picturium.R
import com.example.picturium.databinding.ItemThreadBinding
import com.example.picturium.models.Submission
import kotlinx.android.synthetic.main.item_thread.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GalleryAdapter(private val listener: OnItemClickListener) :
    PagingDataAdapter<Submission, GalleryAdapter.GalleryViewHolder>(PHOTO_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val binding: ItemThreadBinding = ItemThreadBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return GalleryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        getItem(position)?.let { currentSubmission ->
            val width = currentSubmission.coverWidth
            val height = currentSubmission.coverHeight

            if (width != null && height != null) {
                val ratio: Float = holder.itemView.width / width.toFloat()
                holder.itemView.row_img.layoutParams.height = (height.toFloat() * ratio).toInt()
            }
            holder.bind(currentSubmission)
        }
    }

    inner class GalleryViewHolder(private val binding: ItemThreadBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = this.bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION)
                    getItem(position)?.let { it1 -> listener.onItemClick(it1) }
            }
        }

        fun bind(submission: Submission) {
            coroutineScope.launch(Dispatchers.IO) {
                val url = submission.getCoverImage()?.link
                binding.apply {
                    withContext(Dispatchers.Main) {
                        Glide.with(itemView)
                            .load(url)
                            .placeholder(ColorDrawable(Color.BLACK))
                            .fitCenter()
                            .error(R.drawable.error)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(rowImg)
                        textViewTitle.text = submission.title
                        textViewUpVote.text = submission.upVotes.toString()
                        textViewDownVote.text = submission.downVotes.toString()
                    }
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(submission: Submission)
    }

    companion object {
        private val PHOTO_COMPARATOR = object : DiffUtil.ItemCallback<Submission>() {
            override fun areItemsTheSame(oldItem: Submission, newItem: Submission): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Submission, newItem: Submission): Boolean {
                return oldItem == newItem
            }
        }
    }
}
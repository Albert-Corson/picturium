package com.example.picturium.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.picturium.R

class ViewPagerAdapter(val galleries: List<Pair<ProfileGalleryAdapter, GridLayoutManager>>) : RecyclerView.Adapter<ViewPagerAdapter.ViewHolder>() {
    inner class ViewHolder(recyclerView: View) : RecyclerView.ViewHolder(recyclerView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.profile_page_recylcer_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recyclerView = holder.itemView as RecyclerView
        recyclerView.adapter = galleries[position].first
        recyclerView.layoutManager = galleries[position].second
    }

    override fun getItemCount(): Int {
        return galleries.size
    }
}
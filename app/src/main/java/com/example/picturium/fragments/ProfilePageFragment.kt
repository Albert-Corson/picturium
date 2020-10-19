package com.example.picturium.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.picturium.R
import com.example.picturium.User
import com.example.picturium.adapter.ProfileGalleryAdapter
import kotlinx.android.synthetic.main.fragment_profile_page.*
import kotlinx.coroutines.*

class ProfilePageFragment : Fragment(R.layout.fragment_profile_page) {
    private lateinit var _galleryAdapter: ProfileGalleryAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        TO_REMOVE.setOnClickListener { loginBtnOnClick() }

        _galleryAdapter = ProfileGalleryAdapter(emptyList())
        profileGallery_rvGallery.adapter = _galleryAdapter
        profileGallery_rvGallery.layoutManager = GridLayoutManager(context, 3)
    }

    override fun onResume() {
        super.onResume()
        val loginJob = GlobalScope.launch(Dispatchers.IO) {
            User.handleLoginCallback(requireActivity().intent.data)
        }
        lifecycleScope.launch {
            loginJob.join()
            if (!User.isLoggedIn())
                cancel()

            lifecycleScope.launch(Dispatchers.IO) {
                if (User.publicData?.favorites == null)
                    User.loadFavorites()
                withContext(Dispatchers.Main) {
                    _galleryAdapter.setGallery(User.publicData?.favorites ?: emptyList())
                }
            }

            withContext(Dispatchers.Main) {
                Glide.with(this@ProfilePageFragment)
                    .load(User.publicData?.profilePicture)
                    .fallback(R.drawable.ic_dflt_profile)
                    .circleCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(activityProfile_ivProfilePicture)
            }
        }
    }

    fun loginBtnOnClick() {
        if (!User.isLoggedIn())
            User.redirectToLogin(requireContext())
    }
}
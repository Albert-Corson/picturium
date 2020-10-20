package com.example.picturium.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.picturium.R
import com.example.picturium.User
import com.example.picturium.adapter.ProfileGalleryAdapter
import com.example.picturium.adapter.ViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_profile_page.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfilePageFragment : Fragment(R.layout.fragment_profile_page) {
    private lateinit var _favoritesAdapter: ProfileGalleryAdapter
    private lateinit var _submissionsAdapter: ProfileGalleryAdapter
    private lateinit var _viewPagerAdapter: ViewPagerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profile_btnLogin.setOnClickListener { _loginBtnOnClick() }
        profile_ibLogout.setOnClickListener { _logoutBtnOnClick() }
        profile_ibClose.setOnClickListener { _closeBtnOnClick() }

        _favoritesAdapter = ProfileGalleryAdapter(emptyList())
        _submissionsAdapter = ProfileGalleryAdapter(emptyList())
        _initTabbedLayoutViewPager()
    }

    private fun _initTabbedLayoutViewPager() {
        _viewPagerAdapter = ViewPagerAdapter(listOf(
            Pair(_favoritesAdapter, GridLayoutManager(context, 3)),
            Pair(_submissionsAdapter, GridLayoutManager(context, 3))
        ))

        profile_vpGalleries.adapter = _viewPagerAdapter

        TabLayoutMediator(profile_tbGalleries, profile_vpGalleries) { tab, position ->
            when (position) {
                0 -> tab.setIcon(R.drawable.ic_favorite)
                1 -> tab.setIcon(R.drawable.ic_gallery)
            }
        }.attach()
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch(Dispatchers.IO) {
            User.handleLoginCallback(requireActivity().intent.data)
            if (User.isLoggedIn()) {
                _refreshFavorites()
                _refreshSubmissions()
                withContext(Dispatchers.Main) {
                    Glide.with(requireContext())
                        .load(User.publicData?.profilePicture)
                        .fallback(R.drawable.ic_dflt_profile)
                        .circleCrop()
                        .into(profile_ivProfilePicture)
                    profile_ibLogout.visibility = View.VISIBLE
                    profile_btnLogin.visibility = View.GONE
                    profile_tvUsername.text = User.publicData?.username
                }
            } else {
                profile_ibLogout.visibility = View.GONE
                profile_btnLogin.visibility = View.VISIBLE
            }
        }
    }

    private fun _closeBtnOnClick() {
        requireActivity().onBackPressed();
    }

    private fun _loginBtnOnClick() {
        if (!User.isLoggedIn())
            User.redirectToLogin(requireContext())
    }

    private fun _logoutBtnOnClick() {
        if (!User.isLoggedIn())
            return
        User.logout()
        profile_ivProfilePicture.setImageResource(R.drawable.ic_dflt_profile)
        _favoritesAdapter.setGallery(emptyList())
        _submissionsAdapter.setGallery(emptyList())
        profile_ibLogout.visibility = View.GONE
        profile_btnLogin.visibility = View.VISIBLE
        profile_tvUsername.text = ""
    }

    private fun _refreshFavorites() {
        lifecycleScope.launch(Dispatchers.IO) {
            User.loadFavorites()
            withContext(Dispatchers.Main) {
                _favoritesAdapter.setGallery(User.publicData?.favorites ?: emptyList())
            }
        }
    }

    private fun _refreshSubmissions() {
        lifecycleScope.launch(Dispatchers.IO) {
            User.loadSubmissions()
            withContext(Dispatchers.Main) {
                _submissionsAdapter.setGallery(User.publicData?.submissions ?: emptyList())
            }
        }
    }
}
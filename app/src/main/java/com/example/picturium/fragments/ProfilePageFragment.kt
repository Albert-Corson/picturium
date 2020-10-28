package com.example.picturium.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.picturium.R
import com.example.picturium.adapters.ProfileGalleryAdapter
import com.example.picturium.adapters.ViewPagerAdapter
import com.example.picturium.models.Submission
import com.example.picturium.models.UserData
import com.example.picturium.viewmodels.UserViewModel
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_profile_page.*

class ProfilePageFragment : Fragment(R.layout.fragment_profile_page), ProfileGalleryAdapter.OnItemClickListener {
    private lateinit var _favoritesAdapter: ProfileGalleryAdapter
    private lateinit var _submissionsAdapter: ProfileGalleryAdapter
    private lateinit var _viewPagerAdapter: ViewPagerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profile_btnLogin.setOnClickListener { _loginBtnOnClick() }
        profile_ibLogout.setOnClickListener { _logoutBtnOnClick() }
        profile_ibClose.setOnClickListener { _closeBtnOnClick() }

        _favoritesAdapter = ProfileGalleryAdapter(this, lifecycleScope)
        _submissionsAdapter = ProfileGalleryAdapter(this, lifecycleScope)
        _initTabbedLayoutViewPager()

        UserViewModel.favorites.observe(viewLifecycleOwner) {
            _favoritesAdapter.setGallery(it)
        }
        UserViewModel.submissions.observe(viewLifecycleOwner) {
            _submissionsAdapter.setGallery(it)
        }
        UserViewModel.publicData.observe(viewLifecycleOwner) {
            _loginStatusChanged(it)
        }
    }

    override fun onResume() {
        super.onResume()
        if (!UserViewModel.isLoggedIn())
            return
        UserViewModel.publicData.postValue(UserViewModel.publicData.value)
        UserViewModel.loadFavorites()
        UserViewModel.loadSubmissions()
    }

    override fun onItemClick(submission: Submission) {
        val action = ProfilePageFragmentDirections.actionProfilePageFragmentToDetailsPageFragment(submission.id ?: "")

        findNavController().navigate(action)
    }

    private fun _initTabbedLayoutViewPager() {
        _viewPagerAdapter = ViewPagerAdapter(
            listOf(
                Pair(_favoritesAdapter, GridLayoutManager(context, 3)),
                Pair(_submissionsAdapter, GridLayoutManager(context, 3))
            )
        )

        profile_vpGalleries.adapter = _viewPagerAdapter

        TabLayoutMediator(profile_tbGalleries, profile_vpGalleries) { tab, position ->
            when (position) {
                0 -> tab.setIcon(R.drawable.ic_favorite)
                1 -> tab.setIcon(R.drawable.ic_gallery)
            }
        }.attach()
    }

    private fun _loginStatusChanged(userData: UserData?) {
        _setProfilePicture(userData?.profilePicture)
        if (userData == null) {
            UserViewModel.favorites.value = emptyList()
            UserViewModel.submissions.value = emptyList()
            profile_btnLogin.visibility = View.VISIBLE
            profile_ibLogout.visibility = View.GONE
            profile_tvUsername.visibility = View.GONE
        } else {
            UserViewModel.loadSubmissions()
            UserViewModel.loadFavorites()
            profile_btnLogin.visibility = View.GONE
            profile_ibLogout.visibility = View.VISIBLE
            profile_tvUsername.visibility = View.VISIBLE
            profile_tvUsername.text = userData.username
        }
    }

    private fun _setProfilePicture(profilePicture: String?) {
        Glide.with(requireContext())
            .load(profilePicture)
            .fallback(R.drawable.ic_dflt_profile)
            .circleCrop()
            .into(profile_ivProfilePicture)
    }

    private fun _closeBtnOnClick() {
        requireActivity().onBackPressed()
    }

    private fun _loginBtnOnClick() {
        if (!UserViewModel.isLoggedIn())
            UserViewModel.redirectToLogin(requireContext())
    }

    private fun _logoutBtnOnClick() {
        if (!UserViewModel.isLoggedIn())
            return
        UserViewModel.logout()
        profile_ivProfilePicture.setImageResource(R.drawable.ic_dflt_profile)
        _favoritesAdapter.setGallery(emptyList())
        _submissionsAdapter.setGallery(emptyList())
        profile_ibLogout.visibility = View.GONE
        profile_btnLogin.visibility = View.VISIBLE
        profile_tvUsername.text = ""
    }
}
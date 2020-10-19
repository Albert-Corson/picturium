package com.example.picturium.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.picturium.FilterButtonsManager
import com.example.picturium.R
import com.example.picturium.SearchBarQueryListener
import com.example.picturium.User
import kotlinx.android.synthetic.main.fragment_home_page.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomePageFragment : Fragment(R.layout.fragment_home_page) {
    private lateinit var _filterBtnManager: FilterButtonsManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _filterBtnManager = FilterButtonsManager(this.requireActivity())

        topBar_svSearchBar.setOnQueryTextListener(SearchBarQueryListener(topBar_svSearchBar))
        topBar_ibProfile.setOnClickListener { profileBtnOnClick() }
        topBar_ibUpload.setOnClickListener { uploadBtnOnClick() }

        GlobalScope.launch(Dispatchers.IO) {
            User.init(this@HomePageFragment.requireActivity().applicationContext)
            withContext(Dispatchers.Main) {
                _setProfileBtnImage()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        _setProfileBtnImage()
    }

    private fun _setProfileBtnImage() {
        Glide.with(this)
            .load(User.publicData?.profilePicture)
            .circleCrop()
            .into(topBar_ibProfile)
    }

    fun profileBtnOnClick() {
        val action = HomePageFragmentDirections.actionHomeFragmentToProfilePageFragment()
        findNavController().navigate(action)
    }

    fun uploadBtnOnClick() {
        Toast.makeText(this.context, "Upload", Toast.LENGTH_LONG).show()
    }
}
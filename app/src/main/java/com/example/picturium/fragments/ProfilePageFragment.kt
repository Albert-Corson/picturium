package com.example.picturium.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.picturium.R
import com.example.picturium.User
import kotlinx.android.synthetic.main.fragment_profile_page.*

class ProfilePageFragment : Fragment(R.layout.fragment_profile_page) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        TO_REMOVE.setOnClickListener { loginBtnOnClick() }
    }

    override fun onResume() {
        super.onResume()
        User.handleLoginCallback(requireActivity().intent.data)
    }

    fun loginBtnOnClick() {
        if (!User.isLoggedIn())
            User.redirectToLogin(requireContext())
    }
}
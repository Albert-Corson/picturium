package com.example.picturium

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation.findNavController
import com.example.picturium.fragments.HomePageFragmentDirections
import com.example.picturium.viewmodels.UserViewModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        if (UserViewModel.handleLoginCallback(intent.data)) {
            val action = HomePageFragmentDirections.actionHomeFragmentToProfilePageFragment()
            findNavController(this, R.id.fcvHomePage).navigate(action)
        }
    }
}
package com.example.picturium

import android.app.Application
import android.widget.Toast
import com.example.picturium.viewmodels.UserViewModel

class Picturium : Application() {
    companion object {
        private lateinit var _toast: Toast

        fun toastConnectionError() {
            _toast.show()
        }
    }

    override fun onCreate() {
        super.onCreate()
        _toast = Toast.makeText(applicationContext, "Can't connect to Imgur", Toast.LENGTH_SHORT)
        UserViewModel.init(applicationContext)
    }
}
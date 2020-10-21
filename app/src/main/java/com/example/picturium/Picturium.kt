package com.example.picturium

import android.app.Application
import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.runBlocking

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
        runBlocking {
            User.init(applicationContext)
        }
    }
}
package com.example.picturium

import android.app.Application
import kotlinx.coroutines.runBlocking

class Picturium : Application() {
    override fun onCreate() {
        super.onCreate()
        runBlocking {
            User.init(applicationContext)
        }
    }
}
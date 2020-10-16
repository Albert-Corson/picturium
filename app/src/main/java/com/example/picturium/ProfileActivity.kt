package com.example.picturium

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
    }

    public fun loginBtnOnClick(view: View) {
        if (!User.isLoggedIn())
            ImgurAPI.redirectToLogin(this)
    }

    override fun onResume() {
        super.onResume()
        ImgurAPI.handleLoginCallback(intent.data)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.fade_in, R.anim.slide_out_left)
    }
}

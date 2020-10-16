package com.example.picturium

import android.content.Context
import android.content.Intent
import android.net.Uri

object ImgurAPI {
    private val _loginUrl: Uri = Uri.parse("https://api.imgur.com/oauth2/authorize").buildUpon()
        .appendQueryParameter("client_id", BuildConfig.CLIENT_ID)
        .appendQueryParameter("response_type", "token")
        .build()

    fun redirectToLogin(context: Context) {
        val intent: Intent = Intent(Intent.ACTION_VIEW, _loginUrl)
        context.startActivity(intent)
    }

    fun handleLoginCallback(uri: Uri?) {
        val params = uri?.encodedFragment

        if (params == null || !uri.toString().startsWith(BuildConfig.CALLBACK_URL))
            return
        val parsed: Uri = Uri.parse("_://?$params")
        User.login(parsed)
    }
}
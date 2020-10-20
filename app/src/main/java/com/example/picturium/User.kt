package com.example.picturium

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import com.example.picturium.api.ImgurAPI
import com.example.picturium.models.UserData
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object User {
    private lateinit var _context: Context
    private lateinit var _cache: SharedPreferences
    var publicData: UserData? = null
    var accessToken: String? = null
    var refreshToken: String? = null

    suspend fun init(context: Context) {
        _context = context
        _cache = _context.getSharedPreferences("userCache", Context.MODE_PRIVATE)

        _loadFromCache()
        if (!isLoggedIn())
            return

        val tokenCheck = ImgurAPI.safeCall {
            ImgurAPI.instance.checkAccessToken()
        }

        if (tokenCheck is ImgurAPI.CallResult.NetworkError) {
            TODO("Toast no internet")
            return
        } else if (tokenCheck is ImgurAPI.CallResult.ErrorResponse && !_refreshAccessToken()) {
            return
        }

        if (_loadPublicData()) {
            save()
        } else {
            logout()
        }
    }

    suspend fun _refreshAccessToken(): Boolean {
        val tokenRefresh = ImgurAPI.safeCall {
            ImgurAPI.instance.refreshAccessToken()
        }
        when (tokenRefresh) {
            is ImgurAPI.CallResult.NetworkError -> TODO("Toast no internet")
            is ImgurAPI.CallResult.ErrorResponse -> logout()
            is ImgurAPI.CallResult.SuccessResponse -> {
                accessToken = tokenRefresh.body.data.accessToken
                return true
            }
        }
        return false
    }

    fun _login(credentialsUri: Uri) {
        val username: String? = credentialsUri.getQueryParameter("account_username")
        accessToken = credentialsUri.getQueryParameter("access_token")
        refreshToken = credentialsUri.getQueryParameter("refresh_token")
        if (username.isNullOrEmpty() || accessToken.isNullOrEmpty() || refreshToken.isNullOrEmpty()) {
            logout()
            return
        }
        GlobalScope.launch(Dispatchers.IO) {
            if (_loadPublicData()) {
                save()
            } else {
                logout()
            }
        }
    }

    private fun _loadFromCache() {
        if (!_cache.contains("publicData")) {
            logout()
            return
        }
        publicData = Gson().fromJson(_cache.getString("publicData", ""), UserData::class.java)
        accessToken = _cache.getString("accessToken", null)
        refreshToken = _cache.getString("refreshToken", null)
    }

    private suspend fun _loadPublicData(): Boolean {
        val res = ImgurAPI.safeCall {
            ImgurAPI.instance.getUserData()
        }
        when (res) {
            is ImgurAPI.CallResult.NetworkError -> TODO("Toast no internet")
            is ImgurAPI.CallResult.ErrorResponse -> logout()
            is ImgurAPI.CallResult.SuccessResponse -> {
                publicData = res.body.data
                return true
            }
        }
        return false
    }

    fun redirectToLogin(context: Context) {
        val intent: Intent = Intent(Intent.ACTION_VIEW, Uri.parse(ImgurAPI.LOGIN_URL))
        context.startActivity(intent)
    }

    fun handleLoginCallback(uri: Uri?) {
        val params = uri?.encodedFragment

        if (params == null || !uri.toString().startsWith(BuildConfig.CALLBACK_URL))
            return
        val parsed: Uri = Uri.parse("_://?$params")
        User._login(parsed)
    }

    fun logout() {
        _cache.edit().clear().apply()
        publicData = null
        accessToken = null
        refreshToken = null
    }

    fun save() {
        val editor: SharedPreferences.Editor = _cache.edit()

        editor.putString("publicData", Gson().toJson(publicData).toString())
        editor.putString("accessToken", accessToken)
        editor.putString("refreshToken", refreshToken)
        editor.apply()
    }

    fun isLoggedIn(): Boolean {
        return publicData != null
    }
}

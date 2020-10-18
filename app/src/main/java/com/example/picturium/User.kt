package com.example.picturium

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import com.example.picturium.api.ImgurAPI
import com.example.picturium.api.response.RefreshTokenResponse
import com.example.picturium.api.response.UserDataResponse
import com.example.picturium.models.UserData
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Response

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
        if (!ImgurAPI.instance.checkAccessToken().isSuccessful) {
            val response: Response<RefreshTokenResponse> = ImgurAPI.instance.refreshAccessToken()
            if (!response.isSuccessful) {
                logout()
                return
            } else {
                accessToken = response.body()!!.data.accessToken
            }
        }
        if (_loadPublicData(publicData!!.username)) {
            save()
        } else {
            logout()
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

    private suspend fun _loadPublicData(username: String): Boolean {
        val response: Response<UserDataResponse> = ImgurAPI.instance.getUserData(username)

        if (!response.isSuccessful)
            return false
        publicData = response.body()!!.data
        return true
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

    fun _login(credentialsUri: Uri) {
        val username: String? = credentialsUri.getQueryParameter("account_username")
        accessToken = credentialsUri.getQueryParameter("access_token")
        refreshToken = credentialsUri.getQueryParameter("refresh_token")
        if (username.isNullOrEmpty() || accessToken.isNullOrEmpty() || refreshToken.isNullOrEmpty()) {
            logout()
            return
        }
        GlobalScope.launch(Dispatchers.IO) {
            if (_loadPublicData(username)) {
                save()
            } else {
                logout()
            }
        }
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

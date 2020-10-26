package com.example.picturium

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import com.example.picturium.api.ImgurAPI
import com.example.picturium.models.UserData
import kotlinx.coroutines.runBlocking

object User {
    private lateinit var _context: Context
    private lateinit var _cache: SharedPreferences
    private var _publicData: UserData? = null
    var accessToken: String? = null
    var refreshToken: String? = null
    val publicData: UserData?
        get() {
            if (_publicData == null && isLoggedIn()) {
                runBlocking {
                    _loadPublicData()
                }
            }
            return _publicData
        }

    suspend fun init(context: Context) {
        _context = context
        _cache = _context.getSharedPreferences("userCache", Context.MODE_PRIVATE)

        if (!_loadFromCache())
            return

        val tokenCheck = ImgurAPI.safeCall {
            ImgurAPI.instance.checkAccessToken()
            ImgurAPI.CallResult.SuccessResponse(Unit, true, 200)
        }

        if (tokenCheck is ImgurAPI.CallResult.NetworkError) {
            Picturium.toastConnectionError()
            return
        } else if (tokenCheck is ImgurAPI.CallResult.ErrorResponse && !_refreshAccessToken()) {
            return
        }
        _save()
        _loadPublicData()
    }

    private suspend fun _refreshAccessToken(): Boolean {
        val tokenRefresh = ImgurAPI.safeCall {
            ImgurAPI.instance.refreshAccessToken()
        }
        when (tokenRefresh) {
            is ImgurAPI.CallResult.NetworkError -> Picturium.toastConnectionError()
            is ImgurAPI.CallResult.ErrorResponse -> logout()
            is ImgurAPI.CallResult.SuccessResponse -> {
                accessToken = tokenRefresh.data.accessToken
                return true
            }
        }
        return false
    }

    private suspend fun _login(credentialsUri: Uri) {
        accessToken = credentialsUri.getQueryParameter("access_token")
        refreshToken = credentialsUri.getQueryParameter("refresh_token")
        if (accessToken.isNullOrEmpty() || refreshToken.isNullOrEmpty()) {
            logout()
            return
        }
        _loadPublicData()
    }

    private fun _loadFromCache(): Boolean {
        if (!_cache.contains("accessToken")) {
            logout()
            return false
        }
        accessToken = _cache.getString("accessToken", null)
        refreshToken = _cache.getString("refreshToken", null)
        return true
    }

    private suspend fun _loadPublicData(): Boolean {
        val res = ImgurAPI.safeCall {
            ImgurAPI.instance.getUserData()
        }
        when (res) {
            is ImgurAPI.CallResult.NetworkError -> Picturium.toastConnectionError()
            is ImgurAPI.CallResult.ErrorResponse -> logout()
            is ImgurAPI.CallResult.SuccessResponse -> {
                _publicData = res.data
                return true
            }
        }
        return false
    }

    fun redirectToLogin(context: Context) {
        val intent: Intent = Intent(Intent.ACTION_VIEW, Uri.parse(ImgurAPI.LOGIN_URL))
        context.startActivity(intent)
    }

    suspend fun handleLoginCallback(uri: Uri?) {
        if (isLoggedIn())
            return
        val params = uri?.encodedFragment
        if (params == null || !uri.toString().startsWith(BuildConfig.CALLBACK_URL))
            return
        val parsed: Uri = Uri.parse("_://?$params")
        _login(parsed)
    }

    fun logout() {
        _cache.edit().clear().apply()
        _publicData = null
        accessToken = null
        refreshToken = null
    }

    private fun _save() {
        val editor: SharedPreferences.Editor = _cache.edit()

        editor.putString("accessToken", accessToken)
        editor.putString("refreshToken", refreshToken)
        editor.apply()
    }

    fun isLoggedIn(): Boolean {
        return accessToken != null
    }

    suspend fun loadSubmissions(): Boolean {
        if (!isLoggedIn())
            return false
        val res = ImgurAPI.safeCall {
            ImgurAPI.instance.getSubmissionsFrom()
        }
        when (res) {
            is ImgurAPI.CallResult.NetworkError -> Picturium.toastConnectionError()
            is ImgurAPI.CallResult.ErrorResponse -> logout()
            is ImgurAPI.CallResult.SuccessResponse -> {
                if (publicData == null)
                    return false
                publicData!!.submissions = res.data
                return true
            }
        }
        return false
    }

    suspend fun loadFavorites(): Boolean {
        if (!isLoggedIn())
            return false
        val res = ImgurAPI.safeCall {
            ImgurAPI.instance.getFavoritesFrom()
        }
        when (res) {
            is ImgurAPI.CallResult.NetworkError -> Picturium.toastConnectionError()
            is ImgurAPI.CallResult.ErrorResponse -> logout()
            is ImgurAPI.CallResult.SuccessResponse -> {
                if (publicData == null)
                    return false
                publicData!!.favorites = res.data
                return true
            }
        }
        return false
    }
}

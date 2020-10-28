package com.example.picturium.viewmodels

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.picturium.BuildConfig
import com.example.picturium.Picturium
import com.example.picturium.api.ImgurAPI
import com.example.picturium.models.Submission
import com.example.picturium.models.UserData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object UserViewModel : ViewModel() {
    private lateinit var _context: Context
    private lateinit var _cache: SharedPreferences
    val publicData: MutableLiveData<UserData?> = MutableLiveData()
    val favorites: MutableLiveData<List<Submission>> = MutableLiveData()
    val albums: MutableLiveData<List<Submission>> = MutableLiveData()
    var refreshToken: String? = null
        private set
    var accessToken: String? = null
        private set

    fun init(context: Context) {
        _context = context
        _cache = _context.getSharedPreferences("userCache", Context.MODE_PRIVATE)

        if (!_loadFromCache())
            return
        refresh()
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

    private fun _login(credentialsUri: Uri): Boolean {
        accessToken = credentialsUri.getQueryParameter("access_token")
        refreshToken = credentialsUri.getQueryParameter("refresh_token")
        if (accessToken.isNullOrEmpty() || refreshToken.isNullOrEmpty()) {
            logout()
            return false
        }
        _save()
        _loadPublicData()
        return true
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

    private fun _loadPublicData() {
        viewModelScope.launch(Dispatchers.IO) {
            val res = ImgurAPI.safeCall {
                ImgurAPI.instance.getUserData()
            }
            when (res) {
                is ImgurAPI.CallResult.NetworkError -> {
                    Picturium.toastConnectionError()
                    publicData.postValue(null)
                }
                is ImgurAPI.CallResult.ErrorResponse -> logout()
                is ImgurAPI.CallResult.SuccessResponse -> publicData.postValue(res.data)
            }
        }
    }

    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            val tokenCheck = ImgurAPI.safeCall {
                ImgurAPI.instance.checkAccessToken()
                ImgurAPI.CallResult.SuccessResponse(Unit, true, 200)
            }
            if (tokenCheck is ImgurAPI.CallResult.NetworkError) {
                Picturium.toastConnectionError()
                return@launch
            } else if (tokenCheck is ImgurAPI.CallResult.ErrorResponse && !_refreshAccessToken()) {
                return@launch
            }
            _save()
            _loadPublicData()
        }
    }

    fun redirectToLogin(context: Context) {
        val intent: Intent = Intent(Intent.ACTION_VIEW, Uri.parse(ImgurAPI.LOGIN_URL))
        context.startActivity(intent)
    }

    fun handleLoginCallback(uri: Uri?): Boolean {
        val params = uri?.encodedFragment
        if (params == null || !uri.toString().startsWith(BuildConfig.CALLBACK_URL))
            return false
        if (isLoggedIn())
            return true
        val parsed: Uri = Uri.parse("_://?$params")
        return _login(parsed)
    }

    fun logout() {
        _cache.edit().clear().apply()
        publicData.postValue(null)
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

    fun loadAlbums() {
        viewModelScope.launch(Dispatchers.IO) {
            val res = ImgurAPI.safeCall {
                ImgurAPI.instance.getAlbumsFrom()
            }
            albums.postValue(
                when (res) {
                    is ImgurAPI.CallResult.NetworkError -> {
                        Picturium.toastConnectionError()
                        emptyList()
                    }
                    is ImgurAPI.CallResult.ErrorResponse -> emptyList()
                    is ImgurAPI.CallResult.SuccessResponse -> res.data
                }
            )
        }
    }

    fun loadFavorites() {
        viewModelScope.launch(Dispatchers.IO) {
            val res = ImgurAPI.safeCall {
                ImgurAPI.instance.getFavoritesFrom()
            }
            favorites.postValue(
                when (res) {
                    is ImgurAPI.CallResult.NetworkError -> {
                        Picturium.toastConnectionError()
                        emptyList()
                    }
                    is ImgurAPI.CallResult.ErrorResponse -> emptyList()
                    is ImgurAPI.CallResult.SuccessResponse -> res.data
                }
            )
        }
    }
}

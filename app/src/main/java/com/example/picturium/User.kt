package com.example.picturium

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.widget.Toast
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import java.io.IOException

class User(private val _context: Context) {
    init {
        User._cache = _context.getSharedPreferences("userCache", Context.MODE_PRIVATE)
        User._context = _context
        User.init()
    }

    companion object {
        private lateinit var _context: Context
        private lateinit var _cache: SharedPreferences
        private var _hasCredentials: Boolean = false
        var data: UserData? = null
        var userName: String? = null
        var accountId: String? = null
        var accessToken: String? = null
        var refreshToken: String? = null
        var tokenType: String? = null

        private fun init() {
            loadCredentials()
            checkAccessToken()
            loadData()
        }

        private fun loadCredentials() {
            if (!_cache.contains("userName"))
                return
            userName = _cache.getString("userName", null)
            accountId = _cache.getString("accountId", null)
            accessToken = _cache.getString("accessToken", null)
            refreshToken = _cache.getString("refreshToken", null)
            tokenType = _cache.getString("tokenType", null)
            _hasCredentials = true
        }

        private fun checkAccessToken() {
            if (!_hasCredentials)
                return

            val clt = OkHttpClient()
            val checkReq: Request = Request.Builder()
                .get()
                .url("https://api.imgur.com/oauth2/secret")
                .header("Authorization", "Bearer $accessToken")
                .build()

            clt.newCall(checkReq).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {}

                override fun onFailure(call: Call, e: IOException) {
                    val body: RequestBody = MultipartBody.Builder()
                        .addFormDataPart("refresh_token", refreshToken!!)
                        .addFormDataPart("client_id", BuildConfig.CLIENT_ID)
                        .addFormDataPart("client_secret", BuildConfig.CLIENT_SECRET)
                        .addFormDataPart("grant_type", "refresh_token")
                        .setType("application/json; charset=utf-8".toMediaType())
                        .build()

                    val refreshReq: Request = Request.Builder()
                        .post(body)
                        .url("https://api.imgur.com/oauth2/token")
                        .header("Authorization", "Client-ID ${BuildConfig.CLIENT_ID}")
                        .build()

                    clt.newCall(refreshReq).enqueue(object : Callback {
                        override fun onResponse(call: Call, response: Response) {
                            val gson = Gson()
                            val resBody: String = response.body?.string().toString()

                            val refreshedToken: RefreshTokenResponse = gson.fromJson(resBody, RefreshTokenResponse::class.java)
                            accessToken = refreshedToken.access_token
                        }

                        override fun onFailure(call: Call, e: IOException) {
                            logout()
                            Toast.makeText(_context, "Couldn't retrieve data, please log back in", Toast.LENGTH_LONG).show()
                        }
                    })
                }
            })
        }

        private fun loadData() {
            if (!_hasCredentials)
                return

            val clt = OkHttpClient()
            val req: Request = Request.Builder()
                .get()
                .url("https://api.imgur.com/3/account/$userName")
                .header("Authorization", "Client-ID ${BuildConfig.CLIENT_ID}")
                .build()

            clt.newCall(req).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val gson = Gson()
                    val resBody: String = response.body?.string().toString()

                    data = gson.fromJson(resBody, UserDataResponse::class.java).data
                }

                override fun onFailure(call: Call, e: IOException) {
                    logout()
                    Toast.makeText(_context, "Couldn't retrieve data, please log back in", Toast.LENGTH_LONG).show()
                }
            })
        }

        fun login(credentialsUri: Uri) {
            userName = credentialsUri.getQueryParameter("account_username")
            if (userName.isNullOrEmpty()) {
                logout()
                return
            }
            accountId = credentialsUri.getQueryParameter("account_id")
            accessToken = credentialsUri.getQueryParameter("access_token")
            refreshToken = credentialsUri.getQueryParameter("refresh_token")
            tokenType = credentialsUri.getQueryParameter("token_type")
            _hasCredentials = true
            val editor: SharedPreferences.Editor = _cache.edit()
            editor.putString("userName", userName)
            editor.putString("accountId", accountId)
            editor.putString("accessToken", accessToken)
            editor.putString("refreshToken", refreshToken)
            editor.putString("tokenType", tokenType)
            editor.apply()
            loadData()
        }

        fun logout() {
            _cache.edit().clear().apply()
            data = null
            userName = null
            accountId = null
            accessToken = null
            refreshToken = null
            tokenType = null
        }

        fun isLoggedIn(): Boolean {
            return data != null
        }
    }

    private data class UserDataResponse(
        val data: UserData,
        val status: Int,
        val success: Boolean
    )

    data class UserData(
        val avatar: String,
        val avatar_name: String,
        val bio: Any,
        val cover: String,
        val cover_name: String,
        val created: Int,
        val id: Int,
        val is_blocked: Boolean,
        val pro_expiration: Boolean,
        val reputation: Int,
        val reputation_name: String,
        val url: String,
        val user_follow: UserFollow
    )

    data class UserFollow(
        val status: Boolean
    )

    private data class RefreshTokenResponse(
        val access_token: String,
        val account_username: String,
        val expires_in: Int,
        val refresh_token: String,
        val token_type: String
    )
}

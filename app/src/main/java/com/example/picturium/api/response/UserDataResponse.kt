package com.example.picturium.api.response

import com.example.picturium.models.UserData

data class UserDataResponse(
    val data: UserData,
    val status: Int,
    val success: Boolean
)
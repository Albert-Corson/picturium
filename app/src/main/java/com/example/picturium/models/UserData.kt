package com.example.picturium.models

import com.google.gson.annotations.SerializedName

data class UserData(
    @SerializedName("url")
    val username: String,

    @SerializedName("avatar")
    val profilePicture: String,

    @SerializedName("cover")
    val coverPicture: String,

    @SerializedName("is_blocked")
    val isBlocked: Boolean,

    @SerializedName("reputation_name")
    val reputationName: String,

    val bio: String?,
    val id: Int,
    val created: Int,
    val reputation: Int,
) {
    var favorites: List<Submission>? = null
    var submissions: List<Submission>? = null
}

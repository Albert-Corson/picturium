package com.example.picturium.models

import com.google.gson.annotations.SerializedName

data class MediaResource(
    @SerializedName("id")
    val id: String,

    @SerializedName("title")
    val title: String?,

    @SerializedName("description")
    val description: String?,

    @SerializedName("datetime")
    val creationDate: Int,

    @SerializedName("type")
    val type: String,

    @SerializedName("animated")
    val animated: Boolean,

    @SerializedName("width")
    val width: Int,

    @SerializedName("height")
    val height: Int,

    @SerializedName("views")
    val views: Int,

    @SerializedName("favorite")
    val favorite: Boolean,

    @SerializedName("account_url")
    val userName: String,

    @SerializedName("account_id")
    val userId: Int,

    @SerializedName("has_sound")
    val hasSound: Boolean,

    @SerializedName("deletehash")
    val deleteHash: String,

    @SerializedName("link")
    val imageLink: String?,

    @SerializedName("mp4")
    val videoLink: String?,

    @SerializedName("gifv")
    val gifLink: String?
)
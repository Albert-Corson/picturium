package com.example.picturium.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Image(
    @SerializedName("id")
    val id: String?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("views")
    val views: Int?,
    @SerializedName("favorite")
    val isFavorite: Boolean?,
    @SerializedName("account_url")
    val author: String?,
    @SerializedName("account_id")
    val authorId: Int?,
    @SerializedName("tags")
    val tags: List<Tag>?,
    @SerializedName("comment_count")
    val commentCount: Int?,
    @SerializedName("ups")
    val upVotes: Int?,
    @SerializedName("downs")
    val downVotes: Int?,
    @SerializedName("points")
    val points: Int?,
    @SerializedName("score")
    val score: Int?,
    @SerializedName("width")
    val width: Int?,
    @SerializedName("height")
    val height: Int?,
    @SerializedName("animated")
    val animated: Boolean?,
    @SerializedName("has_sound")
    val hasSound: Boolean?,
    @SerializedName("link")
    val link: String?,
    @SerializedName("vote")
    val vote: String?
) : Parcelable
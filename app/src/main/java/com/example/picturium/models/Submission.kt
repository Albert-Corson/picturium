package com.example.picturium.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Submission(
    @SerializedName("cover_width")
    val coverWidth: Int?,

    @SerializedName("cover_height")
    val coverHeight: Int?,

    val id: String,
    val title: String,
    val description: String?,
    val cover: String?,
    val width: Int?,
    val height: Int?,
    val link: String,
    val ups: Int,
    val downs: Int,
    val score: Int,
    val images: List<MediaResource>?,
) : Parcelable
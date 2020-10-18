package com.example.picturium.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class ThreadData(
    val id: String,
    val title: String,
    val description: String?,
    val cover: (String)?,
    val cover_width: Int,
    val cover_height: Int,
    val ups: Int,
    val downs: Int,
    val score: Int,
    val images: List<Image>?,
) : Parcelable {

    @Parcelize
    data class Image(
        val id: String,
        val title: String?,
        val description: String?,
        val link: String
    ) : Parcelable
}
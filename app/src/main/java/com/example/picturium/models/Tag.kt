package com.example.picturium.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Tag(
    @SerializedName("name")
    val name: String?,
    @SerializedName("display_name")
    val displayName: String?,
    @SerializedName("description")
    val description: String?,
) : Parcelable
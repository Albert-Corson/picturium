package com.example.picturium.models

import android.os.Parcelable
import com.example.picturium.api.ImgurAPI
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Submission(
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
    private val width: Int?,
    @SerializedName("height")
    private val height: Int?,
    @SerializedName("animated")
    private val animated: Boolean?,
    @SerializedName("has_sound")
    private val hasSound: Boolean?,
    @SerializedName("link")
    private val link: String?,

    @SerializedName("is_album")
    private val _isAlbum: Boolean?,
    @SerializedName("cover")
    private val _cover: String?,
    @SerializedName("cover_width")
    private val _coverWidth: Int?,
    @SerializedName("cover_height")
    private val _coverHeight: Int?,
    @SerializedName("images")
    private var _images: List<Image>?
) : Parcelable {
    private val cover: String get() = _cover ?: id!!
    val coverWidth: Int get() = _coverWidth ?: width!!
    val coverHeight: Int get() = _coverHeight ?: height!!
    val images: List<Image>
        get() {
            if (_images == null && _cover == null) {
                _images = listOf(
                    Image(
                        id = id,
                        title = title,
                        description = description,
                        views = views,
                        isFavorite = isFavorite,
                        author = author,
                        authorId = authorId,
                        tags = tags,
                        commentCount = commentCount,
                        upVotes = upVotes,
                        downVotes = downVotes,
                        points = points,
                        score = score,
                        width = width,
                        height = height,
                        animated = animated,
                        hasSound = hasSound,
                        link = link
                    )
                )
            } else if (_images == null) {
                _images = emptyList()
            }
            return _images!!
        }

    suspend fun getCoverImage(): Image? {
        return images.find { it.id == cover && !it.link.isNullOrBlank() } ?: run {
            val res = ImgurAPI.safeCall {
                ImgurAPI.instance.getImage(cover)
            }
            if (res is ImgurAPI.CallResult.SuccessResponse) {
                res.body.data
            } else {
                null
            }
        }
    }
}
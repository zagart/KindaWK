package com.vvsemir.kindawk.provider.gson.entities

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.Date

@Parcelize
data class GsonNewsPost (
    @SerializedName("type")
    val type: String?,

    @SerializedName("source_id")
    val sourceId: Long?,

    @SerializedName("date")
    val date: Date?,

    @SerializedName("post_type")
    val postType: String?,

    @SerializedName("post_id")
    val postId: Long?,

    @SerializedName("text")
    val text: String?,

    @SerializedName("attachments")
    val attachments: List<GsonAttachment>?,

    @SerializedName("copy_history")
    val copyHistory: List<GsonCopyHistory>?,

    @SerializedName("likes")
    val likes: GsonLikes?,

    @SerializedName("views")
    val views: GsonViews? ) : Parcelable

package com.vvsemir.kindawk.provider.gson.entities

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GsonPhoto (
        @SerializedName("id")
        val id: Long?,

        @SerializedName("sizes")
        val sizes: List<GsonPhotoSize>?,

        @SerializedName("text")
        val text: String?,

        @SerializedName("date")
        val data: Long?,

        @SerializedName("post_id")
        val postId: Long?,

        @SerializedName("owner_id")
        val ownerId: Long?,

        @SerializedName("album_id")
        val albumId: Int?) : Parcelable
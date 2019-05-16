package com.vvsemir.kindawk.provider.gson.entities

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GsonPhotoSize (
        @SerializedName("type")
        val type: String?,

        @SerializedName("url")
        val url: String?,

        @SerializedName("width")
        val width: Int?,

        @SerializedName("height")
        val height: Int?) : Parcelable



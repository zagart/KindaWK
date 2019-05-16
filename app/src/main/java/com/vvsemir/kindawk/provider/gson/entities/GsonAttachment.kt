package com.vvsemir.kindawk.provider.gson.entities

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GsonAttachment (
        @SerializedName("type")
        val type: String?,

        @SerializedName("photo")
        val photo: GsonPhoto?) : Parcelable
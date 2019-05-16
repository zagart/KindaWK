package com.vvsemir.kindawk.provider.gson.entities

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GsonGroup (
        @SerializedName("id")
        val id: Long?,

        @SerializedName("name")
        val firstName: String?,

        @SerializedName("photo_100")
        val photo100: String?) : Parcelable

package com.vvsemir.kindawk.provider.gson.entities

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GsonCopyHistory (
    @SerializedName("text")
    val text: String?,

    @SerializedName("attachments")
    val attachments: List<GsonAttachment>? ) : Parcelable


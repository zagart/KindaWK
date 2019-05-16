package com.vvsemir.kindawk.provider.gson.entities

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GsonNewsWall (
    @SerializedName("items")
    val items: List<GsonNewsPost>?,

    @SerializedName("profiles")
    val profiles: Set<GsonProfile>?,

    @SerializedName("groups")
    val groups: Set<GsonGroup>?,

    @SerializedName("next_from")
    val nextFrom: String?) : Parcelable



package com.fortrade.tiktok.model


import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserProfileData(
    val fullName: String,
    val UserName: String,
    val PhoneNumber: String,
    val Bio: String,
    val Website: String,
    val gender: String,
    val birthOfDate: String,
    val ProfileImageUrl: String? = null
):Parcelable
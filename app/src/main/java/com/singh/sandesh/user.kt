package com.singh.sandesh

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class user(var name:String, val uid:String) : Parcelable {
    constructor():this("","")
}
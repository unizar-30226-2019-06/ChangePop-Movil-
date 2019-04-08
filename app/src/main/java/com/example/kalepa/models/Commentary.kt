package com.example.kalepa.models

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@SuppressLint("ParcelCreator")
@Parcelize
class Commentary (
    val ID: Int,
    var Date: Date,
    var Body: String

) : Parcelable {
    constructor(): this(0,Date(),"")
}
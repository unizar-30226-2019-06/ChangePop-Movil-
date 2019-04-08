package com.example.kalepa.models

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@SuppressLint("ParcelCreator")
@Parcelize
class Report (
    val ID: Int,
    val Reason: String

) : Parcelable {
    constructor(): this(0,"")
}
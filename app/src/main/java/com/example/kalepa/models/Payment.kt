package com.example.kalepa.models

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@SuppressLint("ParcelCreator")
@Parcelize
class Payment (
    val ID: Int,
    val Date: Date,
    val Amount: Double,
    val IBAN: String,
    val Boost_date: Date

) : Parcelable {
    constructor(): this(0,Date(),0.0, "",Date())
}
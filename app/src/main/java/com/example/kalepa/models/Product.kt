package com.example.kalepa.models

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@SuppressLint("ParcelCreator")
@Parcelize
class Product (
    val ID: Int,
    var Name: String,
    var Description: String,
    var Price: Double,
    var Upload_date: Date,
    var Blocked: Boolean,
    var Bid_expire: Date,
    var Visits: Int,
    var Images: ArrayList<String?>,
    var Boosted: Boolean,
    var Followers: Int,
    var Email: String,
    var Deleted: Boolean,
    var Localization: String,
    var OwnerNick: String

) : Parcelable {
    constructor(): this(0,"","",0.0,Date(),false,
        Date(),0, arrayListOf(""),false,0,"",false,"", "")
}
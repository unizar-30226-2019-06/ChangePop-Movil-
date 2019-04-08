package com.example.kalepa.models

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@SuppressLint("ParcelCreator")
@Parcelize
class User (
    val ID: Int,
    var Nick: String,
    var Name: String,
    var Surnames: String,
    var Blocked: Boolean,
    var Moderator: Boolean,
    var DNI: String,
    var Avatar: String,
    var Birthdate: Date,
    var Place: String,
    var Hash_key: String
) : Parcelable {
    constructor(): this(0, "", "", "", false, false, "", "",
        Date(), "", "")
}
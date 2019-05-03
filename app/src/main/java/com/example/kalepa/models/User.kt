package com.example.kalepa.models

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.json.JSONObject
import java.util.*

@SuppressLint("ParcelCreator")
@Parcelize
class User (
    val ID: Int,
    var Nick: String,
    var Name: String,
    var Surnames: String,
    var Mail: String,
    var phone: String,
    var points: Int,
    var Blocked: Boolean,
    var Moderator: Boolean,
    var DNI: String,
    var Avatar: String,
    var Birthdate: Date,
    var Place: String,
    var Hash_key: String
) : Parcelable {
    constructor(): this(0, "", "", "", "", "",0, false, false, "", "",
        Date(), "", "")

    /*public fun toJson() : JSONObject {
        var jsonObject = JSONObject()
        jsonObject.accumulate("")
        return jsonObject
    }*/
}
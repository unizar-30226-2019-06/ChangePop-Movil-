package com.example.kalepa.models

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.json.JSONObject
import java.util.*

@SuppressLint("ParcelCreator")
@Parcelize
class Comment (
    var id: Int,
    var user: String,
    var body: String
) : Parcelable {
    constructor(): this(0,"","")

    public fun fromJSON (jsonObject: JSONObject){
        id = jsonObject.get("id").toString().toInt()
        user = jsonObject.get("nick").toString()
        body = jsonObject.get("body").toString()
    }
}
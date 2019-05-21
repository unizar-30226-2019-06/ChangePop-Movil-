package com.example.kalepa.models

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.json.JSONObject
import java.util.*

@SuppressLint("ParcelCreator")
@Parcelize
class Comment (
    var user: String,
    var body: String

) : Parcelable {
    constructor(): this("","")

    public fun fromJSON (jsonObject: JSONObject){
        user = jsonObject.get("user").toString()
        body = jsonObject.get("body").toString()

    }
}
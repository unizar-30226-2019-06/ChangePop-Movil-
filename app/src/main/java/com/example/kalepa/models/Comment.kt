package com.example.kalepa.models

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.json.JSONObject
import java.util.*

@SuppressLint("ParcelCreator")
@Parcelize
class Comment (
    var body: String,
    var points: Double
) : Parcelable {
    constructor(): this("",0.0)

    public fun fromJSON (jsonObject: JSONObject){
        body = jsonObject.get("body").toString()
        points = jsonObject.get("points").toString().toDouble()
    }
}
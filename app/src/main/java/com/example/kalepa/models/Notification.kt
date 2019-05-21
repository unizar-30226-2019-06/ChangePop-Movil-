package com.example.kalepa.models

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

@SuppressLint("ParcelCreator")
@Parcelize
class Notification (
    var id: Int,
    var user_id: Int,
    var product_id: Int,
    var category: String,
    var text: String,
    var date: String


) : Parcelable {
    constructor(): this(0,0,0,"","",""
    )

    public fun fromJSON (jsonObject: JSONObject){

        id = jsonObject.get("id").toString().toInt()
        user_id = jsonObject.get("user_id").toString().toInt()
        product_id = jsonObject.get("product_id").toString().toInt()
        category = jsonObject.get("category").toString()
        text = jsonObject.get("text").toString()
        date = jsonObject.get("date").toString()

    }
}
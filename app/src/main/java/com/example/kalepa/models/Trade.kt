package com.example.kalepa.models

import android.annotation.SuppressLint
import android.os.Parcelable
import com.example.kalepa.MainActivity
import com.example.kalepa.Preferences.SharedApp
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.delay
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

@SuppressLint("ParcelCreator")
@Parcelize
class Trade (
    var id: Int,
    var product_id: Int,
    var product_title: String,
    var seller_id: Int,
    var buyer_id: Int,
    var closed: Boolean,
    var price: Double,
    var last_edit: String,
    var product_img: String,
    var other_avatar: String,
    var other_nick: String


) : Parcelable {

    @Volatile var responses: Int = 0

    constructor(): this(0,0,"",0,0,false,0.0,"","",
        "","")

    public fun fromJSON (jsonObject: JSONObject){

        id = jsonObject.get("id").toString().toInt()
        product_id = jsonObject.get("product_id").toString().toInt()
        product_title = jsonObject.get("product_title").toString()
        seller_id = jsonObject.get("seller_id").toString().toInt()
        buyer_id = jsonObject.get("buyer_id").toString().toInt()
        closed = jsonObject.get("closed").toString().toBoolean()
        price = jsonObject.get("price").toString().toDouble()
        last_edit = jsonObject.get("last_edit").toString()
    }
}
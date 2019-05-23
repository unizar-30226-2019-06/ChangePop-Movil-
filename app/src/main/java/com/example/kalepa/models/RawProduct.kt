package com.example.kalepa.models

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

@SuppressLint("ParcelCreator")
@Parcelize
class RawProduct (
    var id: Int,
    var price: Double,
    var title: String,
    var bid_date: String,
    var main_img: String


) : Parcelable {
    constructor(): this(0,0.0,"","","")

    public fun fromJSON (jsonObject: JSONObject){
        id = jsonObject.get("id").toString().toInt()
        price = jsonObject.get("price").toString().toDouble()
        title = jsonObject.get("title").toString()
        bid_date = jsonObject.get("bid_date").toString()
        val aux = jsonObject.get("main_img").toString()
        main_img = aux.replace("\\", "")
    }

    public fun isBid (): Boolean {
        if (!bid_date.equals("None")) {
            val now = Calendar.getInstance()
            val bidDate = now.get(Calendar.YEAR).toString() +
                    String.format("%02d", now.get(Calendar.MONTH)) +
                    String.format("%02d", now.get(Calendar.DAY_OF_MONTH))
            val actual = bidDate.toInt()

            var prodBidDate = bid_date.replace("-", "")

            val prodOne = prodBidDate.substring(0,8).toInt()

            return prodOne > actual
        } else {
            return false
        }
    }
}
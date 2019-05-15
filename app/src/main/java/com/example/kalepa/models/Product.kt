package com.example.kalepa.models

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

@SuppressLint("ParcelCreator")
@Parcelize
class Product (
    var id: Int,
    var descript: String,
    var user_id: Int,
    var price: Double,
    var categories: ArrayList<String>,
    var title: String,
    var bid_date: String,
    var boost_date: String,
    var visits: Int,
    var followers: Int,
    var publish_date: String,
    var main_img: String,
    var photo_urls: ArrayList<String?>,
    var place: String,
    var ban_reason: String


) : Parcelable {
    constructor(): this(0,"",0,0.0,arrayListOf(""),"","","",0,
        0,"","",arrayListOf(""),"",""
    )

    public fun fromJSON (jsonObject: JSONObject){

        id = jsonObject.get("id").toString().toInt()
        descript = jsonObject.get("descript").toString()
        user_id = jsonObject.get("user_id").toString().toInt()
        price = jsonObject.get("price").toString().toDouble()

        val aux = jsonObject.get("categories").toString()
        val separate = aux.split("""(\',\)\",\"\(\')|(\[\"\(\')|(\',\)\"\])""".toRegex())
        categories = ArrayList(separate.slice(IntRange(1,separate.size-2)))

        title = jsonObject.get("title").toString()
        bid_date = jsonObject.get("bid_date").toString()
        boost_date = jsonObject.get("boost_date").toString()
        //visits = jsonObject.get("visits").toString().toInt()
        followers = jsonObject.get("followers").toString().toInt()
        publish_date = jsonObject.get("publish_date").toString()
        main_img = jsonObject.get("main_img").toString()

        val aux2 = jsonObject.get("photo_urls").toString()
        val separate2 = aux.split("""(\',\)\",\"\(\')|(\[\"\(\')|(\',\)\"\])""".toRegex())
        photo_urls = ArrayList(separate2.slice(IntRange(1,separate.size-2)))

        place = jsonObject.get("place").toString()
        ban_reason = jsonObject.get("ban_reason").toString()

    }
}
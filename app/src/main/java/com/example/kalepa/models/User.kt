package com.example.kalepa.models

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.json.JSONObject
import java.util.*

@SuppressLint("ParcelCreator")
@Parcelize
class User (
    var id: Int,
    var nick: String,
    var first_name: String,
    var last_name: String,
    var mail: String,
    var phone: String,
    var is_mod: Boolean,
    var ban_reason: String,
    var points: Double,
    var avatar: String,
    var fnac: String,
    var dni: String,
    var place: String
) : Parcelable {
    constructor(): this(0, "", "", "", "", "",false, "",0.0, "", "",
        "", "")

    public fun fromJSON (jsonObject: JSONObject){

        id = jsonObject.get("id").toString().toInt()
        nick = jsonObject.get("nick").toString()
        first_name = jsonObject.get("first_name").toString()
        last_name = jsonObject.get("last_name").toString()
        mail = jsonObject.get("mail").toString()
        phone = jsonObject.get("phone").toString()
        is_mod = jsonObject.get("is_mod").toString().toBoolean()
        ban_reason = jsonObject.get("ban_reason").toString()
        points = jsonObject.get("points").toString().toDouble()
        avatar = jsonObject.get("avatar").toString()
        fnac = jsonObject.get("fnac").toString()
        dni = jsonObject.get("dni").toString()
        place = jsonObject.get("place").toString()
    }

    public fun toJSON () : JSONObject {

        val jsonObject = JSONObject()

        jsonObject.accumulate("id", id)
        jsonObject.accumulate("nick", nick)
        jsonObject.accumulate("first_name", first_name)
        jsonObject.accumulate("last_name", last_name)
        jsonObject.accumulate("mail", mail)
        jsonObject.accumulate("phone", phone)
        jsonObject.accumulate("is_mod", is_mod)
        jsonObject.accumulate("ban_reason", ban_reason)
        jsonObject.accumulate("points", points)
        jsonObject.accumulate("avatar", avatar)
        jsonObject.accumulate("fnac", fnac)
        jsonObject.accumulate("dni", dni)
        jsonObject.accumulate("place", place)

        return jsonObject
    }
}
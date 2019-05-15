package com.example.kalepa.Preferences

import android.content.Context
import android.content.SharedPreferences

class Prefs (context: Context) {
    val PREFS_NAME = "com.example.kalepa.sharedpreferences"
    val SHARED_COOKIE = "shared_cookie"
    val SHARED_PLACE = "shared_place"
    val SHARED_ID = "shared_id"
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, 0)

    var cookie: String
        get() = prefs.getString(SHARED_COOKIE, "")
        set(value) = prefs.edit().putString(SHARED_COOKIE, value).apply()

    var userPlace: String
        get() = prefs.getString(SHARED_PLACE, "")
        set(value) = prefs.edit().putString(SHARED_PLACE, value).apply()

    var userId: Int
        get() = prefs.getInt(SHARED_ID, 0)
        set(value) = prefs.edit().putInt(SHARED_ID, value).apply()

}
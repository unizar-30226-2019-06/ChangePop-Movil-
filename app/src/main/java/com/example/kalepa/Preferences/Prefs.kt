package com.example.kalepa.Preferences

import android.content.Context
import android.content.SharedPreferences

class Prefs (context: Context) {
    val PREFS_NAME = "com.example.kalepa.sharedpreferences"
    val SHARED_COOKIE = "shared_cookie"
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, 0)

    var cookie: String
        get() = prefs.getString(SHARED_COOKIE, "")
        set(value) = prefs.edit().putString(SHARED_COOKIE, value).apply()
}
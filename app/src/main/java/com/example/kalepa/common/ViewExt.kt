package com.example.charactermanager.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide

fun <T : View> RecyclerView.ViewHolder.bindView(viewId: Int)
        = lazy { itemView.findViewById<T>(viewId) }

fun ImageView.loadImage(photoUrl: String) {
    Glide.with(context)
        .load(photoUrl)
        .into(this)
}

fun <T : Parcelable> Activity.extra(key: String, default: T? = null):
        Lazy<T>
        = lazy { intent?.extras?.getParcelable<T>(key) ?: default ?: throw
Error("No value $key in extras") }
inline fun <reified T : Activity> Context.getIntent() = Intent(this,
    T::class.java)
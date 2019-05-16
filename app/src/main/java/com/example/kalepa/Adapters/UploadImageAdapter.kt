package com.example.kalepa.Adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.kalepa.R
import com.example.kalepa.common.ItemAdapter
import com.example.kalepa.common.bindView
import com.example.kalepa.common.loadImage
import com.example.kalepa.models.Product

class UploadImageAdapter (
    val path: String,
    //val bitmap: Bitmap,
    val holded: (String) -> Boolean
) : ItemAdapter<UploadImageAdapter.ViewHolder>(R.layout.item_upload_image) {
    override fun onCreateViewHolder(itemView: View) = ViewHolder(itemView)
    override fun ViewHolder.onBindViewHolder() { // 2
        imageView.loadImage(path) // 3
        //val bitmap = BitmapFactory.decodeFile(path)
        //imageView.setImageBitmap(bitmap)
        itemView.setOnLongClickListener { holded(path)}
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val imageView by bindView<ImageView>(R.id.n_upload_image_view)
    }
}
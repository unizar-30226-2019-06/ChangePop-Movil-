package com.example.kalepa.Adapters

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
    val image: String,
    val holded: (String) -> Boolean
) : ItemAdapter<UploadImageAdapter.ViewHolder>(R.layout.item_upload_image) {
    override fun onCreateViewHolder(itemView: View) = ViewHolder(itemView)
    override fun ViewHolder.onBindViewHolder() { // 2
        imageView.loadImage(image) // 3
        itemView.setOnLongClickListener { holded(image)}
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val imageView by bindView<ImageView>(R.id.n_upload_image_view)
    }
}
package com.example.kalepa.Adapters

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.example.kalepa.R
import com.example.kalepa.common.ItemAdapter
import com.example.kalepa.common.bindView
import com.example.kalepa.common.loadImage
import com.example.kalepa.models.RawProduct

class ChatProductAdapter (
    val product: RawProduct
) : ItemAdapter<ChatProductAdapter.ViewHolder>(R.layout.item_chat_product) {
    override fun onCreateViewHolder(itemView: View) = ViewHolder(itemView)
    override fun ViewHolder.onBindViewHolder() { // 2
        textView.text = product.title
        price.setText(product.price.toString())
        imageView.loadImage(product.main_img) // 3
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val textView by bindView<TextView>(R.id.n_chat_product_title)
        val price by bindView<TextView>(R.id.n_chat_product_price)
        val imageView by bindView<ImageView>(R.id.n_chat_product_photo)
    }
}
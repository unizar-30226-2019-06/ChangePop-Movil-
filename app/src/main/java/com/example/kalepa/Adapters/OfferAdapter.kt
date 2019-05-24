package com.example.kalepa.Adapters

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.example.kalepa.R
import com.example.kalepa.common.ItemAdapter
import com.example.kalepa.common.bindView
import com.example.kalepa.common.loadImage
import com.example.kalepa.models.RawProduct

class OfferAdapter (
    val product: RawProduct
) : ItemAdapter<OfferAdapter.ViewHolder>(R.layout.item_offer_product) {
    override fun onCreateViewHolder(itemView: View) = ViewHolder(itemView)
    override fun ViewHolder.onBindViewHolder() { // 2
        textView.text = product.title
        imageView.loadImage(product.main_img) // 3
        itemView.setOnClickListener {
            box.isChecked = !box.isChecked
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val textView by bindView<TextView>(R.id.m_offer_product_product)
        val box by bindView<CheckBox>(R.id.m_offer_product_checkBox)
        val imageView by bindView<ImageView>(R.id.m_offer_product_photo)
    }
}
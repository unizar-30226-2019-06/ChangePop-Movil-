package com.example.kalepa.Adapters

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.kalepa.R
import com.example.kalepa.common.ItemAdapter
import com.example.kalepa.common.bindView
import com.example.kalepa.common.loadImage
import com.example.kalepa.models.RawProduct

class RawProductAdapter (
    val product: RawProduct,
    val clicked: (RawProduct) -> Unit
) : ItemAdapter<RawProductAdapter.ViewHolder>(R.layout.item_product) {
    override fun onCreateViewHolder(itemView: View) = ViewHolder(itemView)
    override fun ViewHolder.onBindViewHolder() { // 2
        textView1.text = product.title
        textView2.text = product.price.toString()
        if (product.isBid()) {
            textView3.text = "Subasta"
            textView3.setTextColor(Color.parseColor("#ff0000"))
        } else {
            textView3.text = "Intercambio"
            textView3.setTextColor(Color.parseColor("#0066ff"))
        }
        imageView.loadImage(product.main_img) // 3
        itemView.setOnClickListener { clicked(product) }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val textView1 by bindView<TextView>(R.id.n_product_tittle)
        val textView2 by bindView<TextView>(R.id.n_product_price)
        val textView3 by bindView<TextView>(R.id.n_bid_or_buy)
        val imageView by bindView<ImageView>(R.id.n_product_image)
    }
}
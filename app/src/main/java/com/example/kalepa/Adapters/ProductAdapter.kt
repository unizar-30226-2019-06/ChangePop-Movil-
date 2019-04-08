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

class ProductAdapter (
    val product: Product,
    val clicked: (Product) -> Unit
) : ItemAdapter<ProductAdapter.ViewHolder>(R.layout.item_product) {
    override fun onCreateViewHolder(itemView: View) = ViewHolder(itemView)
    override fun ViewHolder.onBindViewHolder() { // 2
        textView1.text = product.Name
        textView2.text = product.Price.toString()
        imageView.loadImage(product.Images[0]!!) // 3
        itemView.setOnClickListener { clicked(product) }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val textView1 by bindView<TextView>(R.id.n_product_tittle)
        val textView2 by bindView<TextView>(R.id.n_product_price)
        val imageView by bindView<ImageView>(R.id.n_product_image)
    }
}
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

class CategoryAdapter (
    val category: String,
    val holded: (String) -> Boolean
) : ItemAdapter<CategoryAdapter.ViewHolder>(R.layout.item_product_categories) {
    override fun onCreateViewHolder(itemView: View) = ViewHolder(itemView)
    override fun ViewHolder.onBindViewHolder() { // 2
        textView.text = category
        itemView.setOnLongClickListener {
            holded(category)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val textView by bindView<TextView>(R.id.b_category)
    }
}
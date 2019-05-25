package com.example.kalepa.Adapters

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.kalepa.Preferences.SharedApp
import com.example.kalepa.R
import com.example.kalepa.common.ItemAdapter
import com.example.kalepa.common.bindView
import com.example.kalepa.common.loadImage
import com.example.kalepa.models.Trade
import com.example.kalepa.models.User

class TradeAdapter (
    val trade: Trade,
    val clicked: (Trade) -> Unit
) : ItemAdapter<TradeAdapter.ViewHolder>(R.layout.item_person_chat) {
    override fun onCreateViewHolder(itemView: View) = ViewHolder(itemView)
    override fun ViewHolder.onBindViewHolder() { // 2
        if(trade.seller_id.equals(SharedApp.prefs.userId.toString())) {
            textView1.text = "Venta"
            textView1.setTextColor(Color.parseColor("#0066ff"))
        } else {
            textView1.text = "Compra"
            textView1.setTextColor(Color.parseColor("#ff0000"))
        }
        textView2.text = trade.other_nick
        textView3.text = trade.product_title

        imageView1.loadImage(trade.other_avatar)
        imageView2.loadImage(trade.product_img)
        itemView.setOnClickListener { clicked(trade) }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val textView1 by bindView<TextView>(R.id.n_ipc_selbuy)
        val textView2 by bindView<TextView>(R.id.creatorNameTextView)
        val textView3 by bindView<TextView>(R.id.m_PersonChat_product_name)
        val imageView1 by bindView<ImageView>(R.id.coverCircleImageView)
        val imageView2 by bindView<ImageView>(R.id.m_PersonChat_product_image)

    }
}
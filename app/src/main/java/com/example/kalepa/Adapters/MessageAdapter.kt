package com.example.kalepa.Adapters

import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.RatingBar
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.kalepa.Preferences.SharedApp
import com.example.kalepa.R
import com.example.kalepa.common.ItemAdapter
import com.example.kalepa.common.bindView
import com.example.kalepa.models.Message
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.wrapContent

class MessageAdapter (
    val message: Message
) : ItemAdapter<MessageAdapter.ViewHolder>(R.layout.item_text_message) {
    override fun onCreateViewHolder(itemView: View) = ViewHolder(itemView)
    override fun ViewHolder.onBindViewHolder() { // 2
        body.text = message.body
        date.text = message.date.substring(0,19)
        if (message.nick.equals(SharedApp.prefs.username)) {
            root.apply {
                backgroundResource = R.drawable.rect_round_white
                val lParams = FrameLayout.LayoutParams(wrapContent, wrapContent,Gravity.END)
                this.layoutParams = lParams
            }
        } else {
            root.apply {
                backgroundResource = R.drawable.rect_round_primary_color
                val lParams = FrameLayout.LayoutParams(wrapContent, wrapContent,Gravity.START)
                this.layoutParams = lParams
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val body by bindView<TextView>(R.id.textView_message_text)
        val date by bindView<TextView>(R.id.textView_message_time)
        val root by bindView<RelativeLayout>(R.id.message_root)
    }
}
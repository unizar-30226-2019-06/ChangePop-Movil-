package com.example.kalepa.Adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.example.kalepa.R
import com.example.kalepa.common.ItemAdapter
import com.example.kalepa.common.bindView
import com.example.kalepa.models.Notification

class NotificationAdapter (
    val notification: Notification,
    val clickedBody: (Notification) -> Unit,
    val clickedDelete: (Notification) -> Unit
) : ItemAdapter<NotificationAdapter.ViewHolder>(R.layout.item_user_notifications) {
    override fun onCreateViewHolder(itemView: View) = ViewHolder(itemView)
    override fun ViewHolder.onBindViewHolder() { // 2
        textView1.text = notification.category
        textView2.text = notification.text
        layout.setOnClickListener { clickedBody(notification) }
        button.setOnClickListener { clickedDelete(notification) }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val textView1 by bindView<TextView>(R.id.m_notification_category)
        val textView2 by bindView<TextView>(R.id.m_notification_text)
        val layout by bindView<LinearLayout>(R.id.m_notification_body)
        val button by bindView<Button>(R.id.m_notification_delete)
    }
}
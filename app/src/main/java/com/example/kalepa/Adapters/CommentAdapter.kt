package com.example.kalepa.Adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import com.example.kalepa.R
import com.example.kalepa.common.ItemAdapter
import com.example.kalepa.common.bindView
import com.example.kalepa.models.Comment
import com.example.kalepa.models.Notification

class CommentAdapter (
    val comment: Comment
) : ItemAdapter<CommentAdapter.ViewHolder>(R.layout.item_user_notifications) {
    override fun onCreateViewHolder(itemView: View) = ViewHolder(itemView)
    override fun ViewHolder.onBindViewHolder() { // 2
        rating.rating = comment.points.toFloat()
        textView.text = comment.body
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val rating by bindView<RatingBar>(R.id.m_comment_points)
        val textView by bindView<TextView>(R.id.m_comment_body)
    }
}
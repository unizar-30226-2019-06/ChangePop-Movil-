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
import com.example.kalepa.models.User

class UserAdapter (
    val user: User,
    val clicked: (User) -> Unit
) : ItemAdapter<UserAdapter.ViewHolder>(R.layout.item_person_chat) {
    override fun onCreateViewHolder(itemView: View) = ViewHolder(itemView)
    override fun ViewHolder.onBindViewHolder() { // 2
        textView.text = user.Nick
        imageView.loadImage(user.Avatar) // 3
        itemView.setOnClickListener { clicked(user) }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val textView by bindView<TextView>(R.id.creatorNameTextView)
        val imageView by bindView<ImageView>(R.id.coverCircleImageView)
    }
}
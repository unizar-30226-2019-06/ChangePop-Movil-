package com.example.kalepa

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_chat_list.*

class ChatListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)

        n_chatlist_to_chat.setOnClickListener {
            ChatActivity.start(this)
        }
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, ChatListActivity::class.java)
            context.startActivity(intent)
        }
    }
}

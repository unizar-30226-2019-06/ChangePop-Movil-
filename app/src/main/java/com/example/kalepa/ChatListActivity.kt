package com.example.kalepa

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import com.example.charactermanager.MainListAdapter
import com.example.kalepa.Adapters.UserAdapter
import com.example.kalepa.models.User
import kotlinx.android.synthetic.main.activity_chat_list.*
import java.util.*

class ChatListActivity : AppCompatActivity() {

    private val users = listOf(
        User(id = 0, nick = "Pedro", first_name = "", last_name = "",  mail = "", phone = "", is_mod = false,
             ban_reason = "", points = 0.0,
             avatar =  "http://tcrew.be/wp-content/uploads/2015/04/page-demarage.jpg",
             fnac = "", dni = "", place = ""),
        User(id = 0, nick = "Sandra", first_name = "", last_name = "",  mail = "", phone = "", is_mod = false,
            ban_reason = "", points = 0.0,
            avatar =  "https://ak9.picdn.net/shutterstock/videos/1285789/thumb/1.jpg?i10c=img.resize(height:160)",
            fnac = "", dni = "", place = "")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)

        n_recyclerView_cl.layoutManager = GridLayoutManager(this, 1)

        show(users)
    }

    private fun show(items: List<User>) {
        val categoryItemAdapters = items.map(this::createCategoryItemAdapter)
        n_recyclerView_cl.adapter = MainListAdapter(categoryItemAdapters)
    }

    private fun createCategoryItemAdapter(user: User)
            = UserAdapter(user,
        { showCharacterProfile(user) })

    private fun showCharacterProfile(user: User) {
        ChatActivity.start(this)
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, ChatListActivity::class.java)
            context.startActivity(intent)
        }
    }
}

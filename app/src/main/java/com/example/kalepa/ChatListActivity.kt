package com.example.kalepa

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.widget.TextView
import com.example.charactermanager.MainListAdapter
import com.example.kalepa.Adapters.TradeAdapter
import com.example.kalepa.Preferences.SharedApp
import com.example.kalepa.models.Trade
import com.example.kalepa.models.User
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import kotlinx.android.synthetic.main.activity_chat_list.*
import org.jetbrains.anko.toast
import org.json.JSONArray
import org.json.JSONObject

class ChatListActivity : AppCompatActivity() {

    private var trades = ArrayList<Trade>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)

        n_recyclerView_cl.layoutManager = GridLayoutManager(this, 1)

        n_swipeRefreshView_cl.setOnRefreshListener {
            trades.clear()
            loadTrades()
            n_swipeRefreshView_cl.isRefreshing = false
        }

        loadTrades()
    }

    private fun loadTrades() {
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.progress_dialog,null)
        val message = dialogView.findViewById<TextView>(R.id.message)
        message.text = "Cargando intercambios..."
        builder.setView(dialogView)
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()

        val url = MainActivity().projectURL + "/trades"

        val req = url.httpGet().header(Pair("Cookie", SharedApp.prefs.cookie))
        req.responseJson { request, response, result ->
            when (result) {
                is Result.Failure -> {
                    dialog.dismiss()
                    toast("Error cargando sus intercambios, intentelo de nuevo más tarde")
                }
                is Result.Success -> {
                    Initialize(result.value)
                    dialog.dismiss()
                }
            }
        }
    }

    private fun show(items: List<Trade>) {
        val categoryItemAdapters = items.map(this::createCategoryItemAdapter)
        n_recyclerView_cl.adapter = MainListAdapter(categoryItemAdapters)
    }

    private fun createCategoryItemAdapter(trade: Trade)
            = TradeAdapter(trade,
        { showTrade(trade) })

    private fun showTrade(trade: Trade) {
        ChatActivity.start(this, trade)
    }

    private fun Initialize (jsonProducts: JSONObject) {
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.progress_dialog,null)
        val message = dialogView.findViewById<TextView>(R.id.message)
        message.text = "Cargando intercambios..."
        builder.setView(dialogView)
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()

        val length = jsonProducts.get("length").toString().toInt()
        val list = jsonProducts.get("list")
        if (list is JSONArray) {
            for (i in 0 until length) {
                var trade = Trade()
                trade.fromJSON(list.getJSONObject(i))
                trades.add(trade)
            }
        }

        if (list is JSONArray) {
            loadUsers(0, length, list)
        }

        dialog.dismiss()
    }

    private fun loadUsers(i: Int, lenght: Int, list: JSONArray) {
        if (i == lenght) {
            loadProducts(0,lenght,list)
        } else {
            var url: String
            if(trades[i].seller_id.equals(SharedApp.prefs.userId.toString())) {
                url = MainActivity().projectURL + "/user/" + trades[i].buyer_id
            } else {
                url = MainActivity().projectURL + "/user/" + trades[i].seller_id
            }
            val req = url.httpGet().header(Pair("Cookie", SharedApp.prefs.cookie))
            req.responseJson { request, response, result ->
                when (result) {
                    is Result.Success -> {
                        val jsonUser = result.value
                        trades[i].other_avatar = jsonUser.get("avatar").toString()
                        trades[i].other_nick = jsonUser.get("nick").toString()
                        loadUsers(i+1,lenght,list)
                    }
                    is Result.Failure -> {
                        loadUsers(i+1,lenght,list)
                    }
                }
            }
        }
    }

    private fun loadProducts(i: Int, lenght: Int, list: JSONArray) {
        if (i == lenght) {
            show(trades)
        } else {
            val url2 = MainActivity().projectURL + "/product/" + trades[i].product_id
            val req2 = url2.httpGet().header(Pair("Cookie", SharedApp.prefs.cookie))
            req2.responseJson { request, response, result ->
                when (result) {
                    is Result.Success -> {
                        val jsonProduct = result.value
                        trades[i].product_img = jsonProduct.get("main_img").toString()
                        loadProducts(i+1, lenght, list)
                    }
                    is Result.Failure -> {
                        loadProducts(i+1, lenght, list)
                    }
                }
            }
        }
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, ChatListActivity::class.java)
            context.startActivity(intent)
        }
    }
}

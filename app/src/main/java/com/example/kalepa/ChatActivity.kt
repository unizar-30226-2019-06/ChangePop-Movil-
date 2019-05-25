package com.example.kalepa

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.widget.TextView
import com.example.charactermanager.MainListAdapter
import com.example.kalepa.Adapters.MessageAdapter
import com.example.kalepa.Preferences.SharedApp
import com.example.kalepa.common.extra
import com.example.kalepa.common.getIntent
import com.example.kalepa.common.loadImage
import com.example.kalepa.models.Message
import com.example.kalepa.models.Trade
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import kotlinx.android.synthetic.main.activity_chat.*
import org.jetbrains.anko.toast
import org.json.JSONArray
import org.json.JSONObject

class ChatActivity : AppCompatActivity() {

    val trade: Trade by extra(TRADE_ARG)

    private var messages = ArrayList<Message>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        m_Chat_product_name.text = trade.product_title
        m_Chat_price.text = trade.price.toString()
        m_Chat_product_image.loadImage(trade.product_img)

        m_Chat_send_message.setOnClickListener {
            sendMessage()
        }

        m_recyclerView_chat.layoutManager = GridLayoutManager(this, 1)

        loadMessages()
    }

    private fun loadMessages () {

        val url = MainActivity().projectURL + "/msgs/" + trade.id

        val req = url.httpGet().header(Pair("Cookie", SharedApp.prefs.cookie))
        req.responseJson { request, response, result ->
            when (result) {
                is Result.Failure -> {
                    toast("Error cargando mensajes, intentelo de nuevo más tarde")
                }
                is Result.Success -> {
                    Initialize(result.value)
                }
            }
        }
        show(messages)
    }

    private fun show(items: List<Message>) {
        val categoryItemAdapters = items.map(this::createCategoryItemAdapter)
        m_recyclerView_chat.adapter = MainListAdapter(categoryItemAdapters)
        m_recyclerView_chat.scrollToPosition(messages.size - 1)
    }

    private fun createCategoryItemAdapter(message: Message)
            = MessageAdapter(message)


    private fun Initialize (jsonProducts: JSONObject) {
        val length = jsonProducts.get("length").toString().toInt()
        val list = jsonProducts.get("list")
        if (list is JSONArray) {
            messages.clear()
            for (i in 0 until length) {
                var message = Message()
                message.fromJSON(list.getJSONObject(i))
                messages.add(message)
            }
        }
        show(messages)
    }

    private fun sendMessage() {
        if (!m_Chat_editText_message.text.toString().equals("")) {

            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.progress_dialog,null)
            val message = dialogView.findViewById<TextView>(R.id.message)
            message.text = "Enviando Mensaje..."
            builder.setView(dialogView)
            builder.setCancelable(false)
            val dialog = builder.create()
            dialog.show()

            val jsonObject = JSONObject()
            jsonObject.accumulate("body", m_Chat_editText_message.text.toString())

            val url = MainActivity().projectURL + "/msgs/" + trade.id

            val req = url.httpPost().body(jsonObject.toString()).header(Pair("Cookie", SharedApp.prefs.cookie))
            req.httpHeaders["Content-Type"] = "application/json"

            req.responseJson { request, response, result ->
                when (result) {
                    is Result.Failure -> {
                        dialog.dismiss()
                        toast("No se ha podido enviar el mensaje, inténtelo más tarde")
                    }
                    is Result.Success -> {
                        dialog.dismiss()
                        loadMessages()
                    }
                }
            }
        }
    }

    companion object {
        private const val bullet = '\u2022'
        private  const val TRADE_ARG = "com.example.kalepa.ChatActivity.TradeArgKey"

        fun getIntent(context: Context, trade: Trade) = context
            .getIntent<ChatActivity>()
            .apply { putExtra(TRADE_ARG, trade) }

        fun start(context: Context, trade: Trade) {
            val intent = getIntent(context, trade)
            context.startActivity(intent)
        }
    }
}

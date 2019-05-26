package com.example.kalepa

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.widget.*
import com.example.charactermanager.MainListAdapter
import com.example.kalepa.Adapters.ChatProductAdapter
import com.example.kalepa.Adapters.MessageAdapter
import com.example.kalepa.Adapters.RawProductAdapter
import com.example.kalepa.Preferences.SharedApp
import com.example.kalepa.common.extra
import com.example.kalepa.common.getIntent
import com.example.kalepa.common.loadImage
import com.example.kalepa.models.Message
import com.example.kalepa.models.RawProduct
import com.example.kalepa.models.Trade
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.coroutines.delay
import org.jetbrains.anko.activityUiThread
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.json.JSONArray
import org.json.JSONObject

class ChatActivity : AppCompatActivity() {

    val trade: Trade by extra(TRADE_ARG)

    val offeredProducs: ArrayList<RawProduct> = ArrayList<RawProduct>()

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

        m_swipeRefreshView_chat.isEnabled = false

        n_chat_header.setOnClickListener {
            showOffer()
        }

        val url = MainActivity().projectURL + "/trade/" + trade.id

        val req = url.httpGet().header(Pair("Cookie", SharedApp.prefs.cookie))
        req.responseJson { request, response, result ->
            when (result) {
                is Result.Failure -> {
                    ChatListActivity.start(this)
                    toast("Error cargando el intercambio")
                }
                is Result.Success -> {
                    loadProducts(result.value)
                }
            }
        }
    }

    private fun chatLoop () {
        doAsync {
            while(true) {
                activityUiThread {
                    loadMessages()
                }
                Thread.sleep(2000)
            }
        }
    }

    private fun loadProducts (jsonProducts: JSONObject) {
        var id_list = ArrayList<String>()
        val aux = jsonProducts.get("products_offer").toString()
        val separate = aux.split("""(,\)\",\"\()|(\[\"\()|(,\)\"\])""".toRegex())
        if (separate.size >= 2) {
            id_list = ArrayList(separate.slice(IntRange(1, separate.size - 2)))
        } else {
            id_list.clear()
        }

        getProduct(0, id_list.size, id_list)
    }

    private fun getProduct(i: Int, lenght: Int, list: ArrayList<String>) {
        if (i == lenght) {
            chatLoop()
        } else {

            val url = MainActivity().projectURL + "/product/" + list[i]

            val req = url.httpGet().header(Pair("Cookie", SharedApp.prefs.cookie))
            req.responseJson { request, response, result ->
                when (result) {
                    is Result.Success -> {
                        val product = RawProduct()
                        product.fromJSON(result.value)
                        offeredProducs.add(product)
                        getProduct(i+1,lenght,list)
                    }
                }
            }

        }
    }

    private fun loadMessages () {

        val oldMessages = messages.size
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
    }

    private fun show(items: List<Message>) {
        val categoryItemAdapters = items.map(this::createCategoryItemAdapter)
        m_recyclerView_chat.adapter = MainListAdapter(categoryItemAdapters)
        m_recyclerView_chat.scrollToPosition(messages.size - 1)
    }

    private fun createCategoryItemAdapter(message: Message)
            = MessageAdapter(message)


    private fun Initialize (jsonProducts: JSONObject) {
        val oldMessages = messages.size
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
        if (messages.size != oldMessages) {
            show(messages)
        }
        m_swipeRefreshView_chat.isRefreshing = false
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
                        m_Chat_editText_message.setText("")
                        toast("No se ha podido enviar el mensaje, inténtelo más tarde")
                    }
                    is Result.Success -> {
                        dialog.dismiss()
                        m_Chat_editText_message.setText("")
                        loadMessages()
                    }
                }
            }
        }
    }

    private fun showOffer() {
        val view = layoutInflater.inflate(R.layout.dialog_trade_info, null)

        val window = PopupWindow(
            view, // Custom view to show in popup window
            LinearLayout.LayoutParams.WRAP_CONTENT, // Width of popup window
            LinearLayout.LayoutParams.WRAP_CONTENT // Window height
        )
        window.isFocusable = true

        //Blur the background
        val fcolorNone = ColorDrawable(resources.getColor(R.color.transparent))
        val fcolorBlur = ColorDrawable(resources.getColor(R.color.transparentDark))
        n_chat_container.foreground = fcolorBlur

        window.showAtLocation(
            n_chat_header, // Location to display popup window
            Gravity.CENTER, // Exact position of layout to display popup
            0, // X offset
            0 // Y offset
        )

        val confirm = view.findViewById<Button>(R.id.m_trade_popup_close_button)
        val edit = view.findViewById<Button>(R.id.m_trade_popup_edit_button)
        val rv = view.findViewById<RecyclerView>(R.id.n_recyclerView_chat_trade)
        val srv = view.findViewById<SwipeRefreshLayout>(R.id.n_swipeRefreshView_chat_trade)
        val price = view.findViewById<EditText>(R.id.m_trade_popup_money)

        rv.layoutManager = GridLayoutManager(this, 1)
        val categoryItemAdapters = offeredProducs.map(this::createCategoryItemAdapter)
        rv.adapter = MainListAdapter(categoryItemAdapters)

        srv.isEnabled = false

        price.setText(trade.price.toString())

        confirm.setOnClickListener {

        }

        edit.setOnClickListener {

        }

        window.setOnDismissListener {
            n_chat_container.foreground = fcolorNone
        }

        true
    }

    private fun createCategoryItemAdapter(product: RawProduct)
            = ChatProductAdapter(product)


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

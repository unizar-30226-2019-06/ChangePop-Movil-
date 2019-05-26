package com.example.kalepa

import android.app.AlertDialog
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.widget.TextView
import com.example.charactermanager.MainListAdapter
import com.example.kalepa.Adapters.OfferAdapter
import com.example.kalepa.Preferences.SharedApp
import com.example.kalepa.common.extra
import com.example.kalepa.common.getIntent
import com.example.kalepa.models.Product
import com.example.kalepa.models.RawProduct
import com.example.kalepa.models.Trade
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.httpPut
import com.github.kittinunf.result.Result
import kotlinx.android.synthetic.main.activity_edit_trade.*
import org.jetbrains.anko.toast
import org.json.JSONArray
import org.json.JSONObject

class EditTradeActivity : AppCompatActivity() {

    private var products = ArrayList<RawProduct>()
    private var productIds = ArrayList<String>()
    private var product =  Product()

    val trade: Trade by extra(TRADE_ARG)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_trade)

        n_recyclerView_edit_trade_products.layoutManager = GridLayoutManager(this, 1)

        n_swipeRefreshView_edit_trade_products.setOnRefreshListener {
            products.clear()
            loadProducts()
            n_swipeRefreshView_edit_trade_products.isRefreshing = false
        }

        loadProducts()
    }

    private fun loadProducts()  {
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.progress_dialog,null)
        val message = dialogView.findViewById<TextView>(R.id.message)
        message.text = "Cargando productos..."
        builder.setView(dialogView)
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()

        val url = MainActivity().projectURL + "/products/" + SharedApp.prefs.userId.toString()

        val req = url.httpGet().header(Pair("Cookie", SharedApp.prefs.cookie))
        req.responseJson { request, response, result ->
            when (result) {
                is Result.Failure -> {
                    dialog.dismiss()
                    toast("Error cargando sus productos, intentelo de nuevo más tarde")
                }
                is Result.Success -> {
                    Initialize(result.value)
                    dialog.dismiss()
                }
            }
        }
    }

    private fun show(items: List<RawProduct>) {
        val categoryItemAdapters = items.map(this::createCategoryItemAdapter)
        n_recyclerView_edit_trade_products.adapter = MainListAdapter(categoryItemAdapters)
    }

    private fun createCategoryItemAdapter(product: RawProduct)
            = OfferAdapter(product,
        { updateIdList(product) } )

    private fun updateIdList(product: RawProduct){
        val p_id = product.id.toString()
        if (productIds.contains(p_id)) {
            productIds.remove(p_id)
        } else {
            productIds.add(p_id)
        }
    }

    private fun Initialize (jsonProducts: JSONObject) {
        val length = jsonProducts.get("length").toString().toInt()
        val list = jsonProducts.get("list")
        if (list is JSONArray){
            for (i in 0 until length) {
                var product = RawProduct()
                product.fromJSON(list.getJSONObject(i))
                products.add(product)
            }
        }
        show(products)

        n_edit_trade_buy_button.setOnClickListener {
            updateTrade()
        }

        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.progress_dialog,null)
        val message = dialogView.findViewById<TextView>(R.id.message)
        message.text = "Cargando productos..."
        builder.setView(dialogView)
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()

        val product_id: String? = trade.product_id.toString()

        val url = MainActivity().projectURL + "/product/" + product_id

        val req = url.httpGet().header(Pair("Cookie", SharedApp.prefs.cookie))
        req.responseJson { request, response, result ->
            when (result) {
                is Result.Failure -> {
                    dialog.dismiss()
                    toast("Error cargando sus productos, intentelo de nuevo más tarde")
                }
                is Result.Success -> {
                    product.fromJSON(result.value)
                    dialog.dismiss()
                }
            }
        }
    }

    private fun updateTrade() {
        if (checkFields()) {
            val trade_id = trade.id

            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.progress_dialog,null)
            val message = dialogView.findViewById<TextView>(R.id.message)
            message.text = "Actualizando intercambio..."
            builder.setView(dialogView)
            builder.setCancelable(false)
            val dialog = builder.create()
            dialog.show()


            val jsonArray = JSONArray()
            for( s in productIds ) {
                jsonArray.put(s)
            }

            var offeredMoney: Float = 0.0f
            if (!n_edit_trade_offer.text.toString().isNullOrEmpty()){
                offeredMoney = n_edit_trade_offer.text.toString().toFloat()
            }

            val jsonObject = JSONObject()
            jsonObject.accumulate("price", offeredMoney)
            jsonObject.accumulate("products", jsonArray)

            val url = MainActivity().projectURL + "/trade/" + trade_id + "/offer"

            val req = url.httpPut().body(jsonObject.toString()).header(Pair("Cookie", SharedApp.prefs.cookie))
            req.httpHeaders["Content-Type"] = "application/json"

            req.response { request, response, result ->
                when (result) {
                    is Result.Failure -> {
                        dialog.dismiss()
                        toast("La oferta no es válida")
                    }
                    is Result.Success -> {
                        dialog.dismiss()
                        trade.price = offeredMoney.toDouble()
                        ChatActivity.start(this, trade)
                        toast("Intercambio actualizado")
                    }
                }
            }
        }
    }


    private fun checkFields(): Boolean {

        var right = true

        var offeredMoney: Float = 0.0f
        if (!n_edit_trade_offer.text.toString().isNullOrEmpty()){
            offeredMoney = n_edit_trade_offer.text.toString().toFloat()
        }
        val price = product.price.toFloat()

        if (productIds.size == 0) {
            if (offeredMoney < price) {
                n_edit_trade_offer.error = "Si no se ofrecen productos, se debe ofrecer una cantidad de dinero igual o mayor que el precio del producto"
                right = false
            }
        }

        return right
    }

    companion object {
        private const val bullet = '\u2022'
        private  const val TRADE_ARG = "com.example.kalepa.EditTradeActivity.TradeArgKey"

        fun getIntent(context: Context, trade: Trade) = context
            .getIntent<EditTradeActivity>()
            .apply { putExtra(TRADE_ARG, trade) }

        fun start(context: Context, trade: Trade) {
            val intent = getIntent(context, trade)
            context.startActivity(intent)
        }
    }
}

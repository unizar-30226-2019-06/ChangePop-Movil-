package com.example.kalepa.Fragments

import android.app.AlertDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.charactermanager.MainListAdapter
import com.example.kalepa.*
import com.example.kalepa.Adapters.RawProductAdapter
import com.example.kalepa.Preferences.SharedApp
import com.example.kalepa.models.Product
import com.example.kalepa.models.RawProduct
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.support.v4.toast
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class WelcomeScreenFragment: Fragment() {

    /*private val products = listOf(
        Product(id = 0, descript = "Un boli", user_id = 0, price = 25.38, categories = arrayListOf("uno","dos"),
            title = "Boligrafo", bid_date = "", boost_date = "", visits = 0, followers = 0, publish_date = "2019-01-11",
            main_img = "https://www.kalamazoo.es/content/images/product/28023-1_1_xnl.jpg",
            photo_urls = arrayListOf("https://www.kalamazoo.es/content/images/product/28023-1_1_xnl.jpg",
                "https://www.motociclismo.es/media/cache/big/upload/images/article/24729/article-por-que-no-arranca-moto-frio-577662e2620df.jpg",
                "https://i.imgur.com/m4i2rWD.jpg"),
            place = "PepeCity", ban_reason = ""),
        Product(id = 1, descript = "Una Moto guay", user_id = 0, price = 999.38, categories = arrayListOf("Vehiculos"),
            title = "Moto", bid_date = "", boost_date = "", visits = 0, followers = 0, publish_date = "2019-01-11",
            main_img = "https://www.motociclismo.es/media/cache/big/upload/images/article/24729/article-por-que-no-arranca-moto-frio-577662e2620df.jpg",
            photo_urls = arrayListOf("https://www.motociclismo.es/media/cache/big/upload/images/article/24729/article-por-que-no-arranca-moto-frio-577662e2620df.jpg"),
            place = "", ban_reason = ""),
        Product(id = 2, descript = "Una consola guay", user_id = 0, price = 99.02, categories = arrayListOf("Ocio"),
            title = "VideoConsola", bid_date = "", boost_date = "", visits = 0, followers = 0, publish_date = "2019-01-11",
            main_img = "https://rukminim1.flixcart.com/image/704/704/gamingconsole/c/r/4/xbox-360-4-microsoft-wireless-controller-original-imaeqcvaagdaax27.jpeg?q=70",
            photo_urls = arrayListOf("https://rukminim1.flixcart.com/image/704/704/gamingconsole/c/r/4/xbox-360-4-microsoft-wireless-controller-original-imaeqcvaagdaax27.jpeg?q=70"),
            place = "", ban_reason = ""),
        Product(id = 3, descript = "Fucking Noctis", user_id = 0, price = 29.42, categories = arrayListOf("Decoración"),
            title = "Poster FFXV", bid_date = "", boost_date = "", visits = 0, followers = 0, publish_date = "2019-01-11",
            main_img = "https://i.imgur.com/m4i2rWD.jpg",
            photo_urls = arrayListOf("https://i.imgur.com/m4i2rWD.jpg"),
            place = "", ban_reason = ""),
        Product(id = 4, descript = "Furrula guay", user_id = 0, price = 2009.14, categories = arrayListOf("Vehiculos"),
            title = "Tractor Verde", bid_date = "", boost_date = "", visits = 0, followers = 0, publish_date = "2019-01-11",
            main_img = "https://www.rbauction.es/cms_assets/category_images/11007679861/11007679861_W_S.jpg",
            photo_urls = arrayListOf("https://www.rbauction.es/cms_assets/category_images/11007679861/11007679861_W_S.jpg"),
            place = "", ban_reason = "")
    )*/
    private var products = ArrayList<RawProduct>()

    companion object {
        fun newInstance(): WelcomeScreenFragment {
            return WelcomeScreenFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.content_main, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        n_recyclerView_ws.layoutManager = GridLayoutManager(context!!, 2)

        val builder = AlertDialog.Builder(context)
        val dialogView = layoutInflater.inflate(R.layout.progress_dialog,null)
        val message = dialogView.findViewById<TextView>(R.id.message)
        message.text = "Cargando productos..."
        builder.setView(dialogView)
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()

        val url = MainActivity().projectURL + "/products"

        val req = url.httpGet().header(Pair("Cookie", SharedApp.prefs.cookie))
        req.responseJson { request, response, result ->
            when (result) {
                is Result.Failure -> {
                    dialog.dismiss()
                    toast("Error cargando productos, intentelo de nuevo más tarde")
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
        n_recyclerView_ws.adapter = MainListAdapter(categoryItemAdapters)
    }

    private fun createCategoryItemAdapter(product: RawProduct)
            = RawProductAdapter(product,
        { showCharacterProfile(product) })

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
    }

    private fun showCharacterProfile(product: RawProduct) {
        if (product.isBid()) {
            ProductBidActivity.start(context!!, product.id.toString())
        } else {
            ProductBuyActivity.start(context!!, product.id.toString())
        }
    }
}
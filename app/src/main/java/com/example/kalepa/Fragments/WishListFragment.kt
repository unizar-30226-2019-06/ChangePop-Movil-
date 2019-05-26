package com.example.kalepa.Fragments

import android.app.AlertDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.charactermanager.MainListAdapter
import com.example.kalepa.Adapters.RawProductAdapter
import com.example.kalepa.MainActivity
import com.example.kalepa.Preferences.SharedApp
import com.example.kalepa.ProductBidActivity
import com.example.kalepa.ProductBuyActivity
import com.example.kalepa.R
import com.example.kalepa.models.RawProduct
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import kotlinx.android.synthetic.main.fragment_wishlist.*
import org.jetbrains.anko.support.v4.toast
import org.json.JSONArray
import org.json.JSONObject

class WishListFragment: Fragment() {

    private var products = ArrayList<RawProduct>()

    companion object {
        fun newInstance(): WishListFragment {
            return WishListFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_wishlist, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        n_recyclerView_wish.layoutManager = GridLayoutManager(context!!, 2)

        n_swipeRefreshView_wish.setOnRefreshListener {
            products.clear()
            loadProducts()
            n_swipeRefreshView_wish.isRefreshing = false
        }

        loadProducts()
    }

    private fun loadProducts() {
        val builder = AlertDialog.Builder(context)
        val dialogView = layoutInflater.inflate(R.layout.progress_dialog,null)
        val message = dialogView.findViewById<TextView>(R.id.message)
        message.text = "Cargando productos..."
        builder.setView(dialogView)
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()

        val url = MainActivity().projectURL + "/user/follows"

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
        n_recyclerView_wish.adapter = MainListAdapter(categoryItemAdapters)
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
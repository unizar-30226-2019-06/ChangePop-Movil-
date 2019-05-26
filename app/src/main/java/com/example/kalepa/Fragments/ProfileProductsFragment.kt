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
import com.example.kalepa.Adapters.ProductAdapter
import com.example.kalepa.MainActivity
import com.example.kalepa.Preferences.SharedApp
import com.example.kalepa.R
import com.example.kalepa.UpdateProductActivity
import com.example.kalepa.models.Product
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import kotlinx.android.synthetic.main.fragment_profile_products.*
import org.jetbrains.anko.support.v4.toast
import org.json.JSONArray
import org.json.JSONObject

class ProfileProductsFragment: Fragment() {

    private var products = ArrayList<Product>()

    companion object {
        fun newInstance(): ProfileProductsFragment {
            return ProfileProductsFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_profile_products, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        n_recyclerView_fpp.layoutManager = GridLayoutManager(context!!, 2)

        n_swipeRefreshView_fpp.setOnRefreshListener {
            products.clear()
            loadProducts()
            n_swipeRefreshView_fpp.isRefreshing = false
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

    private fun show(items: List<Product>) {
        val categoryItemAdapters = items.map(this::createCategoryItemAdapter)
        n_recyclerView_fpp.adapter = MainListAdapter(categoryItemAdapters)
    }

    private fun createCategoryItemAdapter(product: Product)
            = ProductAdapter(product,
        { showCharacterProfile(product) })

    private fun showCharacterProfile(product: Product) {
        UpdateProductActivity.start(context!!, product.id.toString())
    }

    private fun Initialize (jsonProducts: JSONObject) {
        val length = jsonProducts.get("length").toString().toInt()
        val list = jsonProducts.get("list")
        if (list is JSONArray){
            for (i in 0 until length) {
                var product = Product()
                product.fromJSON(list.getJSONObject(i))
                products.add(product)
            }
        }
        show(products)
    }

}
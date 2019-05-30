package com.example.kalepa.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.kalepa.MainActivity
import com.example.kalepa.Preferences.SharedApp
import com.example.kalepa.R
import com.example.kalepa.models.RawProduct
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import kotlinx.android.synthetic.main.fragment_search.*
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.toast
import org.json.JSONArray
import org.json.JSONObject

class SearchFragment: Fragment() {

    private var category: String = ""
    private var serverCategories = ArrayList<String>()

    companion object {
        fun newInstance(): SearchFragment {
            return SearchFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_search, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val builder = AlertDialog.Builder(context!!)
        val dialogView = layoutInflater.inflate(R.layout.progress_dialog,null)
        val message = dialogView.findViewById<TextView>(R.id.message)
        message.text = "Cargando..."
        builder.setView(dialogView)
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()

        val url = MainActivity().projectURL + "/categories"

        val req = url.httpGet().header(Pair("Cookie", SharedApp.prefs.cookie))
        req.responseJson { request, response, result ->
            when (result) {
                is Result.Failure -> {
                    toast("Error cargando categorías")
                    dialog.dismiss()
                    MainActivity.start(context!!)
                }
                is Result.Success -> {
                    val jsonObject = result.value
                    val aux = jsonObject.get("list").toString()
                    val separate = aux.split("""(\",\")|(\[\")|(\"\])""".toRegex())
                    serverCategories = ArrayList(separate.slice(IntRange(1,separate.size-2)))
                    dialog.dismiss()
                }
            }
        }

        m_search_category_button.setOnClickListener{
            showCategoryDialog()
        }

        m_button_search.setOnClickListener {
            searchProducts()
        }
    }

    private fun searchProducts() {
        val builder = AlertDialog.Builder(context!!)
        val dialogView = layoutInflater.inflate(R.layout.progress_dialog,null)
        val message = dialogView.findViewById<TextView>(R.id.message)
        message.text = "Buscando productos..."
        builder.setView(dialogView)
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()

        val title = m_search_title.text.toString()
        var min = 0
        var max = 0
        val descript = m_search_descript.text.toString()
        val category = m_search_category.text.toString()
        val place = m_search_place.text.toString()

        if(!m_search_priece_min.text.toString().equals("")) {
            min = m_search_priece_min.text.toString().toInt()
        }

        if(!m_search_priece_max.text.toString().equals("")) {
            max = m_search_priece_max.text.toString().toInt()
        }

        val jsonObject = JSONObject()


        jsonObject.accumulate("price_min", min)

        if (max != 0 && max > min) {
            jsonObject.accumulate("price_max", max)
        }

        if(!descript.equals("")){
            jsonObject.accumulate("descript", descript)

        }

        if(!category.equals("")){
            jsonObject.accumulate("category", category)

        }

        if(!place.equals("")){
            jsonObject.accumulate("place", place)
        }

        val url = MainActivity().projectURL + "/search/products/adv"

        val req = url.httpPost().body(jsonObject.toString()).header(Pair("Cookie", SharedApp.prefs.cookie))
        req.httpHeaders["Content-Type"] = "application/json"

        req.responseJson { request, response, result ->
            when (result) {
                is Result.Failure -> {
                    dialog.dismiss()
                    toast("Error en la búsqueda")
                }
                is Result.Success -> {
                    dialog.dismiss()
                    loadProducts(result.value)
                }
            }
        }
    }

    private fun loadProducts (jsonProducts: JSONObject) {
        val products = ArrayList<RawProduct>()
        val length = jsonProducts.get("length").toString().toInt()
        val list = jsonProducts.get("list")
        if (list is JSONArray){
            for (i in 0 until length) {
                var product = RawProduct()
                product.fromJSON(list.getJSONObject(i))
                products.add(product)
            }
        }
        val fragment = SearchProductListFragment.newInstance(products)
        openFragment(fragment)
    }

    private fun showCategoryDialog(){
        lateinit var dialog: AlertDialog

        val items = arrayOfNulls<String>(serverCategories.size)
        for (i in 0 until serverCategories.size) {
            items.set(i,serverCategories[i])
        }

        val builder = AlertDialog.Builder(context!!)

        with(builder) {
            setTitle("Elija categoría")
            setItems(items) { dialog, which ->
                m_search_category.setText(items[which])
            }
        }

        dialog = builder.create()
        dialog.show()
    }

    private fun openFragment(fragment: Fragment) {
        val transaction = this.activity!!.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.main_fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

}
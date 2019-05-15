package com.example.kalepa.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.charactermanager.MainListAdapter
import com.example.kalepa.Adapters.ProductAdapter
import com.example.kalepa.ProductActivity
import com.example.kalepa.R
import com.example.kalepa.models.Product
import kotlinx.android.synthetic.main.content_main.*
import java.util.*
import kotlin.collections.ArrayList

class WelcomeScreenFragment: Fragment() {

    private val products = listOf(
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
    )

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

        show(products)
    }

    private fun show(items: List<Product>) {
        val categoryItemAdapters = items.map(this::createCategoryItemAdapter)
        n_recyclerView_ws.adapter = MainListAdapter(categoryItemAdapters)
    }

    private fun createCategoryItemAdapter(product: Product)
            = ProductAdapter(product,
        { showCharacterProfile(product) })

    private fun showCharacterProfile(product: Product) {
        ProductActivity.start(context!!, product)
    }
}
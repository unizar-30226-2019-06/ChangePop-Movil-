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

class WelcomeScreenFragment: Fragment() {

    private val products = listOf(
        Product(ID = 0, Name = "Boligrafo", Description = "Un boli", Price = 25.38,
            Upload_date = Date(2019, 1, 11), Blocked = false, Bid_expire = Date(), Visits = 0,
            Images = arrayListOf("https://www.kalamazoo.es/content/images/product/28023-1_1_xnl.jpg",
                "https://www.motociclismo.es/media/cache/big/upload/images/article/24729/article-por-que-no-arranca-moto-frio-577662e2620df.jpg",
                "https://i.imgur.com/m4i2rWD.jpg"),
            Boosted = false, Followers = 0, Email = "pepe@pepe.es", Deleted = false,
            Localization = "PepeCity", OwnerNick = "pepe"),
        Product(ID = 1, Name = "Moto", Description = "", Price = 299.99, Upload_date = Date(), Blocked = false,
            Bid_expire = Date(), Visits = 0,
            Images = arrayListOf("https://www.motociclismo.es/media/cache/big/upload/images/article/24729/article-por-que-no-arranca-moto-frio-577662e2620df.jpg"),
            Boosted = false, Followers = 0, Email = "", Deleted = false, Localization = "", OwnerNick = ""),
        Product(ID = 2, Name = "Videoconsola", Description = "", Price = 99.99, Upload_date = Date(), Blocked = false,
            Bid_expire = Date(), Visits = 0,
            Images = arrayListOf("https://rukminim1.flixcart.com/image/704/704/gamingconsole/c/r/4/xbox-360-4-microsoft-wireless-controller-original-imaeqcvaagdaax27.jpeg?q=70"),
            Boosted = false, Followers = 0, Email = "", Deleted = false, Localization = "", OwnerNick = ""),
        Product(ID = 3, Name = "Poster FF XV", Description = "", Price = 19.59, Upload_date = Date(), Blocked = false,
            Bid_expire = Date(), Visits = 0,
            Images = arrayListOf("https://i.imgur.com/m4i2rWD.jpg"),
            Boosted = false, Followers = 0, Email = "", Deleted = false, Localization = "", OwnerNick = ""),
        Product(ID = 4, Name = "Tractor Verde", Description = "", Price = 2000.0, Upload_date = Date(), Blocked = false,
            Bid_expire = Date(), Visits = 0,
            Images = arrayListOf("https://www.rbauction.es/cms_assets/category_images/11007679861/11007679861_W_S.jpg"),
            Boosted = false, Followers = 0, Email = "", Deleted = false, Localization = "", OwnerNick = "")
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
package com.example.kalepa.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.charactermanager.MainListAdapter
import com.example.kalepa.Adapters.RawProductAdapter
import com.example.kalepa.ProductBidActivity
import com.example.kalepa.ProductBuyActivity
import com.example.kalepa.R
import com.example.kalepa.models.RawProduct
import kotlinx.android.synthetic.main.fragment_search_product_list.*

class SearchProductListFragment: Fragment() {

    private var products = ArrayList<RawProduct>()

    companion object {
        fun newInstance(products: ArrayList<RawProduct>): SearchProductListFragment {
            val myFragment = SearchProductListFragment()
            val args = Bundle()
            args.putParcelableArrayList("products", products)
            myFragment.arguments = args
            return myFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_search_product_list, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        n_recyclerView_search.layoutManager = GridLayoutManager(context!!, 2)

        n_swipeRefreshView_search.isEnabled = false

        products = arguments!!.getParcelableArrayList("products")

        show(products)
    }

    private fun show(items: List<RawProduct>) {
        val categoryItemAdapters = items.map(this::createCategoryItemAdapter)
        n_recyclerView_search.adapter = MainListAdapter(categoryItemAdapters)
    }

    private fun createCategoryItemAdapter(product: RawProduct)
            = RawProductAdapter(product,
        { showCharacterProfile(product) })

    private fun showCharacterProfile(product: RawProduct) {
        if (product.isBid()) {
            ProductBidActivity.start(context!!, product.id.toString())
        } else {
            ProductBuyActivity.start(context!!, product.id.toString())
        }
    }
}
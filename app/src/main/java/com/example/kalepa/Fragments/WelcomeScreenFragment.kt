package com.example.kalepa.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.charactermanager.MainListAdapter
import com.example.charactermanager.common.ItemAdapter
import com.example.charactermanager.common.RecyclerListAdapter
import com.example.kalepa.ProductActivity
import com.example.kalepa.R
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*

class WelcomeScreenFragment: Fragment() {
/*
    private val objetos = listOf( // 1
        "objeto1",
        "objeto2")
    )*/

    companion object {
        fun newInstance(): WelcomeScreenFragment {
            return WelcomeScreenFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.content_main, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        content_main_producto.setOnClickListener {
            ProductActivity.start(context!!)
        }
/*
        val rv : RecyclerView = R.id.recyclerView_main
        inflater.inflate(R.layout.activity_main, container, false)
        rv.layoutManager = GridLayoutManager(getActivity()?.getApplicationContext(), 2)
        val categoryItemAdapters = objetos.map(::ItemAdapter)
        rv.adapter = MainListAdapter(categoryItemAdapters)*/

        //ItemAdapter<CharacterItemAdapter.ViewHolder

    }
}
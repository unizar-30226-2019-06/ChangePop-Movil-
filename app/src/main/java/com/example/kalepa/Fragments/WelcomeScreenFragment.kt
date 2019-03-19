package com.example.kalepa.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.kalepa.ProductActivity
import com.example.kalepa.R
import kotlinx.android.synthetic.main.content_main.*

class WelcomeScreenFragment: Fragment() {

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
    }
}
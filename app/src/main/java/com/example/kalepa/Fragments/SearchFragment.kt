package com.example.kalepa.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kalepa.R

class SearchFragment: Fragment() {

    companion object {
        fun newInstance(): SearchFragment {
            return SearchFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_search, container, false)

    /* USAR PARA ESTABLECER FUNCIONALIDADES, BOTONES, ETC
    override fun onActivityCreated(savedInstanceState: Bundle?) {

    }*/
}
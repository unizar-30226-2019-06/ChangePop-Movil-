package com.example.kalepa.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kalepa.R

class ProfileOpinionsFragment: Fragment() {

    companion object {
        fun newInstance(): ProfileOpinionsFragment {
            return ProfileOpinionsFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_profile_opinions, container, false)

    /* USAR PARA ESTABLECER FUNCIONALIDADES, BOTONES, ETC
    override fun onActivityCreated(savedInstanceState: Bundle?) {

    }*/
}
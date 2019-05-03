package com.example.kalepa.Adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.example.kalepa.Fragments.ProfileOpinionsFragment
import com.example.kalepa.Fragments.ProfileSelledFragment

class ViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment? {
        var fragment: Fragment? = null
        if (position == 0) {
            fragment = ProfileSelledFragment()
        } else if (position == 1) {
            fragment = ProfileOpinionsFragment()
        }
        return fragment
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        var title: String? = null
        if (position == 0) {
            title = "Productos en Venta"
        } else if (position == 1) {
            title = "Opiniones"
        }
        return title
    }
}
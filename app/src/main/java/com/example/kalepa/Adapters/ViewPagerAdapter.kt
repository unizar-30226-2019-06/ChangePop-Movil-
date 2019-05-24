package com.example.kalepa.Adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.example.kalepa.Fragments.ProfileCommentsFragment
import com.example.kalepa.Fragments.ProfileSelledFragment

class ViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private var user_id = 0

    override fun getItem(position: Int): Fragment? {
        var fragment: Fragment? = null
        if (position == 0) {
            fragment = ProfileSelledFragment.newInstance(user_id)
        } else if (position == 1) {
            fragment = ProfileCommentsFragment.newInstance(user_id)
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

    fun setUser_id (id: Int) {
        user_id = id
    }
}
package com.example.kalepa

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.example.kalepa.Fragments.ProfileDataFragment
import com.example.kalepa.Fragments.ProfileNotificationsFragment
import com.example.kalepa.Fragments.ProfileProductsFragment
import com.example.kalepa.Preferences.SharedApp
import kotlinx.android.synthetic.main.activity_self_profile.*
import org.jetbrains.anko.toast

class SelfProfileActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.n_profile_navigation_data -> {
                val profileDataFragment = ProfileDataFragment.newInstance()
                openFragment(profileDataFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.n_profile_navigation_products -> {
                val profileProductsFragment = ProfileProductsFragment.newInstance()
                openFragment(profileProductsFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.n_profile_navigation_notifications -> {
                val profileNotificationsFragment = ProfileNotificationsFragment.newInstance()
                openFragment(profileNotificationsFragment)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.n_profile_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_self_profile)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        val profileDataFragment = ProfileDataFragment.newInstance()
        openFragment(profileDataFragment)
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, SelfProfileActivity::class.java)
            context.startActivity(intent)
        }
    }
}

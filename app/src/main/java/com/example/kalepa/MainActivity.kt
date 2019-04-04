package com.example.kalepa

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import com.example.kalepa.Fragments.SearchFragment
import com.example.kalepa.Fragments.SettingsFragment
import com.example.kalepa.Fragments.WelcomeScreenFragment
import com.example.kalepa.Fragments.WishListFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object {
        var logged = false

        fun start(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }

        fun setLogged() {
            logged = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!logged) {
            LoginActivity.start(this)
        }

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        val header_view = nav_view.getHeaderView(0)
        val profile = header_view.findViewById<ImageView>(R.id.navigation_header_profile_image)
        profile.setOnClickListener{
            ProfileActivity.start(this)
        }

        val welcomeScreenFragment = WelcomeScreenFragment.newInstance()
        openFragment(welcomeScreenFragment)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_explorar_articulos -> {
                val welcomeScreenFragment = WelcomeScreenFragment.newInstance()
                openFragment(welcomeScreenFragment)
            }
            R.id.nav_busqueda_filtrada -> {
                val searchFragment = SearchFragment.newInstance()
                openFragment(searchFragment)
            }
            R.id.nav_lista_de_deseos -> {
                val wishListFragment = WishListFragment.newInstance()
                openFragment(wishListFragment)
            }
            R.id.nav_configuracion -> {
                val settingsFragment = SettingsFragment.newInstance()
                openFragment(settingsFragment)
            }
            R.id.nav_subir_articulo -> {
                UploadActivity.start(this)
            }
            R.id.nav_chat -> {
                ChatListActivity.start(this)
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.main_fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}

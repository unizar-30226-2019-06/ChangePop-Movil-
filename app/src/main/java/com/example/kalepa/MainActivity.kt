package com.example.kalepa

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import com.example.kalepa.Fragments.SearchFragment
import com.example.kalepa.Fragments.SettingsFragment
import com.example.kalepa.Fragments.WelcomeScreenFragment
import com.example.kalepa.Fragments.WishListFragment
import com.example.kalepa.Preferences.SharedApp
import com.example.kalepa.common.loadImage
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import org.jetbrains.anko.toast
import org.json.JSONObject

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    val projectURL = "https://kelpa-api.herokuapp.com"

    companion object {

        fun start(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkSesion(0, MySqlHelper(this).fetchCookies())
    }

    private fun checkSesion (index: Int, list: ArrayList<Pair<Int,String>>) {

        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.progress_dialog,null)
        val message = dialogView.findViewById<TextView>(R.id.message)
        message.text = "Comprobando usuario..."
        builder.setView(dialogView)
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()

        if (index > list.size - 1){
            dialog.dismiss()
            LoginActivity.start(this)
        } else {
            val (id, cookie) = list[index]
            val url = MainActivity().projectURL + "/user"

            val req = url.httpGet().header(Pair("Cookie", cookie))
            req.responseJson { request, response, result ->
                when (result) {
                    is Result.Failure -> {
                        MySqlHelper(this).deleteCookie(id)
                        checkSesion(index + 1, list)
                    }
                    is Result.Success -> {
                        SharedApp.prefs.cookie = cookie
                        initialize(result.value)
                        dialog.dismiss()
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
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
            R.id.nav_log_out -> {

                val builder = AlertDialog.Builder(this)
                val dialogView = layoutInflater.inflate(R.layout.progress_dialog,null)
                val message = dialogView.findViewById<TextView>(R.id.message)
                message.text = "Cerrando sesión..."
                builder.setView(dialogView)
                builder.setCancelable(false)
                val dialog = builder.create()
                dialog.show()

                val url = MainActivity().projectURL + "/logout"

                val req = url.httpGet().header(Pair("Cookie", SharedApp.prefs.cookie))
                req.responseJson { request, response, result ->
                    when (result) {
                        is Result.Failure -> {
                            dialog.dismiss()
                            toast("Error al cerrar sesión")
                        }
                        is Result.Success -> {
                            dialog.dismiss()
                            MainActivity.start(this)
                        }
                    }
                }

                MySqlHelper(this).clearCookies()
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

    private fun initialize (jsonUser: JSONObject) {

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        val welcomeScreenFragment = WelcomeScreenFragment.newInstance()
        openFragment(welcomeScreenFragment)

        nav_view.setNavigationItemSelectedListener(this)
        val header_view = nav_view.getHeaderView(0)
        val profile = header_view.findViewById<ImageView>(R.id.navigation_header_profile_image)
        val uname = header_view.findViewById<TextView>(R.id.n_navHead_uname)
        val umail = header_view.findViewById<TextView>(R.id.n_navHead_umail)

        profile.setOnClickListener {
            SelfProfileActivity.start(this)
        }

        uname.setText(jsonUser.get("nick").toString())
        umail.setText(jsonUser.get("mail").toString())
        profile.loadImage(jsonUser.get("avatar").toString())
        SharedApp.prefs.userPlace = jsonUser.get("place").toString()
        SharedApp.prefs.userId = jsonUser.get("id").toString().toInt()
        SharedApp.prefs.isMod = jsonUser.get("is_mod").toString().toBoolean()
        SharedApp.prefs.username = jsonUser.get("nick").toString()

    }

}

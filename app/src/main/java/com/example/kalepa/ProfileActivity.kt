package com.example.kalepa

import android.app.AlertDialog
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.kalepa.Adapters.ViewPagerAdapter
import com.example.kalepa.Preferences.SharedApp
import com.example.kalepa.common.getIntent
import com.example.kalepa.common.loadImage
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import kotlinx.android.synthetic.main.activity_profile.*
import org.jetbrains.anko.toast
import org.json.JSONObject

class ProfileActivity : AppCompatActivity() {

    internal lateinit var viewpageradapter:ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        viewpageradapter= ViewPagerAdapter(supportFragmentManager)

        this.viewPager.adapter=viewpageradapter  //Binding PagerAdapter with ViewPager
        this.n_other_profile_navigation.setupWithViewPager(this.viewPager)



        //ESTO HAY QUE PONERLO BIEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEN
        val userId = intent.getStringExtra(USER_ARG)

        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.progress_dialog,null)
        val message = dialogView.findViewById<TextView>(R.id.message)
        message.text = "Cargando datos..."
        builder.setView(dialogView)
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()

        val url = MainActivity().projectURL + "/user"
        val req = url.httpGet().header(Pair("Cookie", SharedApp.prefs.cookie))

        req.responseJson { request, response, result ->
            when (result) {
                is Result.Failure -> {
                    dialog.dismiss()
                    toast("Error cargando el perfil")
                }
                is Result.Success -> {
                    setUserInfo(result.value)
                    dialog.dismiss()
                }
            }
        }
    }

    private fun setUserInfo(jsonUser: JSONObject) {
        b_profile_username.setText(jsonUser.get("nick").toString())
        b_profile_image.loadImage("https://st.depositphotos.com/2868925/3523/v/950/depositphotos_35236487-stock-illustration-vector-male-profile-image.jpg")
        //b_profile_image.loadImage(jsonUser.get("avatar").toString())
        //b_profile_extrainf.setText(jsonUser.get("").toString())
        b_profile_place.setText(jsonUser.get("place").toString())
        //b_profile_rating..setText(jsonUser.get("").toString())
    }


    companion object {
        private  const val USER_ARG = "com.example.kalepa.ProfileActivity.userArgKey"

        fun getIntent(context: Context, userId: String) = context
            .getIntent<ProfileActivity>()
            .apply { putExtra(USER_ARG, userId) }

        fun start(context: Context, userId: String) {
            val intent = getIntent(context, userId)
            context.startActivity(intent)
        }
    }
}

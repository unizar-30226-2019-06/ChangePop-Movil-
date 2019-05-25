package com.example.kalepa

import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.example.kalepa.Adapters.ViewPagerAdapter
import com.example.kalepa.Preferences.SharedApp
import com.example.kalepa.common.getIntent
import com.example.kalepa.common.loadImage
import com.example.kalepa.models.User
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.httpPut
import com.github.kittinunf.result.Result
import kotlinx.android.synthetic.main.activity_profile.*
import org.jetbrains.anko.toast
import org.json.JSONObject

class ProfileActivity : AppCompatActivity() {

    private val user = User()
    internal lateinit var viewpageradapter:ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val userId = intent.getStringExtra(USER_ARG)

        viewpageradapter= ViewPagerAdapter(supportFragmentManager)
        viewpageradapter.setUser_id(userId.toInt())

        this.viewPager.adapter=viewpageradapter  //Binding PagerAdapter with ViewPager
        this.n_other_profile_navigation.setupWithViewPager(this.viewPager)

        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.progress_dialog,null)
        val message = dialogView.findViewById<TextView>(R.id.message)
        message.text = "Cargando datos..."
        builder.setView(dialogView)
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()

        val url = MainActivity().projectURL + "/user/" + userId
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
        user.fromJSON(jsonUser)

        b_profile_username.setText(user.nick)
        b_profile_image.loadImage(user.avatar)
        //b_profile_extrainf.setText(jsonUser.get("").toString())
        b_profile_place.setText(user.place)
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (SharedApp.prefs.isMod) {
            val inflater = menuInflater
            inflater.inflate(R.menu.mod_user_menu, menu)
            return true
        }
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.n_mum_banear -> {
            banUser()
            true
        }

        R.id.n_mum_mod -> {
            giveMod()
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun banUser() {
        val view = layoutInflater.inflate(R.layout.dialog_ban_user, null)

        val window = PopupWindow(
            view, // Custom view to show in popup window
            LinearLayout.LayoutParams.WRAP_CONTENT, // Width of popup window
            LinearLayout.LayoutParams.WRAP_CONTENT // Window height
        )
        window.isFocusable = true

        //Blur the background
        val fcolorNone = ColorDrawable(resources.getColor(R.color.transparent))
        val fcolorBlur = ColorDrawable(resources.getColor(R.color.transparentDark))
        n_profile_container.foreground = fcolorBlur

        window.showAtLocation(
            n_profile_header, // Location to display popup window
            Gravity.CENTER, // Exact position of layout to display popup
            0, // X offset
            0 // Y offset
        )

        val cancel = view.findViewById<Button>(R.id.n_dbu_cancelar)
        val ban = view.findViewById<Button>(R.id.n_dbu_banear)
        val reason = view.findViewById<EditText>(R.id.n_dbu_reason)
        val date = view.findViewById<EditText>(R.id.n_dbu_date)

        ban.setOnClickListener {
            if (chekFields(reason,date)) {

                window.dismiss()
            }
        }

        cancel.setOnClickListener {
            window.dismiss()
        }

        window.setOnDismissListener {
            n_profile_container.foreground = fcolorNone
        }

        true
    }

    private fun chekFields(reason: EditText, date: EditText): Boolean {
        var right = true

        if (reason.text.toString().isEmpty()) {
            reason.error = "Debe especificarse un motivo de baneo"
            right = false
        }

        val regex = """[1-2][0-9][0-9][0-9]-[0-1][0-9]-[0-3][0-9]""".toRegex()
        if (!regex.matches(date.text.toString())) {
            date.error = "El formato debe ser aaaa-mm-dd"
            right = false
        }

        return right
    }


    private fun giveMod() {
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.progress_dialog,null)
        val message = dialogView.findViewById<TextView>(R.id.message)
        message.text = "Procesando operación..."
        builder.setView(dialogView)
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()

        val url = MainActivity().projectURL + "/user/" + user.id.toString() + "/mod"

        val req = url.httpPut().header(Pair("Cookie", SharedApp.prefs.cookie))
        req.response { request, response, result ->
            when (result) {
                is Result.Failure -> {
                    dialog.dismiss()
                    toast("Error en el proceso, intentelo de nuevo más tarde")
                }
                is Result.Success -> {
                    dialog.dismiss()
                    toast("El usuario es ahora moderador")
                }
            }
        }
    }

}

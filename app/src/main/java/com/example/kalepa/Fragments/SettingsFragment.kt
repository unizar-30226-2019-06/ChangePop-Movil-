package com.example.kalepa.Fragments

import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.kalepa.LoginActivity
import com.example.kalepa.MainActivity
import com.example.kalepa.Preferences.SharedApp
import com.example.kalepa.R
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.httpDelete
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.httpPut
import com.github.kittinunf.result.Result
import kotlinx.android.synthetic.main.fragment_settings.*
import org.jetbrains.anko.support.v4.toast
import org.json.JSONObject

class SettingsFragment: Fragment() {

    companion object {
        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_settings, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        n_settings_delete_user.setOnClickListener {
            checkPassword()
        }
    }

    private fun checkPassword() {
        val view = layoutInflater.inflate(R.layout.dialog_password, null)

        val window = PopupWindow(
            view, // Custom view to show in popup window
            LinearLayout.LayoutParams.WRAP_CONTENT, // Width of popup window
            LinearLayout.LayoutParams.WRAP_CONTENT // Window height
        )
        window.isFocusable = true

        //Blur the background
        val fcolorNone = ColorDrawable(resources.getColor(R.color.transparent))
        val fcolorBlur = ColorDrawable(resources.getColor(R.color.transparentDark))
        n_settings_container.foreground = fcolorBlur

        window.showAtLocation(
            n_settings_header, // Location to display popup window
            Gravity.CENTER, // Exact position of layout to display popup
            0, // X offset
            0 // Y offset
        )

        val cancel = view.findViewById<Button>(R.id.n_dp_cancel)
        val delete = view.findViewById<Button>(R.id.n_dp_delete)
        val password = view.findViewById<EditText>(R.id.n_dp_password)

        delete.setOnClickListener {
            if (password.text.toString().equals("")) {
                toast("Introduzca la contraseña")
            } else {

                val builder = AlertDialog.Builder(context!!)
                val dialogView = layoutInflater.inflate(R.layout.progress_dialog,null)
                val message = dialogView.findViewById<TextView>(R.id.message)
                message.text = "Comprobando contraseña..."
                builder.setView(dialogView)
                builder.setCancelable(false)
                val dialog = builder.create()
                dialog.show()

                val url = MainActivity().projectURL + "/login"

                val jsonObject = JSONObject()
                jsonObject.accumulate("nick", SharedApp.prefs.username)
                jsonObject.accumulate("pass", password.text.toString())
                jsonObject.accumulate("remember", false)

                val req = url.httpPost().body(jsonObject.toString())
                req.httpHeaders["Content-Type"] = "application/json"

                req.response { request, response, result ->
                    when (result) {
                        is Result.Failure -> {
                            dialog.dismiss()
                            toast("La contraseña no es correcta")
                        }
                        is Result.Success -> {
                            dialog.dismiss()
                            deleteUser()
                        }
                    }
                }

                window.dismiss()
            }
        }

        cancel.setOnClickListener {
            window.dismiss()
        }

        window.setOnDismissListener {
            n_settings_container.foreground = fcolorNone
        }

        true
    }

    private fun deleteUser() {

        val builder = AlertDialog.Builder(context)
        val dialogView = layoutInflater.inflate(R.layout.progress_dialog,null)
        val message = dialogView.findViewById<TextView>(R.id.message)
        message.text = "Eliminando cuenta..."
        builder.setView(dialogView)
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()

        val url = MainActivity().projectURL + "/user"

        val req = url.httpDelete().header(Pair("Cookie", SharedApp.prefs.cookie))
        req.response { request, response, result ->
            when (result) {
                is Result.Failure -> {
                    toast("No se ha podido eliminar la cuenta, intentelo más tarde")
                }
                is Result.Success -> {
                    LoginActivity.start(context!!)
                    toast("Exito: verificación enviada al correo")
                }
            }
        }
    }
}
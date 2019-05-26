package com.example.kalepa.Fragments

import android.app.AlertDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.kalepa.LoginActivity
import com.example.kalepa.MainActivity
import com.example.kalepa.Preferences.SharedApp
import com.example.kalepa.R
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.httpDelete
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import kotlinx.android.synthetic.main.fragment_settings.*
import org.jetbrains.anko.support.v4.toast

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
            deleteUser()
        }
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
package com.example.kalepa

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.toast
import org.json.JSONObject


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        m_login_button_login.setOnClickListener {
            if(logUser()) {
                MainActivity.start(this)
            }
        }

        m_login_button_register.setOnClickListener {
            RegisterActivity.start(this)
        }
    }

    private fun logUser () : Boolean {
        val url = MainActivity().projectURL + "/login"

        val jsonObject = JSONObject()
        jsonObject.accumulate("nick", m_userEditText.text.toString())
        jsonObject.accumulate("pass", m_passwordEditText.text.toString())
        jsonObject.accumulate("remember", m_CheckBox.isChecked)

        val req = url.httpPost().body(jsonObject.toString())
        req.httpHeaders["Content-Type"] = "application/json"
        var exito = false

        req.response { request, response, result ->
                when (result) {
                    is Result.Failure -> {
                        toast("Usuario o contraseña no validos")
                    }
                    is Result.Success -> {
                        MySqlHelper(this).addCookie(response.httpResponseHeaders["Set-Cookie"]!![0])
                        MainActivity.start(this)
                        exito = true
                    }
                }
            }

        return exito
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
        }
    }
}

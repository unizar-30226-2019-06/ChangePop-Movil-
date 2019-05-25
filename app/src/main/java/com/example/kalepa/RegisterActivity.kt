package com.example.kalepa

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.toast
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        m_crear_Button.setOnClickListener {
            regUser()
        }
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, RegisterActivity::class.java)
            context.startActivity(intent)
        }
    }

    private fun regUser () {

        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.progress_dialog,null)
        val message = dialogView.findViewById<TextView>(R.id.message)
        message.text = "Comprobando datos..."
        builder.setView(dialogView)
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()

        val url = MainActivity().projectURL + "/user"

        if (check_fields()) {
            val jsonObject = JSONObject()
            jsonObject.accumulate("nick", m_crear_usernameEditText.text.toString())
            jsonObject.accumulate("first_name", m_firstnameEditText.text.toString())
            jsonObject.accumulate("last_name", m_lastnameEditText.text.toString())
            jsonObject.accumulate("mail", m_crear_mailEditText.text.toString())
            jsonObject.accumulate("pass", m_crear_passwordEditText.text.toString())
            jsonObject.accumulate("phone", m_phoneEditText.text.toString())
            jsonObject.accumulate("fnac", m_birthDateEditText.text.toString())
            jsonObject.accumulate("dni", m_IdentifiedCardEditText.text.toString())
            jsonObject.accumulate("place", m_placeEditText.text.toString())

            val req = url.httpPost().body(jsonObject.toString())
            req.httpHeaders["Content-Type"] = "application/json"

            req.response { request, response, result ->
                when (result) {
                    is Result.Failure -> {
                        dialog.dismiss()
                        toast("Ya existe otro usuario con ese nombre")
                    }
                    is Result.Success -> {
                        dialog.dismiss()
                        LoginActivity.start(this)
                    }
                }
            }
        } else {
            dialog.dismiss()
        }
    }

    private fun check_fields () : Boolean {

        var right = true

        if (m_crear_usernameEditText.text.toString().isEmpty()) {
            m_crear_usernameEditText.error = "El campo no puede ser vacio"
            right = false
        }

        if (m_firstnameEditText.text.toString().isEmpty()) {
            m_firstnameEditText.error = "El campo no puede ser vacio"
            right = false
        }

        if (m_lastnameEditText.text.toString().isEmpty()) {
            m_lastnameEditText.error = "El campo no puede ser vacio"
            right = false
        }
        if (m_crear_mailEditText.text.toString().isEmpty()) {
            m_crear_mailEditText.error = "El campo no puede ser vacio"
            right = false
        } else {
            val regex = """.+@.+\..+""".toRegex()
            if (!regex.matches(m_crear_mailEditText.text.toString())) {
                m_crear_mailEditText.error = "El email introducido no es válido"
                right = false
            }
        }

        if (!m_crear_mailEditText.text.toString().equals(m_crear_mailconfirmEditText.text.toString())){
            m_crear_mailconfirmEditText.error = "Los emails no coinciden"
            right = false
        }

        if (m_crear_passwordEditText.text.toString().isEmpty()) {
            m_crear_passwordEditText.error = "El campo no puede ser vacio"
            right = false
        } else {
            if (6 > m_crear_passwordEditText.text.toString().length || 12 < m_crear_passwordEditText.text.toString().length) {
                m_crear_passwordEditText.error = "La contraseña debe tener entre 6 y 12 caracteres"
                right = false
            }
        }

        if (!m_crear_passwordEditText.text.toString().equals(m_crear_password_confirmEditText.text.toString())){
            m_crear_password_confirmEditText.error = "Las contraseñas no coinciden"
            right = false
        }

        if (m_phoneEditText.text.toString().length < 9) {
            m_phoneEditText.error = "Este número no es válido"
            right = false
        }

        val regex = """[1-2][0-9][0-9][0-9]-[0-1][0-9]-[0-3][0-9]""".toRegex()
        if (!regex.matches(m_birthDateEditText.text.toString())) {
            m_birthDateEditText.error = "El formato debe ser aaaa-mm-dd"
            right = false
        }

        if (m_IdentifiedCardEditText.text.toString().length < 8) {
            m_IdentifiedCardEditText.error = "Este DNI no es válido"
            right = false
        }
        if (m_placeEditText.text.toString().isEmpty()) {
            m_placeEditText.error = "El campo no puede ser vacio"
            right = false
        }

        return right
    }

}

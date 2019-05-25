package com.example.kalepa.Fragments

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.kalepa.MainActivity
import com.example.kalepa.Preferences.SharedApp
import com.example.kalepa.R
import com.example.kalepa.common.loadImage
import com.example.kalepa.models.User
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPut
import com.github.kittinunf.result.Result
import kotlinx.android.synthetic.main.fragment_profile_data.*
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.toast
import org.json.JSONObject
import java.io.File

class ProfileDataFragment: Fragment() {

    private val user: User = User()

    private var selectedAction = 0
    private val FINAL_TAKE_PHOTO = 1
    private val FINAL_CHOOSE_PHOTO = 2
    private var imageUri: Uri? = null
    private var imagePath: String? = null

    companion object {
        fun newInstance(): ProfileDataFragment {
            return ProfileDataFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_profile_data, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val builder = AlertDialog.Builder(context)
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
                    MainActivity.start(context!!)
                }
                is Result.Success -> {
                    setFields(result.value)
                    dialog.dismiss()
                }
            }
        }

        m_ProfileData_editarCampos.setOnClickListener {
            updateUser()
        }

        m_crear_coverCircleImageView.setOnClickListener {
            showImageDialog()
        }
    }

    private fun setFields(jsonUser: JSONObject) {
        user.fromJSON(jsonUser)

        m_crear_coverCircleImageView.loadImage(user.avatar)

        m_ProfileData_username.setText(user.nick)
        m_ProfileData_mail.setText(user.mail)
        m_ProfileData_desc.setText(user.desc)
        m_ProfileData_firstname.setText(user.first_name)
        m_ProfileData_lastname.setText(user.last_name)
        m_ProfileData_birthDate.setText(user.fnac)
        m_ProfileData_place.setText(user.place)
        m_ProfileData_phone.setText(user.phone)
        m_ProfileData_IdentifiedCard.setText(user.dni)

    }

    private fun updateUser() {

        val builder = AlertDialog.Builder(context)
        val dialogView = layoutInflater.inflate(R.layout.progress_dialog, null)
        val message = dialogView.findViewById<TextView>(R.id.message)
        message.text = "Cargando datos..."
        builder.setView(dialogView)
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()

        if (checkFields()) {
            loadFields()

            val jsonObject = user.toJSON()

            val url = MainActivity().projectURL + "/user"

            val req = url.httpPut().body(jsonObject.toString()).header(Pair("Cookie", SharedApp.prefs.cookie))
            req.httpHeaders["Content-Type"] = "application/json"
            req.responseJson { request, response, result ->
                when (result) {
                    is Result.Failure -> {
                        dialog.dismiss()
                        toast("Error al actualizar datos")
                    }
                    is Result.Success -> {
                        dialog.dismiss()
                        toast("Perfil actualizado")
                    }
                }
            }
        } else {
            dialog.dismiss()
        }
    }

    private fun loadFields() {
        user.nick = m_ProfileData_username.text.toString()
        user.mail = m_ProfileData_mail.text.toString()
        user.desc = m_ProfileData_desc.text.toString()
        user.first_name = m_ProfileData_firstname.text.toString()
        user.last_name = m_ProfileData_lastname.text.toString()
        user.fnac = m_ProfileData_birthDate.text.toString()
        user.place = m_ProfileData_place.text.toString()
        user.phone = m_ProfileData_phone.text.toString()
        user.dni = m_ProfileData_IdentifiedCard.text.toString()
    }

    private fun checkFields () : Boolean {

        var right = true

        if (m_ProfileData_username.text.toString().isEmpty()) {
            m_ProfileData_username.error = "El campo no puede ser vacio"
            right = false
        }

        if (m_ProfileData_desc.text.toString().isEmpty()) {
            m_ProfileData_desc.error = "El campo no puede ser vacio"
            right = false
        }

        if (m_ProfileData_firstname.text.toString().isEmpty()) {
            m_ProfileData_firstname.error = "El campo no puede ser vacio"
            right = false
        }

        if (m_ProfileData_lastname.text.toString().isEmpty()) {
            m_ProfileData_lastname.error = "El campo no puede ser vacio"
            right = false
        }
        if (m_ProfileData_mail.text.toString().isEmpty()) {
            m_ProfileData_mail.error = "El campo no puede ser vacio"
            right = false
        } else {
            val regex = """.+@.+\..+""".toRegex()
            if (!regex.matches(m_ProfileData_mail.text.toString())) {
                m_ProfileData_mail.error = "El email introducido no es válido"
                right = false
            }
        }

        if (m_ProfileData_phone.text.toString().length < 9) {
            m_ProfileData_phone.error = "Este número no es válido"
            right = false
        }

        val regex = """[1-2][0-9][0-9][0-9]-[0-1][0-9]-[1-3][0-9]""".toRegex()
        if (!regex.matches(m_ProfileData_birthDate.text.toString())) {
            m_ProfileData_birthDate.error = "El formato debe ser aaaa-mm-dd"
            right = false
        }

        if (m_ProfileData_IdentifiedCard.text.toString().length < 8) {
            m_ProfileData_IdentifiedCard.error = "Este DNI no es válido"
            right = false
        }
        if (m_ProfileData_place.text.toString().isEmpty()) {
            m_ProfileData_place.error = "El campo no puede ser vacio"
            right = false
        }

        return right
    }

    private fun showImageDialog(){
        lateinit var dialog: android.support.v7.app.AlertDialog

        val items = arrayOf("Galería","Camara")
        val builder = android.support.v7.app.AlertDialog.Builder(context!!)
        var selected = "Galería"
        builder.setTitle("¿De donde desea conseguir la imagen?")

        builder.setItems(items) { dialog, which ->
            selected = items[which]
            if (which == 0){    //GALERÍA
                selectedAction = 0
                val checkSelfPermission = ContextCompat.checkSelfPermission(context!!, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                if (checkSelfPermission != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this.requireActivity(), arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                }
                else{
                    openAlbum()
                }
            } else {            //CAMARA
                selectedAction = 1
                val checkSelfPermission = ContextCompat.checkSelfPermission(context!!, android.Manifest.permission.CAMERA)
                val checkReadPermission = ContextCompat.checkSelfPermission(context!!, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                if (checkSelfPermission != PackageManager.PERMISSION_GRANTED && checkReadPermission != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this.requireActivity(), arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                    ), 1)
                }
                else{
                    openCamera()
                }
            }
        }

        // Set the positive/yes button click listener
        builder.setPositiveButton("OK") { _, _ -> }

        dialog = builder.create()
        dialog.show()
    }

    private fun openCamera() {
        val outputImage = File(this.requireActivity().externalCacheDir, "output_image.jpg")
        if(outputImage.exists()) {
            outputImage.delete()
        }
        outputImage.createNewFile()
        imageUri = if(Build.VERSION.SDK_INT >= 24){
            FileProvider.getUriForFile(context!!, "com.example.kalepa.fileprovider", outputImage)
        } else {
            Uri.fromFile(outputImage)
        }

        val intent = Intent("android.media.action.IMAGE_CAPTURE")
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, FINAL_TAKE_PHOTO)
    }

    private fun openAlbum(){
        val intent = Intent("android.intent.action.GET_CONTENT")
        intent.type = "image/*"
        startActivityForResult(intent, FINAL_CHOOSE_PHOTO)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            1 ->
                if (grantResults.isNotEmpty() && grantResults[0] ==PackageManager.PERMISSION_GRANTED){
                    if (selectedAction == 0) {
                        openAlbum()
                    } else {
                        openCamera()
                    }
                }
                else {
                    toast("Has denegado el permiso")
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            FINAL_TAKE_PHOTO ->
                if (resultCode == Activity.RESULT_OK) {

                    val builder = android.support.v7.app.AlertDialog.Builder(context!!)
                    val dialogView = layoutInflater.inflate(R.layout.progress_dialog,null)
                    val message = dialogView.findViewById<TextView>(R.id.message)
                    message.text = "Subiendo Imagen..."
                    builder.setView(dialogView)
                    builder.setCancelable(false)
                    val dialog = builder.create()
                    dialog.show()

                    val urlA = MainActivity().projectURL + "/upload"

                    AndroidNetworking.upload(urlA)
                        .addHeaders("Content-Type", "multipart/form-data")
                        .addHeaders("Cookie", SharedApp.prefs.cookie)
                        .addMultipartFile("file", File(this.requireActivity().externalCacheDir, "output_image.jpg"))
                        .build().getAsJSONObject(object : JSONObjectRequestListener {
                            override fun onResponse(response: JSONObject) {
                                user.avatar = MainActivity().projectURL + response.get("message").toString()
                                m_crear_coverCircleImageView.loadImage(user.avatar)
                                dialog.dismiss()
                            }
                            override fun onError(error: ANError) {
                                dialog.dismiss()
                                toast("Error al subir imagen de camara")
                            }
                        })
                }
            FINAL_CHOOSE_PHOTO ->
                if (resultCode == Activity.RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        handleImageOnKitkat(data!!.data)
                    }
                    else{
                        handleImageBeforeKitkat(data!!.data)
                    }

                    val builder = android.support.v7.app.AlertDialog.Builder(context!!)
                    val dialogView = layoutInflater.inflate(R.layout.progress_dialog,null)
                    val message = dialogView.findViewById<TextView>(R.id.message)
                    message.text = "Subiendo Imagen..."
                    builder.setView(dialogView)
                    builder.setCancelable(false)
                    val dialog = builder.create()
                    dialog.show()

                    val urlA = MainActivity().projectURL + "/upload"

                    AndroidNetworking.upload(urlA)
                        .addHeaders("Content-Type", "multipart/form-data")
                        .addHeaders("Cookie", SharedApp.prefs.cookie)
                        .addMultipartFile("file", File(imagePath))
                        .build().getAsJSONObject(object : JSONObjectRequestListener {
                            override fun onResponse(response: JSONObject) {
                                user.avatar = MainActivity().projectURL + response.get("message").toString()
                                m_crear_coverCircleImageView.loadImage(user.avatar)
                                dialog.dismiss()
                            }

                            override fun onError(error: ANError) {
                                dialog.dismiss()
                                toast("Error al subir imagen de galería")
                            }
                        })

                }
        }
    }

    @TargetApi(19)
    private fun handleImageOnKitkat(newUri: Uri) {
        var path: String? = null
        val uri = newUri//data!!.data
        if (DocumentsContract.isDocumentUri(context, uri)){
            val docId = DocumentsContract.getDocumentId(uri)
            if ("com.android.providers.media.documents" == uri.authority){
                val id = docId.split(":")[1]
                val selsetion = MediaStore.Images.Media._ID + "=" + id
                path = imagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selsetion)
            }
            else if ("com.android.providers.downloads.documents" == uri.authority){
                val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(docId))
                path = imagePath(contentUri, null)
            }
        }
        else if ("content".equals(uri.scheme, ignoreCase = true)){
            path = imagePath(uri, null)
        }
        else if ("file".equals(uri.scheme, ignoreCase = true)){
            path = uri.path
        }
        imagePath = path
    }

    private fun handleImageBeforeKitkat(newUri: Uri) {}

    private fun imagePath(uri: Uri?, selection: String?): String {
        var path: String? = null
        val cursor = this.requireActivity().contentResolver.query(uri, null, selection, null, null )
        if (cursor != null){
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            }
            cursor.close()
        }
        return path!!
    }

}
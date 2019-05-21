package com.example.kalepa

import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.annotation.TargetApi
import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager

import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.view.MenuItem
import android.view.View
import android.widget.EditText

import android.widget.PopupMenu
import android.widget.TextView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener

import com.example.charactermanager.MainListAdapter
import com.example.kalepa.Adapters.UploadImageAdapter
import com.example.kalepa.Preferences.SharedApp

import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import kotlinx.android.synthetic.main.activity_upload.*

import org.jetbrains.anko.toast
import org.json.JSONObject
import java.io.File

import java.util.*
import kotlin.collections.ArrayList

class UploadActivity : AppCompatActivity() {

    private var imageUrls = ArrayList<String>()
    private var categories = ArrayList<String>()
    private var serverCategories = ArrayList<String>()
    private var selectedAction = 0

    private val FINAL_TAKE_PHOTO = 1
    private val FINAL_CHOOSE_PHOTO = 2
    private var imageUri: Uri? = null
    private var imagePath: String? = null
    private var cameraImages = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        val url = MainActivity().projectURL + "/categories"

        val req = url.httpGet().header(Pair("Cookie", SharedApp.prefs.cookie))
        req.responseJson { request, response, result ->
            when (result) {
                is Result.Failure -> {
                    toast("Error cargando categorías")
                    MainActivity.start(this)
                }
                is Result.Success -> {
                    val jsonObject = result.value
                    val aux = jsonObject.get("list").toString()
                    val separate = aux.split("""(\",\")|(\[\")|(\"\])""".toRegex())
                    serverCategories = ArrayList(separate.slice(IntRange(1,separate.size-2)))
                }
            }
        }


        n_Upload_category_button.setOnClickListener{
            showCategoryDialog()
        }

        m_image_subir.setOnClickListener{
            showImageDialog()
        }

        m_button_upload.setOnClickListener {
            uploadProduct()
        }
    }

    private fun showImages(items: ArrayList<String>) {
        m_images_container.layoutManager = GridLayoutManager(this, items.size)
        val imageItemAdapters = items.map(this::createCategoryItemAdapter)
        m_images_container.adapter = MainListAdapter(imageItemAdapters)
    }

    private fun createCategoryItemAdapter(image: String)
            = UploadImageAdapter(image,
        //BitmapFactory.decodeStream(getContentResolver().openInputStream(image)),
        { showImageMenu(image) })

    private fun showImageMenu(image: String) :Boolean{
        var popup = PopupMenu(this, m_Upload_menu_reference)
        popup.inflate(R.menu.delete_menu)
        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->

            when (item!!.itemId) {
                R.id.delete_staff_menu -> {
                    if (imageUrls.size > 1) {
                        imageUrls.remove(image)
                        showImages(imageUrls)
                        toast("Imagen Eliminada")
                    } else {
                        toast("No se puede dejar menos de una imagen")
                    }
                }
            }
            true
        })
        popup.show()

        return true
    }

    private fun uploadProduct () {
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.progress_dialog,null)
        val message = dialogView.findViewById<TextView>(R.id.message)
        message.text = "Comprobando datos..."
        builder.setView(dialogView)
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()

        if (check_fields()) {
            val now = Calendar.getInstance()
            val bidDate = now.get(Calendar.YEAR).toString() + "-" +
                    String.format("%02d",now.get(Calendar.MONTH)) + "-" +
                    String.format("%02d",now.get(Calendar.DAY_OF_MONTH)) + " " +
                    String.format("%02d",now.get(Calendar.HOUR)) + ":" +
                    String.format("%02d",now.get(Calendar.MINUTE)) + ":" +
                    String.format("%02d",now.get(Calendar.SECOND))

            val jsonObject = JSONObject()
            jsonObject.accumulate("title", m_Upload_titulo.text.toString())
            jsonObject.accumulate("descript", m_Upload_descripcion.text.toString())
            jsonObject.accumulate("price", m_Upload_precio.text.toString())
            jsonObject.accumulate("bid_date", bidDate)
            jsonObject.accumulate("place", SharedApp.prefs.userPlace)
            jsonObject.accumulate("main_img", imageUrls[0])

            for (i in 0 until categories.size) {
                jsonObject.accumulate("categories", categories[i])
            }

            for (i in 0 until imageUrls.size) {
                jsonObject.accumulate("photo_urls", imageUrls[i])
            }

            val url = MainActivity().projectURL + "/product"

            val req = url.httpPost().body(jsonObject.toString()).header(Pair("Cookie", SharedApp.prefs.cookie))
            req.httpHeaders["Content-Type"] = "application/json"

            req.response { request, response, result ->
                when (result) {
                    is Result.Failure -> {
                        dialog.dismiss()
                        toast("No se ha podido subir el artículo, por favor, intentelo más tarde")
                    }
                    is Result.Success -> {
                        dialog.dismiss()
                        MainActivity.start(this)
                        toast("Producto subido")
                    }
                }
            }
        } else {
            dialog.dismiss()
        }
    }

    private fun check_fields () : Boolean {

        var right = true

        if (m_Upload_titulo.text.toString().isEmpty()) {
            m_Upload_titulo.error = "El campo no puede ser vacio"
            right = false
        }

        if (m_Upload_descripcion.text.toString().isEmpty()) {
            m_Upload_descripcion.error = "El campo no puede ser vacio"
            right = false
        }

        if (categories.size <= 1) {
            n_Upload_category_text.error = "Minimo dos categoría"
            right = false
        }

        if (imageUrls.size <= 1) {
            toast("Introduzca dos imagenes como mínimo")
            right = false
        }

        val regex = """[0-9]+(.[0-9][0-9])?""".toRegex()
        if (!regex.matches(m_Upload_precio.text.toString())) {
            m_Upload_precio.error = "No es una cantidad válida"
            right = false
        }

        return right
    }

    private fun showCategoryDialog(){
        lateinit var dialog:AlertDialog

        val arrayCategories = arrayOfNulls<String>(serverCategories.size)
        val arrayChecked = BooleanArray(serverCategories.size)
        for (i in 0 until serverCategories.size) {
            arrayCategories.set(i,serverCategories[i])
            val checked = categories.indexOf(serverCategories[i]) != -1
            if (checked) {
                arrayChecked.set(i,true)
            } else {
                arrayChecked.set(i,false)
            }
        }
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Elija categorías")

        builder.setMultiChoiceItems(arrayCategories, arrayChecked, {dialog,which,isChecked->
            arrayChecked[which] = isChecked
            val Category = arrayCategories[which]
        })


        // Set the positive/yes button click listener
        builder.setPositiveButton("OK") { _, _ ->
            categories.clear()
            var list = ""
            for (i in 0 until arrayCategories.size) {
                val checked = arrayChecked[i]
                if (checked) {
                    categories.add(arrayCategories[i]!!)
                    list = list + " #" + arrayCategories[i]
                }
            }

            n_Upload_category_text.setText(list)
        }

        dialog = builder.create()
        dialog.show()
    }

    private fun showImageDialog(){
        lateinit var dialog:AlertDialog

        val items = arrayOf("Galería","Camara")
        val builder = AlertDialog.Builder(this)
        var selected = "Galería"
        builder.setTitle("¿De donde desea conseguir la imagen?")

        builder.setItems(items) { dialog, which ->
            selected = items[which]
            if (which == 0){    //GALERÍA
                selectedAction = 0
                val checkSelfPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                if (checkSelfPermission != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                }
                else{
                    openAlbum()
                }
            } else {            //CAMARA
                selectedAction = 1
                val checkSelfPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                val checkReadPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                if (checkSelfPermission != PackageManager.PERMISSION_GRANTED && checkReadPermission != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this, arrayOf(READ_EXTERNAL_STORAGE, CAMERA), 1)
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
        val outputImage = File(externalCacheDir, "output_image" + cameraImages.toString() + ".jpg")
        if(outputImage.exists()) {
            outputImage.delete()
        }
        outputImage.createNewFile()
        imageUri = if(Build.VERSION.SDK_INT >= 24){
            FileProvider.getUriForFile(this, "com.example.kalepa.fileprovider", outputImage)
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

                    val builder = android.support.v7.app.AlertDialog.Builder(this)
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
                        .addMultipartFile("file", File(externalCacheDir, "output_image" + cameraImages.toString() + ".jpg"))
                        .build().getAsJSONObject(object : JSONObjectRequestListener {
                            override fun onResponse(response: JSONObject) {
                                imageUrls.add(MainActivity().projectURL + response.get("message").toString())
                                anyadir(false)
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

                    val builder = AlertDialog.Builder(this)
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
                                imageUrls.add(MainActivity().projectURL + response.get("message").toString())
                                anyadir(false)
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

    private fun anyadir (camera: Boolean) {
        if (camera) cameraImages++
        showImages(imageUrls)
    }

    @TargetApi(19)
    private fun handleImageOnKitkat(newUri: Uri) {
        var path: String? = null
        val uri = newUri//data!!.data
        if (DocumentsContract.isDocumentUri(this, uri)){
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
        val cursor = contentResolver.query(uri, null, selection, null, null )
        if (cursor != null){
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            }
            cursor.close()
        }
        return path!!
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, UploadActivity::class.java)
            context.startActivity(intent)
        }
    }


}

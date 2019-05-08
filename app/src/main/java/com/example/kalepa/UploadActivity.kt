package com.example.kalepa

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.view.MenuItem
import android.widget.PopupMenu
import com.example.charactermanager.MainListAdapter
import com.example.kalepa.Adapters.UploadImageAdapter
import com.example.kalepa.Preferences.SharedApp
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import kotlinx.android.synthetic.main.activity_upload.*
import org.jetbrains.anko.toast
import org.json.JSONObject
import java.time.Year
import java.util.*
import kotlin.collections.ArrayList

class UploadActivity : AppCompatActivity() {

    private var images = ArrayList<String>()
    private var categories = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        images = arrayListOf<String>("https://www.kalamazoo.es/content/images/product/28023-1_1_xnl.jpg",
            "https://www.motociclismo.es/media/cache/big/upload/images/article/24729/article-por-que-no-arranca-moto-frio-577662e2620df.jpg",
            "https://i.imgur.com/m4i2rWD.jpg",
            "https://images-na.ssl-images-amazon.com/images/I/81U6Q%2BTrCoL._SX355_.jpg",
            "http://pilarsanzcervera.com/wp-content/uploads/2018/03/libro.png",
            "https://www.ikea.com/PIAimages/0534657_PE649204_S5.JPG?f=s",
            "https://www.enriquedans.com/wp-content/uploads/2017/03/Samsung-Dex.jpg",
            "https://fotos00.autofacil.es/2015/01/21/646x260/mini-5p2.jpg")

        showImages(images)

        n_Upload_category_button.setOnClickListener{
            showDialog()
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
        { showImageMenu(image) })

    private fun showImageMenu(image: String) :Boolean{
        var popup = PopupMenu(this, m_Upload_menu_reference)
        popup.inflate(R.menu.delete_menu)
        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->

            when (item!!.itemId) {
                R.id.delete_staff_menu -> {
                    images.remove(image)
                    showImages(images)
                    toast("Imagen Eliminada")
                }
            }
            true
        })
        popup.show()

        return true
    }

    private fun uploadProduct () {

        if (check_fields()) {
            val now = Calendar.getInstance()
            val bidDate = now.get(Calendar.YEAR).toString() + "-" +
                    String.format("%02d",now.get(Calendar.MONTH)) + "-" +
                    String.format("%02d",now.get(Calendar.DAY_OF_MONTH)) + " " +
                    String.format("%02d",now.get(Calendar.HOUR)) + ":" +
                    String.format("%02d",now.get(Calendar.MINUTE)) + ":" +
                    String.format("%02d",now.get(Calendar.SECOND))

            /*var categoryList = "[ "
            for (i in 0 until categories.size - 1) {
                categoryList = categoryList + "\"" + categories[i] + "\", "
            }
            categoryList = categoryList + "\"" + categories[categories.size - 1] + "\" ]"

            var imageList = "[ "
            for (i in 0 until images.size - 1) {
                imageList = imageList + "\"" + images[i] + "\", "
            }
            imageList = imageList + "\"" + images[images.size - 1] + "\" ]"*/



            val jsonObject = JSONObject()
            jsonObject.accumulate("title", m_Upload_titulo.text.toString())
            jsonObject.accumulate("descript", m_Upload_descripcion.text.toString())
            jsonObject.accumulate("price", m_Upload_precio.text.toString())
            jsonObject.accumulate("bid_date", bidDate)
            jsonObject.accumulate("place", SharedApp.prefs.userPlace)
            jsonObject.accumulate("main_img", images[0])

            for (i in 0 until categories.size - 1) {
                jsonObject.accumulate("categories", categories[i])
            }

            for (i in 0 until images.size - 1) {
                jsonObject.accumulate("photo_urls", images[i])
            }

            val url = MainActivity().projectURL + "/product"

            val req = url.httpPost().body(jsonObject.toString()).header(Pair("Cookie", SharedApp.prefs.cookie))
            req.httpHeaders["Content-Type"] = "application/json"

            req.response { request, response, result ->
                when (result) {
                    is Result.Failure -> {
                        toast("No se ha podido subir el artículo, por favor, intentelo más tarde")
                    }
                    is Result.Success -> {
                        MainActivity.start(this)
                        toast("Producto subido")
                    }
                }
            }
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

        if (categories.size <= 0) {
            n_Upload_category_text.error = "Minimo una categoría"
            right = false
        }

        if (images.size <= 0) {
            toast("Introduzca una imagen como mínimo")
            right = false
        }

        val regex = """[0-9]+(.[0-9][0-9])?""".toRegex()
        if (!regex.matches(m_Upload_precio.text.toString())) {
            m_Upload_precio.error = "No es una cantidad válida"
            right = false
        }

        return right
    }

    private fun showDialog(){
        lateinit var dialog:AlertDialog







        //LLAMAR AQUI A LA API PARA SACAR CATEGORIAS
        val arrayCategories = arrayOf("Moda","Deporte","Casa","Cocina","Juguetes","Jardín")
        val arrayChecked = booleanArrayOf(false,false,false,false,false,false)
        for (i in 0 until arrayCategories.size) {
            val checked = categories.indexOf(arrayCategories[i]) != -1
            if (checked) {
                arrayChecked[i] = true
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
                    categories.add(arrayCategories[i])
                    list = list + " #" + arrayCategories[i]
                }
            }

            n_Upload_category_text.setText(list)
        }

        dialog = builder.create()
        dialog.show()
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, UploadActivity::class.java)
            context.startActivity(intent)
        }
    }


}

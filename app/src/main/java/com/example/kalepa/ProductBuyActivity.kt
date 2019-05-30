package com.example.kalepa

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.kalepa.common.extra
import com.example.kalepa.common.getIntent
import com.example.kalepa.common.loadImage
import com.example.kalepa.models.Product
import kotlinx.android.synthetic.main.activity_product_buy.*

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.widget.GridLayoutManager
import android.view.*
import android.widget.*
import com.example.charactermanager.MainListAdapter
import com.example.kalepa.Adapters.CategoryAdapter
import com.example.kalepa.Preferences.SharedApp
import com.example.kalepa.models.User
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.httpPut
import com.github.kittinunf.result.Result
import kotlinx.android.synthetic.main.fragment_product_image.view.*
import org.jetbrains.anko.toast
import org.json.JSONObject

class ProductBuyActivity : AppCompatActivity() {

    var product: Product = Product()
    var user: User = User()
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    private var num_images = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val product_id: String? = intent.extras.getString(PRODUCT_ARG)
        setContentView(R.layout.activity_product_buy)

        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.progress_dialog,null)
        val message = dialogView.findViewById<TextView>(R.id.message)
        message.text = "Cargando producto..."
        builder.setView(dialogView)
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()

        val url = MainActivity().projectURL + "/product/" + product_id

        val req = url.httpGet().header(Pair("Cookie", SharedApp.prefs.cookie))
        req.responseJson { request, response, result ->
            when (result) {
                is Result.Failure -> {
                    MainActivity.start(this)
                    toast("Error cargando el producto")
                }
                is Result.Success -> {
                    Initialize1(result.value)
                    dialog.dismiss()
                }
            }
        }

    }

    private fun Initialize1(jsonObject: JSONObject) {
        product.fromJSON(jsonObject)

        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.progress_dialog,null)
        val message = dialogView.findViewById<TextView>(R.id.message)
        message.text = "Cargando usuario..."
        builder.setView(dialogView)
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()

        val url = MainActivity().projectURL + "/user/" + product.user_id

        val req = url.httpGet().header(Pair("Cookie", SharedApp.prefs.cookie))
        req.responseJson { request, response, result ->
            when (result) {
                is Result.Failure -> {
                    MainActivity.start(this)
                    toast("Error cargando el producto")
                }
                is Result.Success -> {
                    Initialize2(result.value)
                    dialog.dismiss()
                }
            }
        }

    }

    private fun Initialize2(jsonObject: JSONObject) {

        user.fromJSON(jsonObject)

        b_product_name.setText(product.title)
        b_product_price.setText(product.price.toString())
        b_product_description.setText(product.descript)
        b_product_date.setText(product.publish_date)
        b_product_place.setText(product.place)
        b_product_seller.setText(user.nick)
        b_product_rating.rating = user.points.toFloat()

        b_product_seller.setOnClickListener {
            ProfileActivity.start(this, product.user_id.toString())
        }

        b_product_trade.setOnClickListener {
            TradeActivity.start(this, product.id.toString(), user.id.toString())
        }

        m_product_share_facebook.setOnClickListener {
            val url = MainActivity().webURL + "/anuncio.html?idAnuncio=" + product.id.toString()
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type="text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Mira que producto mas chulo de KALEPA, mi pagina de compra y venta favorita:")
            shareIntent.putExtra(Intent.EXTRA_TEXT, url)
            startActivity(Intent.createChooser(shareIntent, "Compartir con:"))
        }


        num_images = product.photo_urls.size

        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        b_product_images_container.adapter = mSectionsPagerAdapter

        showImages(product.categories)
    }

    private fun showImages(items: ArrayList<String>) {
        b_categories_container_buy.layoutManager = GridLayoutManager(this, items.size)
        val imageItemAdapters = items.map(this::createCategoryItemAdapter)
        b_categories_container_buy.adapter = MainListAdapter(imageItemAdapters)
    }

    private fun createCategoryItemAdapter(category: String)
            = CategoryAdapter(category,
        { rtrue(category) })

    private fun rtrue (category: String): Boolean {
        return true
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position , product.photo_urls)
        }

        override fun getCount(): Int {
            // Show 5 total pages.(we will use 5 pages so change it to 5)
            return num_images
        }
    }

    class PlaceholderFragment : Fragment() {

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val rootView = inflater.inflate(R.layout.fragment_product_image, container, false)

            val images = arguments!!.getStringArrayList(ARG_IMAGE_LIST)
            rootView.image_iv.loadImage(images[arguments!!.getInt(ARG_SECTION_NUMBER)])

            return rootView
        }

        companion object {
            /**
             * The fragment argument representing the section number for this
             * fragment.
             */
            private val ARG_SECTION_NUMBER = "section_number"
            private val ARG_IMAGE_LIST = "image_list"

            /**
             * Returns a new instance of this fragment for the given section
             * number.
             */
            fun newInstance(sectionNumber: Int, images: ArrayList<String?>): PlaceholderFragment {
                val fragment = PlaceholderFragment()
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                args.putStringArrayList(ARG_IMAGE_LIST, images)
                fragment.arguments = args
                return fragment
            }
        }
    }

    companion object {

        private const val bullet = '\u2022'
        private  const val PRODUCT_ARG = "com.example.kalepa.BuyProductActivity.ProductArgKey"

        fun getIntent(context: Context, product_id: String) = context
            .getIntent<ProductBuyActivity>()
            .apply { putExtra(PRODUCT_ARG, product_id) }

        fun start(context: Context, product_id: String) {
            val intent = getIntent(context, product_id)
            context.startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        if (SharedApp.prefs.isMod) {
            inflater.inflate(R.menu.mod_product_menu, menu)
        } else {
            inflater.inflate(R.menu.user_product_menu, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.n_upm_follow -> {
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.progress_dialog,null)
            val message = dialogView.findViewById<TextView>(R.id.message)
            message.text = "Siguiendo producto..."
            builder.setView(dialogView)
            builder.setCancelable(false)
            val dialog = builder.create()
            dialog.show()

            val url = MainActivity().projectURL + "/product/" + product.id.toString() + "/follow"

            val req = url.httpPost().header(Pair("Cookie", SharedApp.prefs.cookie))
            req.response { request, response, result ->
                when (result) {
                    is Result.Failure -> {
                        dialog.dismiss()
                        toast("Ya sigues este producto")
                    }
                    is Result.Success -> {
                        dialog.dismiss()
                        toast("Añadido a tu lista de deseos")
                    }
                }
            }
            true
        }
        R.id.n_upm_unfollow -> {
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.progress_dialog,null)
            val message = dialogView.findViewById<TextView>(R.id.message)
            message.text = "Siguiendo producto..."
            builder.setView(dialogView)
            builder.setCancelable(false)
            val dialog = builder.create()
            dialog.show()

            val url = MainActivity().projectURL + "/product/" + product.id.toString() + "/unfollow"

            val req = url.httpPost().header(Pair("Cookie", SharedApp.prefs.cookie))
            req.response { request, response, result ->
                when (result) {
                    is Result.Failure -> {
                        dialog.dismiss()
                        toast("No sigues este producto")
                    }
                    is Result.Success -> {
                        dialog.dismiss()
                        toast("Eliminado de tu lista de deseos")
                    }
                }
            }
            true
        }
        R.id.n_upm_report -> {
            reportProduct()
            true
        }
        R.id.n_mpm_ban -> {
            banProduct()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun reportProduct() {
        val view = layoutInflater.inflate(R.layout.dialog_report_product, null)

        val window = PopupWindow(
            view, // Custom view to show in popup window
            LinearLayout.LayoutParams.WRAP_CONTENT, // Width of popup window
            LinearLayout.LayoutParams.WRAP_CONTENT // Window height
        )
        window.isFocusable = true

        //Blur the background
        val fcolorNone = ColorDrawable(resources.getColor(R.color.transparent))
        val fcolorBlur = ColorDrawable(resources.getColor(R.color.transparentDark))
        n_productBuy_container.foreground = fcolorBlur

        window.showAtLocation(
            n_productBuy_header, // Location to display popup window
            Gravity.CENTER, // Exact position of layout to display popup
            0, // X offset
            0 // Y offset
        )

        val cancel = view.findViewById<Button>(R.id.n_drp_cancelar)
        val report = view.findViewById<Button>(R.id.n_drp_reportar)
        val reason = view.findViewById<EditText>(R.id.n_drp_reason)

        report.setOnClickListener {

            if (!reason.text.toString().equals("")) {

                val builder = AlertDialog.Builder(this)
                val dialogView = layoutInflater.inflate(R.layout.progress_dialog,null)
                val message = dialogView.findViewById<TextView>(R.id.message)
                message.text = "Enviando report..."
                builder.setView(dialogView)
                builder.setCancelable(false)
                val dialog = builder.create()
                dialog.show()

                val jsonObject = JSONObject()
                jsonObject.accumulate("user_id", user.id)
                jsonObject.accumulate("product_id", product.id)
                jsonObject.accumulate("reason", reason.text.toString())

                val url = MainActivity().projectURL + "/report"

                val req = url.httpPost().body(jsonObject.toString()).header(Pair("Cookie", SharedApp.prefs.cookie))
                req.httpHeaders["Content-Type"] = "application/json"

                req.response { request, response, result ->
                    when (result) {
                        is Result.Failure -> {
                            dialog.dismiss()
                            toast("Error enviando report")
                        }
                        is Result.Success -> {
                            dialog.dismiss()
                            toast("Report enviado")
                        }
                    }
                }
                window.dismiss()
            } else{
                toast("Se debe introducir un motivo")
            }
        }

        cancel.setOnClickListener {
            window.dismiss()
        }

        window.setOnDismissListener {
            n_productBuy_container.foreground = fcolorNone
        }

        true
    }

    private fun banProduct() {
        val view = layoutInflater.inflate(R.layout.dialog_ban_product, null)

        val window = PopupWindow(
            view, // Custom view to show in popup window
            LinearLayout.LayoutParams.WRAP_CONTENT, // Width of popup window
            LinearLayout.LayoutParams.WRAP_CONTENT // Window height
        )
        window.isFocusable = true

        //Blur the background
        val fcolorNone = ColorDrawable(resources.getColor(R.color.transparent))
        val fcolorBlur = ColorDrawable(resources.getColor(R.color.transparentDark))
        n_productBuy_container.foreground = fcolorBlur

        window.showAtLocation(
            n_productBuy_header, // Location to display popup window
            Gravity.CENTER, // Exact position of layout to display popup
            0, // X offset
            0 // Y offset
        )

        val cancel = view.findViewById<Button>(R.id.n_dbp_cancelar)
        val ban = view.findViewById<Button>(R.id.n_dbp_ban)
        val reason = view.findViewById<EditText>(R.id.n_dbp_reason)

        ban.setOnClickListener {

            if (!reason.text.toString().equals("")) {

                val builder = AlertDialog.Builder(this)
                val dialogView = layoutInflater.inflate(R.layout.progress_dialog,null)
                val message = dialogView.findViewById<TextView>(R.id.message)
                message.text = "Baneando producto..."
                builder.setView(dialogView)
                builder.setCancelable(false)
                val dialog = builder.create()
                dialog.show()

                val jsonObject = JSONObject()
                jsonObject.accumulate("ban_reason", reason.text.toString())

                val url = MainActivity().projectURL + "/product/" + product.id.toString() + "/ban"

                val req = url.httpPut().body(jsonObject.toString()).header(Pair("Cookie", SharedApp.prefs.cookie))
                req.httpHeaders["Content-Type"] = "application/json"

                req.response { request, response, result ->
                    when (result) {
                        is Result.Failure -> {
                            dialog.dismiss()
                            toast("Error baneando producto")
                        }
                        is Result.Success -> {
                            dialog.dismiss()
                            MainActivity.start(this)
                            toast("Producto baneado")
                        }
                    }
                }
                window.dismiss()
            } else{
                toast("Se debe introducir un motivo")
            }
        }

        cancel.setOnClickListener {
            window.dismiss()
        }

        window.setOnDismissListener {
            n_productBuy_container.foreground = fcolorNone
        }

        true
    }
}

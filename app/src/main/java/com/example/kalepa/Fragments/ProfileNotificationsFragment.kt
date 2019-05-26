package com.example.kalepa.Fragments

import android.app.AlertDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.charactermanager.MainListAdapter
import com.example.kalepa.Adapters.NotificationAdapter
import com.example.kalepa.MainActivity
import com.example.kalepa.Preferences.SharedApp
import com.example.kalepa.ProductBidActivity
import com.example.kalepa.ProductBuyActivity
import com.example.kalepa.R
import com.example.kalepa.models.Notification
import com.example.kalepa.models.RawProduct
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.httpDelete
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import kotlinx.android.synthetic.main.fragment_profile_notifications.*
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.toast
import org.json.JSONArray
import org.json.JSONObject

class ProfileNotificationsFragment: Fragment() {

    private var notifications = ArrayList<Notification>()

    companion object {
        fun newInstance(): ProfileNotificationsFragment {
            return ProfileNotificationsFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_profile_notifications, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        n_recyclerView_notifications.layoutManager = GridLayoutManager(context!!, 1)

        n_swipeRefreshView_notifications.setOnRefreshListener {
            notifications.clear()
            loadNotifications()
            n_swipeRefreshView_notifications.isRefreshing = false
        }

        loadNotifications()
    }

    private fun loadNotifications() {
        val builder = AlertDialog.Builder(context)
        val dialogView = layoutInflater.inflate(R.layout.progress_dialog,null)
        val message = dialogView.findViewById<TextView>(R.id.message)
        message.text = "Cargando notificaciones..."
        builder.setView(dialogView)
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()

        val url = MainActivity().projectURL + "/notifications"

        val req = url.httpGet().header(Pair("Cookie", SharedApp.prefs.cookie))
        req.responseJson { request, response, result ->
            when (result) {
                is Result.Failure -> {
                    dialog.dismiss()
                    toast("Error cargando notificaciones, intentelo de nuevo más tarde")
                }
                is Result.Success -> {
                    Initialize(result.value)
                    dialog.dismiss()
                }
            }
        }
    }

    private fun show(items: List<Notification>) {
        val categoryItemAdapters = items.map(this::createCategoryItemAdapter)
        n_recyclerView_notifications.adapter = MainListAdapter(categoryItemAdapters)
    }

    private fun createCategoryItemAdapter(notification: Notification)
            = NotificationAdapter(notification,
        { showProduct(notification) },
        { deleteNotification(notification) })

    private fun Initialize (jsonProducts: JSONObject) {
        val length = jsonProducts.get("length").toString().toInt()
        val list = jsonProducts.get("list")
        if (list is JSONArray){
            for (i in 0 until length) {
                var notification = Notification()
                notification.fromJSON(list.getJSONObject(i))
                notifications.add(notification)
            }
        }
        show(notifications)

        m_notifications_deleteAll.setOnClickListener {
            deleteAllNotifications()
        }
    }

    private fun showProduct(notification: Notification) {

        val builder = AlertDialog.Builder(context)
        val dialogView = layoutInflater.inflate(R.layout.progress_dialog,null)
        val message = dialogView.findViewById<TextView>(R.id.message)
        message.text = "Cargando producto..."
        builder.setView(dialogView)
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()

        val url = MainActivity().projectURL + "/product/" + notification.product_id

        val req = url.httpGet().header(Pair("Cookie", SharedApp.prefs.cookie))
        req.responseJson { request, response, result ->
            when (result) {
                is Result.Failure -> {
                    toast("Error cargando el producto")
                }
                is Result.Success -> {
                    Launch(result.value)
                    dialog.dismiss()
                }
            }
        }
    }

    private fun Launch(jsonObject: JSONObject) {
        val product: RawProduct = RawProduct()
        product.fromJSON(jsonObject)

        if (product.isBid()) {
            ProductBidActivity.start(context!!, product.id.toString())
        } else {
            ProductBuyActivity.start(context!!, product.id.toString())
        }
    }

    private fun deleteNotification(notification: Notification) {

        val builder = AlertDialog.Builder(context)
        val dialogView = layoutInflater.inflate(R.layout.progress_dialog,null)
        val message = dialogView.findViewById<TextView>(R.id.message)
        message.text = "Eliminando..."
        builder.setView(dialogView)
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()

        val url = MainActivity().projectURL + "/notification/" + notification.id

        val req = url.httpDelete().header(Pair("Cookie", SharedApp.prefs.cookie))
        req.response { request, response, result ->
            when (result) {
                is Result.Failure -> {
                    toast("Error eliminando notificación")
                    dialog.dismiss()
                }
                is Result.Success -> {
                    notifications.remove(notification)
                    show(notifications)
                    toast("Notificación eliminada")
                    dialog.dismiss()
                }
            }
        }
    }

    private fun deleteAllNotifications() {
        val builder = AlertDialog.Builder(context)
        val dialogView = layoutInflater.inflate(R.layout.progress_dialog,null)
        val message = dialogView.findViewById<TextView>(R.id.message)
        message.text = "Eliminando..."
        builder.setView(dialogView)
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()

        val url = MainActivity().projectURL + "/notifications"

        val req = url.httpDelete().header(Pair("Cookie", SharedApp.prefs.cookie))
        req.response { request, response, result ->
            when (result) {
                is Result.Failure -> {
                    toast("Error eliminando notificaciones")
                    dialog.dismiss()
                }
                is Result.Success -> {
                    notifications.clear()
                    show(notifications)
                    toast("Notificaciones eliminadas")
                    dialog.dismiss()
                }
            }
        }
    }
}
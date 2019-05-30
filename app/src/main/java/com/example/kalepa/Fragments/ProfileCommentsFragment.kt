package com.example.kalepa.Fragments

import android.app.AlertDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import com.example.charactermanager.MainListAdapter
import com.example.kalepa.Adapters.CommentAdapter
import com.example.kalepa.MainActivity
import com.example.kalepa.Preferences.SharedApp
import com.example.kalepa.R
import com.example.kalepa.models.Comment
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.httpDelete
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import kotlinx.android.synthetic.main.fragment_profile_comments.*
import org.jetbrains.anko.support.v4.toast
import org.json.JSONArray
import org.json.JSONObject

class ProfileCommentsFragment: Fragment() {

    private var comments = ArrayList<Comment>()

    companion object {
        fun newInstance(user_id: Int): ProfileCommentsFragment {
            val myFragment = ProfileCommentsFragment()
            val args = Bundle()
            args.putInt("user_id", user_id)
            myFragment.arguments = args
            return myFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_profile_comments, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        n_recyclerView_comments.layoutManager = GridLayoutManager(context!!, 1)

        n_swipeRefreshView_comments.setOnRefreshListener {
            comments.clear()
            loadComments()
            n_swipeRefreshView_comments.isRefreshing = false
        }

        loadComments()
    }

    private fun loadComments() {
        val user_id = arguments!!.getInt("user_id",0)

        val builder = AlertDialog.Builder(context)
        val dialogView = layoutInflater.inflate(R.layout.progress_dialog,null)
        val message = dialogView.findViewById<TextView>(R.id.message)
        message.text = "Cargando comentarios..."
        builder.setView(dialogView)
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()

        val url = MainActivity().projectURL + "/comments/" + user_id.toString()

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

    private fun show(items: List<Comment>) {
        val categoryItemAdapters = items.map(this::createCategoryItemAdapter)
        n_recyclerView_comments.adapter = MainListAdapter(categoryItemAdapters)
    }

    private fun createCategoryItemAdapter(comment: Comment)
            = CommentAdapter(comment,
            {showCommentMenu(comment)})

    private fun showCommentMenu(comment: Comment): Boolean {
        var popup = PopupMenu(context, view!!.findViewById(R.id.n_swipeRefreshView_comments))
        popup.inflate(R.menu.delete_menu)
        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->
            when (item!!.itemId) {
                R.id.delete_staff_menu -> {
                    deleteComment(comment)
                    toast("Comentario Eliminado")
                }
            }
            true
        })
        popup.show()
        return true
    }

    private fun deleteComment(comment: Comment) {
        val builder = AlertDialog.Builder(context)
        val dialogView = layoutInflater.inflate(R.layout.progress_dialog,null)
        val message = dialogView.findViewById<TextView>(R.id.message)
        message.text = "Cargando comentarios..."
        builder.setView(dialogView)
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()

        val url = MainActivity().projectURL + "/comment/" + comment.id + "/del"

        val req = url.httpDelete().header(Pair("Cookie", SharedApp.prefs.cookie))
        req.response { request, response, result ->
            when (result) {
                is Result.Failure -> {
                    dialog.dismiss()
                    toast("Error eliminando comentario")
                }
                is Result.Success -> {
                    dialog.dismiss()
                    comments.remove(comment)
                    show(comments)
                    toast("Comentario eliminado")
                }
            }
        }
    }

    private fun Initialize (jsonProducts: JSONObject) {
        val length = jsonProducts.get("length").toString().toInt()
        val list = jsonProducts.get("list")
        if (list is JSONArray){
            for (i in 0 until length) {
                var comment = Comment()
                comment.fromJSON(list.getJSONObject(i))
                comments.add(comment)
            }
        }
        show(comments)
    }
}
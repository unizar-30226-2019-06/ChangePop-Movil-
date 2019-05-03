package com.example.kalepa

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.kalepa.models.*
import org.jetbrains.anko.db.*

class MySqlHelper(val ctx: Context) : ManagedSQLiteOpenHelper(ctx, "mydb") {

    companion object {
        private var instance: MySqlHelper? = null

        @Synchronized
        fun getInstance(ctx: Context): MySqlHelper {
            if (instance == null) {
                instance = MySqlHelper(ctx.applicationContext)
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {

        db.createTable("Cookies", true,
            "id" to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
            "Value" to TEXT + NOT_NULL)

        /*db.createTable("Users", true,
            "id" to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
            "Nick" to TEXT + NOT_NULL,
            "Password" to TEXT + NOT_NULL,
            "Remember" to INTEGER+ NOT_NULL)*/

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }

    public fun fetchCookies () : ArrayList<Pair<Int,String>> = ctx.database.use {
        val cookies = ArrayList<Pair<Int,String>>()

        select(
            "Cookies", "id", "Value"
        ).parseList(object : MapRowParser<List<Pair<Int, String>>> {
            override fun parseRow(columns: Map<String, Any?>): List<Pair<Int, String>> {
                val id = columns.getValue("id").toString().toInt()
                val cookie = columns.getValue("Value").toString()
                cookies.add(Pair(id,cookie))
                return cookies
            }
        })
        cookies
    }

    public fun addCookie (value : String) = ctx.database.use {
        var rem = 0
        insert(
            "Cookies",
            "Value" to value
        )
    }

    public fun deleteCookie (id: Int) = ctx.database.use{
        delete("Cookies", "id = {cookieId}", "cookieId" to id)
    }


    public fun clearCookies () = ctx.database.use{
        delete("Cookies")
    }

}

// Access property for Context
val Context.database: MySqlHelper
get() = MySqlHelper.getInstance(getApplicationContext())
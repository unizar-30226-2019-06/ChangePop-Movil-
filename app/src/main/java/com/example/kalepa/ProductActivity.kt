package com.example.kalepa

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class ProductActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, ProductActivity::class.java)
            context.startActivity(intent)
        }
    }
}

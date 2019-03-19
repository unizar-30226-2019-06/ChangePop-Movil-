package com.example.kalepa

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class UploadActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, UploadActivity::class.java)
            context.startActivity(intent)
        }
    }
}

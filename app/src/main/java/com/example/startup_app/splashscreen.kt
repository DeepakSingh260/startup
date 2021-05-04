package com.example.startup_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper

class splashscreen : AppCompatActivity() {

    private val SPLASH_TIME_OUT = 3000L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)
//        setContentView(R.layout.activity_main)
        Handler(Looper.getMainLooper()).postDelayed(
                {
                    val i = Intent(this, MainActivity::class.java)
                    startActivity(i)
                    finish()
                }, SPLASH_TIME_OUT)
    }
}
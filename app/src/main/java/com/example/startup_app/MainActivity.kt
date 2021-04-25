package com.example.startup_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.annotation.NonNull
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlin.math.sign

class MainActivity : AppCompatActivity() {

    private lateinit var bottom_nav_bar : BottomNavigationView

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth

        bottom_nav_bar = findViewById(R.id.bottom_nav_bar)
        bottom_nav_bar.setOnNavigationItemSelectedListener {
                when(it.itemId){
                    R.id.profile -> supportFragmentManager.beginTransaction().apply { replace(R.id.frame , profile()).commit() }

                    R.id.camera ->supportFragmentManager.beginTransaction().apply { replace(R.id.frame ,camera()).commit() }
                    R.id.home ->supportFragmentManager.beginTransaction().apply { replace(R.id.frame ,home()).commit() }

                }

                 true
            }


    }


    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser == null){
           startActivity(Intent(this , logIn::class.java))
        }
    }


    companion object{
        private const val TAG = "EmailPassword"

    }
}
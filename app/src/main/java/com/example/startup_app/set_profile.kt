package com.example.startup_app

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

class set_profile : AppCompatActivity() {

    private lateinit var profile_img : ImageView
    private lateinit var change_img : Button
    private var PICK_IMAGE:Int = 100
    private lateinit var imageUri:Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_profile)
        profile_img = findViewById(R.id.select_img)
        change_img = findViewById(R.id.select_photo)

        change_img.setOnClickListener {
            (object : View.OnClickListener {

              override fun onClick(v:View){

                 openGallery()
            }
        } )}


    }

    private fun openGallery(){
        startActivityForResult(Intent(Intent.ACTION_PICK , MediaStore.Images.Media.INTERNAL_CONTENT_URI),PICK_IMAGE)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            if (data != null) {
                imageUri = data.data!!
            }
            profile_img.setImageURI(imageUri)
        }
    }

}

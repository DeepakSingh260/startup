 package com.example.startup_app

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentResolverCompat.query

 data class Image(val uri: Uri ,
    val  name:String ,
    val size:Int
)

 private val imageList = mutableListOf<Image>()


 class set_profile : AppCompatActivity() {

    private lateinit var profile_img : ImageView
    private lateinit var change_img : Button
    private var PICK_IMAGE:Int = 100
    private lateinit var imageUri:Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_profile)


        val projection = arrayOf(MediaStore.Images.Media.ORIGINAL_DOCUMENT_ID,MediaStore.Images.Media.DISPLAY_NAME , MediaStore.Images.Media.SIZE,)
        val selection = "${MediaStore.Images.Media.SIZE}<=?"
        val selectionArgs = arrayOf("2")
        val sortOrder = "${MediaStore.Images.Media.DISPLAY_NAME} ASC"

        applicationContext.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                , projection
                , selection
                ,selectionArgs
                , sortOrder)?.use { cursor->

                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.ORIGINAL_DOCUMENT_ID)
                    val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                    val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)

                    while (cursor.moveToNext()){

                        val id = cursor.getLong(idColumn)
                        val name = cursor.getString(nameColumn)
                        val size  = cursor.getInt(sizeColumn)

                        val contentUri:Uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,id)
                        imageList += Image(contentUri , name , size)



                    }

        }



        profile_img = findViewById(R.id.select_img)
        change_img = findViewById(R.id.select_photo)

        change_img.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                Toast.makeText(this@set_profile , "open gallery" , Toast.LENGTH_SHORT).show()
            }
        })
    }



    companion object{
        private const val TAG = "set profile"
    }
}

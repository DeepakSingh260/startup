package com.example.startup_app

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.net.toUri
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class uploadGallery : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var upload:Button
    private lateinit var downloadUrl:Uri
    private lateinit var listID:MutableList<String>
    private val user = Firebase.auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_gallery)
       imageView = findViewById(R.id.galleryPic)
        upload = findViewById(R.id.uploadButton)
        listID = ArrayList()
       val  photoUri = intent.extras?.getString("imageUri")
       imageView.setImageURI(photoUri!!.toUri())

        FirebaseDatabase.getInstance().getReference("followers/").child(Firebase.auth.currentUser.uid+"/").addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    listID.clear()
                    for(postSnapshot in snapshot.children){
                        Log.d(camera.TAG,"post snap shots"+ snapshot.children.toString())
                        val id = postSnapshot.child("id/").value
                        Log.d(camera.TAG, "ID : " +id)
                        listID.add(id.toString())
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })


        upload.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                val ref =FirebaseStorage.getInstance().reference.child("storage/").child(Firebase.auth.currentUser.uid+"/")
                val uploadTask = ref.putFile(photoUri!!.toUri())
                val urlTask = uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot , Task<Uri>>{
                    if (!it.isSuccessful){
                        it.exception?.let {
                            throw it
                        }
                }
                    return@Continuation ref.downloadUrl
                })?.addOnCompleteListener() {
                    if (it.isSuccessful){
                        downloadUrl = it.result!!
                        for (i in listID){
                            val push = FirebaseDatabase.getInstance().getReference("profiles").child(i+"/").child("posts/").child(user.uid+"/").push()
                            push.child("id").setValue(user.uid).toString()
                            push.child("name").setValue(user.displayName.toString())
                            push.child("profileUrl").setValue(user.photoUrl.toString())
                            push.child("postUrl").setValue(downloadUrl.toString())
                            push.child("TYPE").setValue(1)
                            val timeStamp = SimpleDateFormat("dd:MM:yyyy").format(Date())
                            push.child("timeStamp").setValue(timeStamp)
                        }
                    }
                }


                Toast.makeText(this@uploadGallery , "clicked" , Toast.LENGTH_SHORT).show()
            }
        })

    }
}
package com.example.startup_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class BlogPost : AppCompatActivity() {

    private lateinit var wrtieBlog : EditText
    private lateinit var postBlog:Button
    private lateinit var blogText: String
    private lateinit var listID:MutableList<String>
    private val user = Firebase.auth.currentUser


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blog_post)
        listID = ArrayList()
        wrtieBlog = findViewById(R.id.writeBlog)
        postBlog = findViewById(R.id.postBlog)

        FirebaseDatabase.getInstance().getReference("followers/").child(user.uid+"/").addValueEventListener(
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

        postBlog.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                blogText = wrtieBlog.text.toString()
                val  pushBlog = FirebaseDatabase.getInstance().getReference("posts/").child(Firebase.auth.currentUser.uid+"/").push()
                pushBlog.child("blog").setValue(blogText)
                pushBlog.child("comments").setValue("Comment")
                pushBlog.child("likes").setValue("Like")
                pushBlog.child("TYPE").setValue(2)

                for (i in listID){
                    val push = FirebaseDatabase.getInstance().getReference("profiles").child(i+"/").child("posts/").push()
                    push.child("id").setValue(user.uid).toString()
                    push.child("name").setValue(user.displayName.toString())
                    push.child("profileUrl").setValue(user.photoUrl.toString())
                    push.child("blog").setValue(blogText)
                    push.child("TYPE").setValue(2)
                    push.child("likes").setValue("Like")
                    push.child("comments").setValue("Comment")
                    val timeStamp = SimpleDateFormat("dd:MM:yyyy").format(Date())
                    push.child("timeStamp").setValue(timeStamp)
                }

            }
        })

    }
}
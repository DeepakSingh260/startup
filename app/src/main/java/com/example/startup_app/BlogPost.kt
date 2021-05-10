package com.example.startup_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class BlogPost : AppCompatActivity() {

    private lateinit var wrtieBlog : EditText
    private lateinit var postBlog:Button
    private lateinit var blogText: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blog_post)

        wrtieBlog = findViewById(R.id.writeBlog)
        postBlog = findViewById(R.id.postBlog)

        postBlog.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                blogText = wrtieBlog.text.toString()
                val  pushBlog = FirebaseDatabase.getInstance().getReference("posts/").child(Firebase.auth.currentUser.uid+"/").push()
                pushBlog.child("blog").setValue(blogText)
                pushBlog.child("comments").setValue("Comment")
                pushBlog.child("likes").setValue("Like")
                pushBlog.child("TYPE").setValue(2)


            }
        })

    }
}
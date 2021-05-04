package com.example.startup_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso


class see_profile : AppCompatActivity() {

    private var _db = FirebaseDatabase.getInstance().getReference("profiles")
    private lateinit var profile_name:String
    private lateinit var profile_url:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_see_profile)

        var userId = intent.extras?.getString("ID")
        val profilePic:ImageView = findViewById(R.id.profile_image_other)
        val name:TextView = findViewById(R.id.name)



        Toast.makeText(this , "user ID : "+userId , Toast.LENGTH_SHORT).show()
        _db.child(userId +"/").addValueEventListener(object :  ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(TAG ,snapshot.toString())
                profile_name = snapshot.child("name/").value as String
                profile_url = snapshot.child("profileUrl/").value as String

                Picasso.with(applicationContext).load(profile_url).into(profilePic)
                name.text = profile_name
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })



    }
    companion object{
        const val TAG = "see profile"
    }
}
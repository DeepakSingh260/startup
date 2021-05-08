package com.example.startup_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*


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
        val messageButton: Button = findViewById(R.id.message)
        val connectButton : Button = findViewById(R.id.connect)

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

        messageButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {

                 startActivity(Intent(this@see_profile , message::class.java).apply {
                    putExtra("id" , userId)
                })

            }
        })

        connectButton.setOnClickListener (object : View.OnClickListener {
            override fun onClick(v: View?) {
                val push = FirebaseDatabase.getInstance().getReference("follows").child(Firebase.auth.currentUser.uid+"/").child(userId.toString()+"/")
                push.child("id").setValue(userId.toString())
                push.child("photoUrl").setValue(profile_url.toString())
                push.child("name").setValue(profile_name.toString())
                val timeStamp = SimpleDateFormat("dd:MM:yyyy").format(Date())
                push.child("timeStamp").setValue(timeStamp.toString())
                val newPush = FirebaseDatabase.getInstance().getReference("followers").child(userId.toString()+"/").child(Firebase.auth.currentUser.uid+"/")
                newPush.child("id").setValue(Firebase.auth.currentUser.uid)
                newPush.child("name").setValue(Firebase.auth.currentUser.displayName.toString())
                newPush.child("photoUrl").setValue(Firebase.auth.currentUser.photoUrl.toString())
                newPush.child("timeStamp").setValue(timeStamp)


                Toast.makeText(this@see_profile , "connect"  , Toast.LENGTH_SHORT).show()
            }
        })

    }
    companion object{
        const val TAG = "see profile"
    }
}
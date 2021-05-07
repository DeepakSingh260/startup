package com.example.startup_app

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.startup_app.see_profile.Companion.TAG
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.log

class message : AppCompatActivity() {
    private lateinit var sent_button:Button
    private lateinit var edit_message:EditText
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var imageView: ImageView
    private lateinit var profileName: TextView
    private  var _db = FirebaseDatabase.getInstance().getReference("chats/")
    private val user = Firebase.auth.currentUser.uid
    private  lateinit var adapter:messageAdapter
    private lateinit var chatList:MutableList<chatInfo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        sent_button = findViewById(R.id.sent_button)
        edit_message = findViewById(R.id.type_message)
        chatRecyclerView = findViewById(R.id.recycleChatMessage)
        imageView = findViewById(R.id.image_other)
        profileName= findViewById(R.id.name_display)
        chatList = ArrayList()


        adapter = messageAdapter(applicationContext , chatList as ArrayList<chatInfo>)
        chatRecyclerView.adapter=adapter
        chatRecyclerView.layoutManager = LinearLayoutManager(applicationContext)

        val userId = intent.extras?.getString("id")
        Toast.makeText(this , "user Id : " +userId ,Toast.LENGTH_SHORT).show()
        var i:Int = 0
        _db.child(user+"/").child(userId.toString()).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                i++
                Log.d(TAG , "Data click set change function is called for the  " +i.toString() +"times")
                chatList.clear()
                Log.d(TAG , " before size of chat list  " + chatList.toString())
                for (postSnapshot in snapshot.children.iterator()) {
                    Log.d(TAG, "Snap  :  " + postSnapshot)

                    val id = postSnapshot.child("id").value
                    val timeStamp = postSnapshot.child("timeStamp").value
                    val photoUrl = postSnapshot.child("photoUrl").value
                    val message = postSnapshot.child("message").value
                    val name = postSnapshot.child("name").value
                    chatList.add(
                        chatInfo(
                            id.toString(),
                            timeStamp.toString(),
                            message.toString(),
                            photoUrl.toString(),
                            name.toString()
                        )
                    )
                    Log.d(TAG, "size of chat list  " + chatList.size)

                }
                Log.d(TAG , "Chat List "+chatList.toString())

                adapter.notifyDataSetChanged()
                _db.removeEventListener(this)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        }

        )



        FirebaseDatabase.getInstance().getReference("profiles").child(userId.toString() +"/").addValueEventListener(object :  ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                profileName.text = snapshot.child("name/").value.toString()
                Picasso.with(this@message).load(snapshot.child("profileUrl/").value.toString()).into(imageView)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
        sent_button.setOnClickListener(object :  View.OnClickListener {
            override fun onClick(v: View?) {
                val id = user
                val message = edit_message.text.toString()
                val photoUrl = Firebase.auth.currentUser.photoUrl
                val name = Firebase.auth.currentUser.displayName
                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                val push = _db.child(user+"/").child(userId.toString()+"/").push()
                push.child("id").setValue(id.toString())
                push.child("photoUrl").setValue(photoUrl.toString())
                push.child("name").setValue(name.toString())
                push.child("message").setValue(message.toString())
                push.child("timeStamp").setValue(timeStamp.toString())
//                edit_message.setText("")
                i=0
                val pushTo = _db.child(userId.toString()+"/").child(user+"/").push()
                pushTo.child("id").setValue(id.toString())
                pushTo.child("photoUrl").setValue(photoUrl.toString())
                pushTo.child("name").setValue(name.toString())
                pushTo.child("message").setValue(message.toString())
                pushTo.child("timeStamp").setValue(timeStamp.toString())
                edit_message.text.clear()


            }

        })
    }
}

class messageAdapter(applicationContext: Context, chatList: ArrayList<chatInfo>) : RecyclerView.Adapter<messageAdapter.messageViewHolder>(){
    val ChatList = chatList
    val ctx = applicationContext
    val user = Firebase.auth.currentUser.uid

    class messageViewHolder(view:View) :RecyclerView.ViewHolder(view) {
            val message:TextView
            val name:TextView
            val profilePhoto:ImageView
        init {
            message = view.findViewById(R.id.chat_message)
            name = view.findViewById(R.id.profile_name_)
            profilePhoto = view.findViewById(R.id.profile_photo)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): messageViewHolder {
//        Log.d(TAG , " on crate view")
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_message , parent,false , )
        return messageViewHolder(view)
    }

    override fun onBindViewHolder(holder: messageViewHolder, position: Int) {

//        Log.d(TAG ,"Chat List : "+ChatList.toString())

        if (ChatList.get(position).id==user){

            holder.message.text = ChatList.get(position).message

            holder.name.text = ChatList.get(position).name
            Picasso.with(ctx).load(ChatList.get(position).photoUrl).into(holder.profilePhoto)
        }else{
            holder.message.text = ChatList.get(position).message
            holder.name.text = ChatList.get(position).name
            Picasso.with(ctx).load(ChatList.get(position).photoUrl).into(holder.profilePhoto)
        }
    }

    override fun getItemCount(): Int {
        Log.d(TAG , "message Size : "+ChatList.size.toString())
      return ChatList.size
    }

}

data class chatInfo(
    val id : String,
    val timeStamp :String,
    val message : String ,
    val photoUrl:String,
    val name:String
)


package com.example.startup_app

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
        _db.child(user.toString()).setValue(user.toString())
        _db.child(userId.toString()).setValue(userId.toString())

        _db.child(user+"/"+userId+"/").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.removeAll(Collections.emptyList())
                for (postSnapshot in snapshot.children){
                    val id = snapshot.child(user+"/").key
                    var photourl:String? = ""
                    _db.child("profiles").addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if(id!=null) {

                                photourl = snapshot.child(id+ "/" + "profileUrl").value.toString()
                            }

                        }

                        override fun onCancelled(error: DatabaseError) {

                        }

                    })


                    for (snap in postSnapshot.children) {
                        val message = snap.value.toString()
                        chatList.add(chatInfo(id.toString(), message.toString() , photourl))
                    }


                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


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
                _db.child(user+"/"+userId+"/"+user+"/") .child( SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())).setValue(edit_message.text.toString())
                edit_message.setText("")


            }

        })
    }
}

class messageAdapter(applicationContext: Context, chatList: ArrayList<chatInfo>) : RecyclerView.Adapter<messageAdapter.messageViewHolder>(){
    val chatList = chatList
    val ctx = applicationContext
    val user = Firebase.auth.currentUser.uid

    class messageViewHolder(view:View) :RecyclerView.ViewHolder(view) {
            val message:TextView

        init {
            message = view.findViewById(R.id.chat_message)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): messageViewHolder {
        Log.d(TAG , " on crate view")
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_message , parent,false , )
        return messageViewHolder(view)
    }

    override fun onBindViewHolder(holder: messageViewHolder, position: Int) {

        Log.d(TAG ,"Chat List : "+chatList.toString())

        if (chatList.get(position).id==user){

            holder.message.text = chatList.get(position).message
            holder.message.gravity = Gravity.RIGHT
        }else{
            holder.message.text = chatList.get(position).message
            holder.message.gravity = Gravity.LEFT
        }
    }

    override fun getItemCount(): Int {
      return chatList.size
    }

}

data class chatInfo(
    val id :String,
    val message : String ,
    val photoUrl:String?
)


package com.example.startup_app

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.startup_app.see_profile.Companion.TAG
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList

class comment : AppCompatActivity() {
    private lateinit var writeComment:EditText
    private lateinit var postComment: Button
    private lateinit var commentPath :DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: commentAdapter
    private lateinit var commentList: MutableList<CommentData>
    private lateinit var storeInfo : MutableList<storeinfo>
    private var list:MutableList<String> = ArrayList()
    private  var path:String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)
        writeComment = findViewById(R.id.writeComment)
        postComment = findViewById(R.id.postComment)

        commentList = ArrayList()
        recyclerView = findViewById(R.id.listComment)
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        adapter = commentAdapter(applicationContext , commentList as ArrayList<CommentData>)

        recyclerView.adapter = adapter

        storeInfo = ArrayList()
         path = intent.extras?.getString("path")

        FirebaseDatabase.getInstance().getReferenceFromUrl(path.toString()).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                commentList.clear()
                for (i in snapshot.children){

                    val id = i.child("id").value.toString()
                    val comment = i.child("comment").value.toString()
                     FirebaseDatabase.getInstance().getReference("profiles").child(id+"/").addListenerForSingleValueEvent(
                         object : ValueEventListener {
                             override fun onDataChange(snapshot: DataSnapshot) {
                                 commentList.removeAll(Collections.emptyList())
                                     val name = snapshot.child("name").value.toString()
                                     val photoUrl = snapshot.child("profileUrl").value.toString()
                                     commentList.add(CommentData(name , comment , photoUrl))
                                 Log.d(TAG , "photoUrl " + photoUrl)

                             }

                             override fun onCancelled(error: DatabaseError) {

                             }
                         })

                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })



        commentPath = FirebaseDatabase.getInstance().getReferenceFromUrl(path.toString()).push()


        postComment.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                commentPath = FirebaseDatabase.getInstance().getReferenceFromUrl(path.toString()).push()
                commentList.clear()
                val comment =  writeComment.text.toString()
                writeComment.text.clear()
                commentPath.child("comment").setValue(comment)
                commentPath.child("id").setValue(Firebase.auth.currentUser.uid)
                update()

            }
        })
    }
    companion object{
        const val TAG = "Comment"
    }
    fun update(){
        FirebaseDatabase.getInstance().getReferenceFromUrl(path.toString()).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                commentList.clear()
                for (i in snapshot.children){

                    val id = i.child("id").value.toString()
                    val comment = i.child("comment").value.toString()
                    FirebaseDatabase.getInstance().getReference("profiles").child(id+"/").addListenerForSingleValueEvent(
                        object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                commentList.removeAll(Collections.emptyList())
                                val name = snapshot.child("name").value.toString()
                                val photoUrl = snapshot.child("profileUrl").value.toString()
                                commentList.add(CommentData(name , comment , photoUrl))
                                Log.d(TAG , "photoUrl " + photoUrl)

                            }

                            override fun onCancelled(error: DatabaseError) {

                            }
                        })

                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

}



class commentAdapter(applicationContext: Context, commentList: ArrayList<CommentData>) : RecyclerView.Adapter<commentAdapter.commentViewHolder>(){
    val ctx = applicationContext
    val comment_list = commentList
    class commentViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val photo:ImageView
        val name : TextView
        val comment:TextView
        init {
            photo = view.findViewById(R.id.comment_pic)
            name = view.findViewById(R.id.commentName)
            comment = view.findViewById(R.id.commentPosted)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): commentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.comment_card , parent , false)
        return commentViewHolder(view)
    }

    override fun onBindViewHolder(holder: commentViewHolder, position: Int) {
        Log.d(TAG , "comment_list"+comment_list)
        Picasso.with(ctx).load(comment_list.get(position).photoUrl).into(holder.photo)
        holder.name.text = comment_list.get(position).name
        holder.comment.text = comment_list.get(position).comment
    }

    override fun getItemCount(): Int {
        return comment_list.size
    }
}

data class CommentData(
    val name:String,
    val comment:String,
    val photoUrl : String
)

data class storeinfo(val id:String , val comment:String)
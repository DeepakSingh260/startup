package com.example.startup_app

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
//import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList


class Friend : Fragment() {


    private val _db = FirebaseDatabase.getInstance().getReference("profiles/")
    private lateinit var profileList :MutableList<profile_info>
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter : profileAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friend, container, false)
    }

    override fun onStart() {
        super.onStart()

        profileList = ArrayList<profile_info>()

        recyclerView = requireView().findViewById(R.id.cardviewrecycle)
        adapter  = profileAdapter(profileList , requireContext())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        _db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapshot in snapshot.children){
                    profileList.removeAll(Collections.emptyList())
                    Log.d(TAG , postSnapshot.toString())
                    val userID = postSnapshot.key.toString()
                    val photoUrl = postSnapshot.child("profileUrl/").value
                    val name = postSnapshot.child("name/").value
                    Log.d(TAG , "Name :" +name + "photo url : " + photoUrl)
                    val id = profile_info(name!! as String, photoUrl!! as String , userID!!)
                    profileList.add(id)
                }
                adapter.notifyDataSetChanged()
                Log.d(TAG ,"Data set changed " +profileList)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    companion object {
        private const val TAG ="Friends"
    }
}

class profileAdapter(profileList: List<profile_info>, requireContext: Context) : RecyclerView.Adapter<profileAdapter.profileViewHolder>() {

    var profileList = profileList
    var ctx = requireContext

    class profileViewHolder (view: View):RecyclerView.ViewHolder(view){
        var nameText :TextView
        var profilePic:ImageView
        var userid :TextView
        val NAME = "ID"

        init {
            nameText = view.findViewById(R.id.display_profile_name)
            profilePic = view.findViewById(R.id.display_profile_pic)
            userid = view.findViewById(R.id.userid)

            view.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {

                    view.context.startActivity(Intent(view.context , see_profile::class.java).apply {

                        putExtra(NAME, userid.text.toString())

                    })
                }
            })
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): profileViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_view , parent,false)

        return profileViewHolder(view)
    }

    override fun onBindViewHolder(holder: profileViewHolder, position: Int) {
        var index = holder.position
        Picasso.with(ctx).load(profileList.get(index).photoUrl).into(holder.profilePic)
        holder.nameText.text = profileList.get(index).name
        holder.userid.text = profileList.get(index).userID
        Log.d(TAG ,profileList.get(index).photoUrl +": url" + profileList.get(index).name+" name")
    }

    override fun getItemCount(): Int {
        Log.d(TAG ,"List size" + profileList.size)
        return profileList.size
    }
}

data class profile_info(
        val name:String ,
        val photoUrl: String,
        val userID : String
)
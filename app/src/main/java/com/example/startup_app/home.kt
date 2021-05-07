package com.example.startup_app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso


class home : Fragment() {

    private var _db = FirebaseDatabase.getInstance().getReference("follows/")
    private lateinit var profileList: MutableList<profile_data>
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: profileView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onStart() {
        super.onStart()
        profileList = ArrayList()
         recyclerView = requireView().findViewById(R.id.display_other_profile)
        adapter = profileView(requireContext() , profileList as ArrayList<profile_data>)

        recyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL , false)
        recyclerView.adapter = adapter
        _db.child(Firebase.auth.currentUser.uid+"/").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                profileList.clear()
                for (postSnapshot in snapshot.children){
                    val id = postSnapshot.child("id").value
                    val photoUrl = postSnapshot.child("photoUrl").value
                    val name = postSnapshot.child("name").value

                    profileList.add(profile_data(id.toString() , name.toString() , photoUrl.toString()))
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }


}
class profileView(requireContext: Context, profileList: ArrayList<profile_data>) : RecyclerView.Adapter<profileView.profileViewHolder>(){
    val ctx = requireContext
    val profileList = profileList

    class profileViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val name:TextView
        val pic:ImageView
        val messageButton:Button
        init {
            name = view.findViewById(R.id.profilename)
            pic = view.findViewById(R.id.profile_image_person)
            messageButton = view.findViewById(R.id.messageButton)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): profileViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.profilecardview , parent , false)
        return profileViewHolder(view)
    }

    override fun onBindViewHolder(holder: profileViewHolder, position: Int) {
        val name = profileList.get(position).name
        val id = profileList.get(position).id
        val photoUrl = profileList.get(position).profilePic

        holder.name.text = name
        Picasso.with(ctx).load(photoUrl).into(holder.pic)
        holder.messageButton.setOnClickListener(object :View.OnClickListener{
            override fun onClick(v: View?) {
                v!!.context.startActivity(Intent(ctx , message::class.java).apply {
                    putExtra("id" , id)
                })
            }
        })

    }

    override fun getItemCount(): Int {
        return profileList.size
    }
}

data class profile_data(
    val id:String,
    val name:String,
    val profilePic:String
)
package com.example.startup_app

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.startup_app.see_profile.Companion.TAG
import com.google.common.io.Resources.getResource
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlin.jvm.internal.FunctionReference


class home : Fragment() {

    private var _db = FirebaseDatabase.getInstance().getReference("follows/")
    private lateinit var profileList: MutableList<profile_data>
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: profileView
    private lateinit var recycleView:RecyclerView
    private lateinit var postList:MutableList<post_data>
    private lateinit var Adapter:PostData
    private var type:Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onStart() {
        super.onStart()
        postList = ArrayList()
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

        recycleView = requireView().findViewById(R.id.display_post)
        Adapter = PostData(requireContext() , postList as ArrayList<post_data>)
        recycleView.layoutManager = LinearLayoutManager(requireContext())
        recycleView.adapter= Adapter

        FirebaseDatabase.getInstance().getReference("profiles").child(Firebase.auth.currentUser.uid+"/").child("posts").addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    postList.clear()

                    for(shot in snapshot.children) {

                        val id = shot.child("id").value.toString()

                        FirebaseDatabase.getInstance().getReferenceFromUrl(id).addListenerForSingleValueEvent(
                            object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {

                                    val uid = snapshot.child("id").value.toString()
                                    Log.d(TAG , "ID" + snapshot.value)
                                    val postUrl = snapshot.child("photoUrl")?.value.toString()
                                    val blog = snapshot.child("blog").value?.toString()
                                    val likePath = snapshot.child("likes").ref
                                    val commentPath = snapshot.child("comments").ref
                                    if( snapshot.child("TYPE").value.toString().isDigitsOnly() && snapshot.child("TYPE").value!=null) {
                                        type=snapshot.child("TYPE").value.toString().toInt()
                                    }
                                    FirebaseDatabase.getInstance().getReference("profiles").child(uid+"/").addListenerForSingleValueEvent(
                                        object : ValueEventListener {
                                            override fun onDataChange(snapshot: DataSnapshot) {

                                                val name = snapshot.child("name").value.toString()
                                                val profileUrl = snapshot.child("profileUrl").value.toString()
                                                postList.add(post_data(id, name, profileUrl, postUrl , type , blog , likePath ,commentPath))

                                                Adapter.notifyDataSetChanged()
                                            }

                                            override fun onCancelled(error: DatabaseError) {

                                            }
                                        })

                                }

                                override fun onCancelled(error: DatabaseError) {

                                }
                            })

                    }


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

class PostData(requireContext: Context, postList: ArrayList<post_data>) : RecyclerView.Adapter< RecyclerView.ViewHolder>(){
    val ctx =requireContext
    val postList = postList

    class PostViewHolder (view: View) : RecyclerView.ViewHolder(view){
        val name:TextView
        val profilePhoto:ImageView
        val post:ImageView
        val likeButton : ImageButton
        val commentButton:ImageButton
        init {
            name = view.findViewById(R.id.display_name)
            profilePhoto = view.findViewById(R.id.profile_image_post)
            post = view.findViewById(R.id.post)
            likeButton = itemView.findViewById(R.id.likeButton)
            commentButton = itemView.findViewById(R.id.commentButton)
            var check = false

        }
    }
    class BlogPostHolder(view: View) : RecyclerView.ViewHolder(view){
        val pic:ImageView
        val name : TextView
        val blog:TextView
        val likeButton : ImageButton
        val commentButton:ImageButton
        init {
            pic = itemView.findViewById(R.id.my_image)
            name = itemView.findViewById(R.id.my_name)
            blog = itemView.findViewById(R.id.Blog)
            likeButton = itemView.findViewById(R.id.likeBlogButton)
            commentButton = itemView.findViewById(R.id.commentBlogButton)



        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType==1) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.postcardview, parent, false)
            return PostViewHolder(view)
        }else{
            val view = LayoutInflater.from(parent.context).inflate(R.layout.cardblogview, parent, false)
            return BlogPostHolder(view)
        }


    }

     fun ImagePost(holder: PostViewHolder, position: Int) {
        holder.name.text = postList.get(position).name
        Picasso.with(ctx).load(postList.get(position).profileUrl).into(holder.profilePhoto)
        Picasso.with(ctx).load(postList.get(position).postUrl).into(holder.post)

         var check = false
         postList.get(position).likePath.addValueEventListener(object :ValueEventListener{
             override fun onDataChange(snapshot: DataSnapshot) {
                 for(shot in snapshot.children){
                     if ( Firebase.auth.currentUser!!.uid==shot.value.toString()){
                         check = true
                         holder.likeButton.setBackgroundColor(ContextCompat.getColor(ctx , R.color.red))
                     }
                 }
             }

             override fun onCancelled(error: DatabaseError) {

             }
         })

         holder.likeButton.setOnClickListener(object :View.OnClickListener{
             override fun onClick(v: View?) {
                 if (!check){
                     check = true
                     postList.get(position).likePath.child(Firebase.auth.currentUser.uid).setValue(Firebase.auth.currentUser.uid)
                     holder.likeButton.setBackgroundColor(ContextCompat.getColor(ctx , R.color.red))
                 }else{
                     check = false
                     postList.get(position).likePath.child(Firebase.auth.currentUser.uid).removeValue()
                     holder.likeButton.setBackgroundColor(ContextCompat.getColor(ctx, R.color.fui_transparent))
                 }
             }
         })
         holder.commentButton.setOnClickListener(object :View.OnClickListener{
             override fun onClick(v: View?) {
                 val intent = Intent(ctx ,comment::class.java)

                 ctx.startActivity(intent.apply {
                     putExtra("path",postList.get(position).commentPath.ref.toString())

                 })

             }
         })
    }
    fun BlogPost(holder:BlogPostHolder , position: Int){
        Log.d(TAG ,"Blog Post")
        holder.name.text =postList.get(position).name
        holder.blog.text = postList.get(position)!!.blog
        Picasso.with(ctx).load( postList.get(position).profileUrl).into(holder.pic)
        var check = false
        postList.get(position).likePath.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(shot in snapshot.children){
                    if ( Firebase.auth.currentUser!!.uid==shot.value.toString()){
                        check = true
                        holder.likeButton.setBackgroundColor(ContextCompat.getColor(ctx , R.color.red))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        holder.likeButton.setOnClickListener(object :View.OnClickListener{
            override fun onClick(v: View?) {
                if (!check){
                    check = true
                    postList.get(position).likePath.child(Firebase.auth.currentUser.uid).setValue(Firebase.auth.currentUser.uid)
                    holder.likeButton.setBackgroundColor(ContextCompat.getColor(ctx , R.color.red))
                }else{
                    check = false
                    postList.get(position).likePath.child(Firebase.auth.currentUser.uid).removeValue()
                    holder.likeButton.setBackgroundColor(ContextCompat.getColor(ctx, R.color.fui_transparent))
                }
            }
        })

        holder.commentButton.setOnClickListener(object :View.OnClickListener{
            override fun onClick(v: View?) {
                val intent = Intent(ctx ,comment::class.java)

                        ctx.startActivity(intent.apply {
                    putExtra("path",postList.get(position).commentPath.ref.toString())

                })

            }
        })


    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun getItemViewType(position: Int): Int {
        if (postList.get(position)!!.blog == null){
            return 1
        }
        if((postList.get(position)!!.postUrl == null)){
            Log.d(TAG ,"blog post")
            return 2
        }
        else{
            Log.d(TAG , "default")
            return 2
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.d(TAG, "Post List " + postList)
        when(holder.itemViewType){
            1-> ImagePost(holder as PostViewHolder, position)
            2->BlogPost(holder as BlogPostHolder, position)
        }
    }

}

data class post_data(
    val id: String,
    val name: String,
    val profileUrl : String,
    val postUrl:String?,
    val TYPE :Int? ,
    val blog:String?,
    val likePath:DatabaseReference,
    val commentPath :DatabaseReference
)

data class profile_data(
    val id:String,
    val name:String,
    val profilePic:String
)
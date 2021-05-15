package com.example.startup_app

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.text.isDigitsOnly
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*

import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList


class profile : Fragment() {

    private lateinit var profile_name : TextView
    private lateinit var profile_image:ImageView
    private lateinit var edit_profile : Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapterImage: imageAdapter
    val user = Firebase.auth.currentUser
    private val _db = FirebaseDatabase.getInstance().getReference("posts/"+user.uid +"/")
    var imageList = ArrayList<post?>()
    private var type:Int?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onStart() {
        super.onStart()
        profile_name = requireView().findViewById(R.id.profile_name)
        profile_image = requireView().findViewById(R.id.profile_image)
        edit_profile = requireView().findViewById(R.id.edit_profile)
        recyclerView = requireView().findViewById(R.id.recyclerView)


        adapterImage = imageAdapter(imageList,  requireContext())



        recyclerView.adapter = adapterImage
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        _db.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                imageList.removeAll(Collections.emptyList())
                for (postSnapshot in snapshot.children.iterator()){

                        val photo = postSnapshot.child("photoUrl").value.toString()
                    if( postSnapshot.child("TYPE").value.toString().isDigitsOnly()){
                        type=postSnapshot.child("TYPE").value.toString().toInt()
                    }

                        val blog = postSnapshot.child("blog").value.toString()
                        val likePath = postSnapshot.child("likes").ref
                        val  commentPath = postSnapshot.child("comments").ref
                        imageList.add(post(photo , type , blog , likePath , commentPath))
                }
                Log.d(TAG , " IMG_LIST : " + imageList)
                adapterImage.notifyDataSetChanged()



            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


        for(i in imageList){
            Log.d(TAG ,"234" + i)

        }
        Log.d(TAG , imageList.size.toString() + "size")


        val mdataReference = FirebaseStorage.getInstance().getReference("storage")
//        val imageReference = FirebaseStorage.getInstance().getReference().child("images")
        val list = mdataReference.listAll()



        user?.let {
            for (profile in it.providerData){
                val name = profile.displayName
                val photoUrl = profile.photoUrl
//                Toast.makeText(requireContext() , photoUrl.toString() , Toast.LENGTH_SHORT).show()


                print(name+"name")
                print("photourl" + photoUrl)
                profile_name.setText(name)
                Picasso.with(requireContext()).load(photoUrl).into(profile_image)
            }
        }
        edit_profile.setOnClickListener(object : View.OnClickListener {

            override fun onClick(v: View?) {
                startActivity(Intent(requireContext() , set_profile::class.java) )
            }
        } )


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    companion object{
        private const val TAG = "profile"
    }

    override fun onResume() {
        super.onResume()
    }

}

private class imageAdapter(imageList: ArrayList<post?>, requireContext: Context) : RecyclerView.Adapter< RecyclerView.ViewHolder>()  {
    var ctx = requireContext
    var img_list = imageList

    class imageViewHolder(view: View):RecyclerView.ViewHolder(view) {
            val image:ImageView
            val name :TextView
            val pic :ImageView
            val likeButton : ImageButton
            val commentButton : ImageButton
            init {
                image = itemView.findViewById(R.id.img_view)
                pic = itemView.findViewById(R.id.your_image)
                name = itemView.findViewById(R.id.your_profile_name)
                likeButton = itemView.findViewById(R.id.likeViewButton)
                commentButton = itemView.findViewById(R.id.commentViewButton)
            }

    }


    class blogViewHolder (view: View) : RecyclerView.ViewHolder(view){
        val pic:ImageView
        val name : TextView
        val blog:TextView
        val likeButton :ImageButton
        val commentButton : ImageButton
        init {
            pic = itemView.findViewById(R.id.my_image)
            name = itemView.findViewById(R.id.my_name)
            blog = itemView.findViewById(R.id.Blog)
            likeButton = itemView.findViewById(R.id.likeBlogButton)
            commentButton = itemView.findViewById(R.id.commentBlogButton)


        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):  RecyclerView.ViewHolder  {
        Log.d(TAG , "on create view holder")
        if (viewType==1) {
            val view =LayoutInflater.from(parent.context).inflate(R.layout.viewholder, parent, false)
            return imageViewHolder(view)
        }
        else{
            val view =LayoutInflater.from(parent.context).inflate(R.layout.cardblogview, parent, false)
            return blogViewHolder(view)
        }

    }



    override fun getItemCount(): Int {

        if (img_list.size <1){
            return 0
        }
            return img_list.size
    }

    override fun getItemViewType(position: Int): Int {
        if (img_list.get(position)!!.type == 1){
            return 1
        }
        if((img_list.get(position)!!.type == 2)){
            return 2
        }
        else{
            return super.getItemViewType(position)
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when(holder.itemViewType){
            1-> imagePost(position , holder as imageViewHolder)
            2-> blogPost(position , holder as blogViewHolder)
        }

    }
    fun imagePost(position: Int, holder: imageViewHolder){
        Picasso.with(ctx).load(img_list.get(position)!!.photoUrl).into(holder.image)
        Picasso.with(ctx).load( Firebase.auth.currentUser.photoUrl).into(holder.pic)
        holder.name.text = Firebase.auth.currentUser.displayName

        var check = false
        img_list.get(position)!!.likePath.addValueEventListener(object :ValueEventListener{
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
                    img_list.get(position)!!.likePath.child(Firebase.auth.currentUser.uid).setValue(Firebase.auth.currentUser.uid)
                    holder.likeButton.setBackgroundColor(ContextCompat.getColor(ctx , R.color.red))
                }else{
                    check = false
                    img_list.get(position)!!.likePath.child(Firebase.auth.currentUser.uid).removeValue()
                    holder.likeButton.setBackgroundColor(ContextCompat.getColor(ctx, R.color.fui_transparent))
                }
            }
        })
        holder.commentButton.setOnClickListener(object :View.OnClickListener{
            override fun onClick(v: View?) {
                val intent = Intent(ctx ,comment::class.java)

                ctx.startActivity(intent.apply {
                    putExtra("path",img_list.get(position)!!.commentPath.ref.toString())

                })

            }
        })

    }

    fun blogPost(position: Int, holder: blogViewHolder){
        holder.name.text = Firebase.auth.currentUser.displayName
        holder.blog.text = img_list.get(position)!!.blog
        Picasso.with(ctx).load( Firebase.auth.currentUser.photoUrl).into(holder.pic)
        var check = false
        img_list.get(position)!!.likePath.addValueEventListener(object :ValueEventListener{
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
                    img_list.get(position)!!.likePath.child(Firebase.auth.currentUser.uid).setValue(Firebase.auth.currentUser.uid)
                    holder.likeButton.setBackgroundColor(ContextCompat.getColor(ctx , R.color.red))
                }else{
                    check = false
                    img_list.get(position)!!.likePath.child(Firebase.auth.currentUser.uid).removeValue()
                    holder.likeButton.setBackgroundColor(ContextCompat.getColor(ctx, R.color.fui_transparent))
                }
            }
        })
        holder.commentButton.setOnClickListener(object :View.OnClickListener{
            override fun onClick(v: View?) {
                val intent = Intent(ctx ,comment::class.java)

                ctx.startActivity(intent.apply {
                    putExtra("path",img_list.get(position)!!.commentPath.ref.toString())

                })

            }
        })
    }
}
data class post(val photoUrl:String? , val type:Int? , val blog:String? , val likePath:DatabaseReference , val commentPath:DatabaseReference )


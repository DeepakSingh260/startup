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
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList


class profile : Fragment() {

    private lateinit var profile_name : TextView
    private lateinit var profile_image:ImageView

    private lateinit var edit_profile : ImageButton
//    private lateinit var profile_Grid : GridView

    private lateinit var edit_profile : Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapterImage: imageAdapter
    val user = Firebase.auth.currentUser
    private val _db = FirebaseDatabase.getInstance().getReference("images/"+user.uid +"/")
    var imageList  = ArrayList<String?>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onStart() {
        super.onStart()

//        profile_Grid = requireView().findViewById(R.id.profile_grid)
//        profile_Grid.adapter = image_adapter(requireContext())
        val user = Firebase.auth.currentUser

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
                    imageList.add(postSnapshot.value.toString())

                }
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



>
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

private class imageAdapter(imageList: ArrayList<String?>, requireContext: Context) : RecyclerView.Adapter<imageAdapter.imageViewHolder>()  {
    var ctx = requireContext
    var img_list = imageList

    class imageViewHolder(view: View):RecyclerView.ViewHolder(view) {
            val image:ImageView
            init {
                image = view.findViewById(R.id.img_view)
            }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): imageViewHolder {
        Log.d(TAG , "on create view holder")
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder,parent , false)
        return imageViewHolder(view)

    }

    override fun onBindViewHolder(holder: imageViewHolder, position: Int) {

            var index = holder.adapterPosition
            Log.d(TAG ,"list 1"+img_list[index] )
            Picasso.with(ctx).load(img_list.get(position)!!.toUri()).into(holder.image)
//            holder.image.setImageURI(img_list[position].toUri())
    }

    override fun getItemCount(): Int {

        if (img_list.size <1){
            return 0
        }
            return img_list.size
    }
}
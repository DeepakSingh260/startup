package com.example.startup_app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso


class profile : Fragment() {

    private lateinit var profile_name : TextView
    private lateinit var profile_image:ImageView
    private lateinit var edit_profile : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onStart() {
        super.onStart()
        profile_name = requireView().findViewById(R.id.profile_name)
        profile_image = requireView().findViewById(R.id.profile_image)
        edit_profile = requireView().findViewById(R.id.edit_profile)
        val user = Firebase.auth.currentUser
        user?.let {
            for (profile in it.providerData){
                val name = profile.displayName
                val photoUrl = profile.photoUrl



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

}
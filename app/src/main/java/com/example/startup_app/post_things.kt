package com.example.startup_app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class post_things : Fragment() {
    private lateinit var writeABlog:TextView
    private lateinit var SelectFromGallery:TextView
    private lateinit var reDirectToCamera:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onStart() {
        super.onStart()
        writeABlog = requireView().findViewById(R.id.writeablog)
        SelectFromGallery = requireView().findViewById(R.id.postfromgallery)
        reDirectToCamera = requireView().findViewById(R.id.dircettocamera)



        reDirectToCamera.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                fragmentManager!!.beginTransaction().apply { replace(R.id.frame , camera()).commit() }
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_things, container, false)
    }

    companion object {
        const val TAG ="post something"
    }
}
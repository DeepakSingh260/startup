package com.example.startup_app

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class post_things : Fragment() {
    private lateinit var writeABlog:TextView
    private lateinit var SelectFromGallery:TextView
    private lateinit var reDirectToCamera:TextView
    val SELECT_PICTURE =100
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
        SelectFromGallery.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                GalleryOpener()
            }
        })
    }

    private fun GalleryOpener(){
        val intent = Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(intent , "Select Picture") , SELECT_PICTURE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK){
            if (requestCode == SELECT_PICTURE){
                val imageUri = data?.data
                if (null!=imageUri){

                    startActivity(Intent(requireContext() , uploadGallery::class.java).apply {
                        putExtra("imageUri" , imageUri.toString())
                    })
                }
            }
        }
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
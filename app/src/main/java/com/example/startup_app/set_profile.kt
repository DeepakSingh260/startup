 package com.example.startup_app

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.content.ContentResolverCompat.query
import com.google.android.gms.auth.api.signin.internal.Storage
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storageMetadata

 data class Image(val uri: Uri ,
    val  name:String ,
    val size:Int
)

 private val imageList = mutableListOf<Image>()


 class set_profile : AppCompatActivity() {
    val SELECT_PICTURE =100
     private  var  downloadUrl:Uri? = null
    private val user  = Firebase.auth.currentUser
    private lateinit var profile_img : ImageView
    private lateinit var change_img : Button
    private lateinit var save_changes : Button
    private lateinit var imageUri:Uri
    private lateinit var getName:EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_profile)

        getName = findViewById(R.id.edit_name)

        profile_img = findViewById(R.id.select_img)
        change_img = findViewById(R.id.select_photo)
        save_changes = findViewById(R.id.save)

        change_img.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                Toast.makeText(this@set_profile , "open gallery" , Toast.LENGTH_SHORT).show()
                imageCloser()
            }
        })

        save_changes.setOnClickListener(object : View.OnClickListener {

            override fun onClick(v: View?) {
                val profileUpdates = userProfileChangeRequest {
                    displayName = getName.text.toString()
                    photoUri = downloadUrl
                    Toast.makeText(applicationContext  , downloadUrl.toString() , Toast.LENGTH_SHORT).show()
                }

                user!!.updateProfile(profileUpdates).addOnCompleteListener{
                    if(it.isSuccessful){
                        Toast.makeText(this@set_profile ,"Save changes" , Toast.LENGTH_SHORT).show()

                    }
                }
            }

        })
    }

    fun imageCloser(){
        intent = Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(intent,"Select Pictire") ,SELECT_PICTURE)
    }

     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
         super.onActivityResult(requestCode, resultCode, data)
         if(resultCode == RESULT_OK){
             if (requestCode == SELECT_PICTURE){
                 imageUri= data?.data!!
                 if (null!= imageUri){
                     profile_img.setImageURI(imageUri)
                     val ref = FirebaseStorage.getInstance().reference?.child("profile_pic/" +user.uid )
                     val uploadTask = ref?.putFile(imageUri!!)

                     val urlTask = uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot , Task<Uri>> {
                         if (!it.isSuccessful){
                             it.exception?.let {
                                 throw it
                             }
                         }
                         return@Continuation ref.downloadUrl
                     })?.addOnCompleteListener{
                         if(it.isSuccessful){
                              downloadUrl = it.result!!

                         }
                     }
                 }
             }
         }
     }

    companion object{
        private const val TAG = "set profile"
    }
}

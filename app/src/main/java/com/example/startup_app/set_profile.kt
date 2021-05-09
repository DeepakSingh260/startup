 package com.example.startup_app

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask

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
    private lateinit var mPrgramming:CheckBox
     private lateinit var mMedical:CheckBox
     private lateinit var mReading:CheckBox
     private lateinit var mNetflix:CheckBox
     private lateinit var mDarkHumour:CheckBox
     private lateinit var mSarcastic:CheckBox
     private lateinit var mMusic:CheckBox
     private lateinit var mWorkout:CheckBox
     private  val _db = FirebaseDatabase.getInstance().getReference("profiles/")

     private lateinit var tagList: List<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_profile)

        getName = findViewById(R.id.edit_name)

        profile_img = findViewById(R.id.select_img)
        change_img = findViewById(R.id.select_photo)
        save_changes = findViewById(R.id.save)

        mPrgramming = findViewById(R.id.programming)
        mMedical = findViewById(R.id.medical)
        mReading = findViewById(R.id.reading)
        mNetflix = findViewById(R.id.Netflix)
        mDarkHumour = findViewById(R.id.darkHumour)
        mSarcastic = findViewById(R.id.sarcasm)
        mMusic = findViewById(R.id.music)
        mWorkout = findViewById(R.id.workout)

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

                tagList = checkBox()

                val id = User(getName.text.toString() , downloadUrl ,tagList )
                Log.d(TAG, "ID : "+id.toString())

                _db.child(user.uid+"/name").setValue(id.name)
                _db.child(user.uid+"/profileUrl").setValue(id.profileURI.toString())

                for (i in id.tagList) {
                    _db.child(user.uid + "/tagList/"+i).setValue(i)
                    }
                }

        })
    }

    fun imageCloser(){
        intent = Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(intent,"Select Picture") ,SELECT_PICTURE)
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

     fun checkBox():List<String>{
         val list:MutableList<String> = ArrayList()
         if(mPrgramming.isChecked){
             list.add(mPrgramming.text.toString())
         }
         if(mMedical.isChecked){
             list.add(mMedical.text.toString())
         }
         if(mMusic.isChecked){
             list.add(mMusic.text.toString())
         }
         if(mDarkHumour.isChecked){
             list.add(mDarkHumour.text.toString())
         }
         if(mReading.isChecked){
             list.add(mReading.text.toString())
         }
         if(mSarcastic.isChecked){
             list.add(mSarcastic.text.toString())
         }
         if(mWorkout.isChecked){
             list.add(mWorkout.text.toString())
         }
         if(mNetflix.isChecked){
             list.add(mNetflix.text.toString())
         }

         return list
     }

    companion object{
        private const val TAG = "set profile"
    }
}

 data class User(
         val name:String,
         val profileURI: Uri?,
         val tagList:List<String>
     )


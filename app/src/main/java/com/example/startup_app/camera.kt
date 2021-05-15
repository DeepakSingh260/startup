package com.example.startup_app

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Camera
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class camera : Fragment() {
    private lateinit var click: Button
    private lateinit var upload:Button
    private lateinit var download : Button
    private  var mcamera:Camera? = null
    private var mpreview:CameraPreview? = null
    private val MEDIA_TYPE_IMAGE:Int = 1
    private val MEDIA_TYPE_VIDEO:Int =   2
    private  lateinit var  pictureFile :File
    private  var Data:ByteArray? = null
    private lateinit var ctx: Context
    private val user = Firebase.auth.currentUser
    private  var downloadUrl:Uri? = null
    private lateinit var listID:MutableList<String>
    private  val _db = FirebaseDatabase.getInstance().getReference("posts/")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    private fun getOutputMediaFileuri(type: Int): Uri {
        return Uri.fromFile(getOutputMediaFile(type))
    }


    private val mPicture = Camera.PictureCallback { data, _ ->
            pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE) ?: run {
            Log.d(TAG, ("Error creating media file, check storage permissions"))

            return@PictureCallback
        }
        Data = data
        click.visibility = View.INVISIBLE
        upload.visibility = View.VISIBLE
        download.visibility = View.VISIBLE


    }

    private fun getOutputMediaFileUri(type: Int): Uri {
        return Uri.fromFile(getOutputMediaFile(type))
    }

    /** Create a File for saving an image or video */
    private fun getOutputMediaFile(type: Int): File? {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.


        val mediaStorageDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "MyCameraApp"
        )

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        mediaStorageDir.apply {
            if (!exists()) {
                if (!mkdirs()) {
                    mediaStorageDir.mkdir()
                    Log.d("MyCameraApp", "failed to create directory")
                    return null

                }
            }
        }

        // Create a media file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        return when (type) {
            MEDIA_TYPE_IMAGE -> {
                File("${mediaStorageDir.path}${File.separator}IMG_$timeStamp.jpg")
            }
            MEDIA_TYPE_VIDEO -> {
                File("${mediaStorageDir.path}${File.separator}VID_$timeStamp.mp4")
            }
            else -> null
        }
    }


    override fun onStart() {
        super.onStart()
        click = requireView().findViewById(R.id.click)
        upload = requireView().findViewById(R.id.upload)
        download = requireView().findViewById(R.id.download)
        upload.visibility = View.INVISIBLE
        download.visibility = View.INVISIBLE
        ctx = requireContext()
        listID = ArrayList()
        if (checkCameraHardware(ctx)){
            mcamera = getCameraInstance()
            mpreview = mcamera?.let {
                CameraPreview(requireContext() ,it)
            }

            mpreview?.also {
                val preview :FrameLayout = requireView().findViewById(R.id.camera_preview)
                preview.addView(it)
            }



            click.setOnClickListener{
                mcamera?.takePicture(null , null  , mPicture)
            }

            download.setOnClickListener {
                try {
                    val fos = FileOutputStream(pictureFile)
                    fos.write(Data)
                    Toast.makeText(requireContext() , "photo saved " , Toast.LENGTH_SHORT).show()

                    fos.close()
                } catch (e: FileNotFoundException) {
                    Log.d(TAG, "File not found: ${e.message}")
                } catch (e: IOException) {
                    Log.d(TAG, "Error accessing file: ${e.message}")
                }

            }
            FirebaseDatabase.getInstance().getReference("followers/").child(user.uid+"/").addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        listID.clear()
                        for(postSnapshot in snapshot.children){
                            Log.d(TAG ,"post snap shots"+ snapshot.children.toString())
                            val id = postSnapshot.child("id/").value
                            Log.d(TAG , "ID : " +id)
                            listID.add(id.toString())
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })

            upload.setOnClickListener {

                val ref = FirebaseStorage.getInstance().reference?.child("storage/" +user.uid+"/Img_"+SimpleDateFormat("yyyyMMdd_HHmmss").format(Date()))
                try {
                    val fos = FileOutputStream(pictureFile)
                    fos.write(Data)
                    Toast.makeText(requireContext() , "photo saved " , Toast.LENGTH_SHORT).show()

                    fos.close()
                } catch (e: FileNotFoundException) {
                    Log.d(TAG, "File not found: ${e.message}")
                } catch (e: IOException) {
                    Log.d(TAG, "Error accessing file: ${e.message}")
                }
                val uploadTask = ref.putFile(Uri.fromFile(pictureFile)!!)

                val urlTask = uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> {
                    if (!it.isSuccessful) {
                        it.exception?.let {
                            throw it
                        }
                    }

                    Toast.makeText(requireContext(), "uploaded", Toast.LENGTH_SHORT).show()

                    return@Continuation ref.downloadUrl
                })?.addOnCompleteListener {
                    if (it.isSuccessful){
                         downloadUrl = it.result!!
                        Toast.makeText(requireContext() , "uploaded" , Toast.LENGTH_SHORT).show()
                        val push = _db.child(user.uid+"/").push()
                        push.child("photoUrl").setValue(downloadUrl.toString())
                        push.child("TYPE").setValue(1)
                        push.child("comments").setValue("Comment")
                        push.child("likes").setValue("Like")
                        push.child("id").setValue(Firebase.auth.currentUser.uid)
                        val timeStamp = SimpleDateFormat("dd:MM:yyyy").format(Date())
                        push.child("timeStamp").setValue(timeStamp)

                        Log.d(TAG , "list id "+listID)
                        for (i in listID){
                            val pushPhoto = FirebaseDatabase.getInstance().getReference("profiles").child(i+"/").child("posts/").push()
                            pushPhoto.child("id").setValue(push.toString())
                        }
                    }
                }



            }


        }


    }



    fun getCameraInstance():Camera?{
        return try {
            Camera.open()
        }catch (e:Exception){
            null
        }
    }

    private fun checkCameraHardware(context:Context):Boolean{

        if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)){

            Toast.makeText(requireContext(), " Camera detected", Toast.LENGTH_SHORT).show()
            return true
        }else{
            return false
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }
    companion object{
        const val TAG = "snap"
    }


}

class  CameraPreview(context: Context , private val mcamera:Camera):SurfaceView(context) , SurfaceHolder.Callback{

    private val mHolder :SurfaceHolder = holder.apply {
        addCallback(this@CameraPreview)
        setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)

    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        mcamera.apply {
            try {
                setPreviewDisplay(holder)
                setDisplayOrientation(90)
                startPreview()
            }catch (e:IOException){
                Log.d(TAG , "Error settings camera ${e.message}")
            }
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        //
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

            if (mHolder.surface == null){
                return
            }
        try{
                mcamera.stopPreview()
        }catch (e:Exception){

        }

        mcamera.apply {
            try {
                setPreviewDisplay(mHolder)
                setDisplayOrientation(90)
                startPreview()
            }catch (e:Exception){
                Log.d(TAG ,"Error Starting camera preview ${e.message}")
            }
        }
    }



    companion object{
        private const val TAG = "PreviewCamera"
    }

}

class imageUrl{
    companion object Factory{
        fun create(): imageUrl = imageUrl()
    }
    var objectId:String? = null
    var taskDesc:String? = null
    var done:Boolean? = false
}
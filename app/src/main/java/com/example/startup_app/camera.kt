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
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


class camera : Fragment() {
    private lateinit var click: Button
    private  var mcamera:Camera? = null
    private var mpreview:CameraPreview? = null
    private val MEDIA_TYPE_IMAGE:Int = 1
    private val MEDIA_TYPE_VIDEO:Int =   2
    private lateinit var ctx: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    private fun getOutputMediaFileuri(type: Int): Uri {
        return Uri.fromFile(getOutputMediaFile(type))
    }


    private val mPicture = Camera.PictureCallback { data, _ ->
        val pictureFile: File = getOutputMediaFile(MEDIA_TYPE_IMAGE) ?: run {
            Log.d(TAG, ("Error creating media file, check storage permissions"))
            return@PictureCallback
        }

        try {
            val fos = FileOutputStream(pictureFile)
            fos.write(data)
            fos.close()
        } catch (e: FileNotFoundException) {
            Log.d(TAG, "File not found: ${e.message}")
        } catch (e: IOException) {
            Log.d(TAG, "Error accessing file: ${e.message}")
        }
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
                    Log.d("MyCameraApp", "failed to create directory")
                    mediaStorageDir.mkdir()
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
        ctx = requireContext()
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
        private const val TAG = "snap"
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
package com.example.startup_app

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Camera
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import android.widget.Toast
import java.io.IOException
import java.lang.Exception


class camera : Fragment() {

    private  var mcamera:Camera? = null
    private var mpreview:CameraPreview? = null
    private lateinit var ctx: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onStart() {
        super.onStart()
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
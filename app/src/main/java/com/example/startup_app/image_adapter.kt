package com.example.startup_app

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.*

class image_adapter (c:Context): BaseAdapter() {
    private lateinit var mContext: Context

    override fun getCount(): Int {
        return 0
    }



    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0

    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val imageView :ImageView
        if (convertView == null){
            imageView = ImageView(mContext)
            imageView.layoutParams.height=85
            imageView.layoutParams.width=85
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            imageView.setPadding(  8,8,8,8)
        }
        else{
//            imageView = (ImageView)convertView
            return convertView
        }
//        imageView.setImageResource()

        return imageView
    }

}
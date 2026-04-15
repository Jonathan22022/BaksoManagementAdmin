package com.example.baksomanagementadmin.data.remote

import android.content.Context
import android.util.Log
import com.cloudinary.android.MediaManager
import com.example.baksomanagementadmin.BuildConfig

object CloudinaryClient {

    fun init(context: Context) {

        val config = HashMap<String, String>()

        config["cloud_name"] = BuildConfig.CLOUDINARY_CLOUD_NAME
        config["api_key"] = BuildConfig.CLOUDINARY_API_KEY
        config["api_secret"] = BuildConfig.CLOUDINARY_API_SECRET

        MediaManager.init(context, config)

        Log.d("CloudinaryDebug", "Cloud name: ${BuildConfig.CLOUDINARY_CLOUD_NAME}")
        Log.d("CloudinaryDebug", "API key: ${BuildConfig.CLOUDINARY_API_KEY}")
    }
}
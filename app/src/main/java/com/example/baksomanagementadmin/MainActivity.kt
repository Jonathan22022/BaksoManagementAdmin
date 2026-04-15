package com.example.baksomanagementadmin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.baksomanagementadmin.data.remote.CloudinaryClient
import com.example.baksomanagementadmin.ui.FirstPageFragment
import com.example.baksomanagementadmin.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CloudinaryClient.init(this)
        setContentView(R.layout.activity_main)
    }
}
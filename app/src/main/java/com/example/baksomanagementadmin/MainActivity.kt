package com.example.baksomanagementadmin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.baksomanagementadmin.data.remote.CloudinaryClient
import com.example.baksomanagementadmin.ui.FirstPageFragment
import com.example.baksomanagementadmin.R
import com.example.baksomanagementadmin.utils.NotificationHelper
import com.example.baksomanagementadmin.utils.SessionManager
import com.example.baksomanagementadmin.utils.ThemeManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applyTheme(
            ThemeManager.getTheme(this)
        )
        super.onCreate(savedInstanceState)
        CloudinaryClient.init(this)
        NotificationHelper.createChannel(this)
        if (SessionManager.isSessionActive(this)) {
            val intent = Intent(this, HomepageActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

        setContentView(R.layout.activity_main)
    }

}
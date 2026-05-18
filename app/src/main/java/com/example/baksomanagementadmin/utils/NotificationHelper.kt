package com.example.baksomanagementadmin.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.baksomanagementadmin.R

object NotificationHelper {

    private const val CHANNEL_ID = "admin_order_channel"

    fun createChannel(context: Context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                CHANNEL_ID,
                "Admin Order Notification",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifikasi order baru masuk"
            }

            val manager =
                context.getSystemService(NotificationManager::class.java)

            manager.createNotificationChannel(channel)
        }
    }

    fun isNotificationEnabled(context: Context): Boolean {
        val sharedPref = context.getSharedPreferences(
            "app_settings",
            Context.MODE_PRIVATE
        )
        return sharedPref.getBoolean(
            "global_notification",
            true
        )
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showNewOrderNotification(context: Context) {

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Bakso Admin")
            .setContentText("Ada orderan baru masuk")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat
            .from(context)
            .notify(2001, notification)
    }
}
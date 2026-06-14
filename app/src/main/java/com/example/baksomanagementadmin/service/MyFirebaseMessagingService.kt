package com.example.baksomanagementadmin.service

import android.util.Log
import com.example.baksomanagementadmin.utils.NotificationHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService :
    FirebaseMessagingService() {

    override fun onMessageReceived(
        message: RemoteMessage
    ) {
        Log.d(
            "FCM_DEBUG",
            """
        NOTIFIKASI DITERIMA
        title = ${message.notification?.title}
        body  = ${message.notification?.body}
        orderId = ${message.data["orderId"]}
        userId  = ${message.data["userId"]}
        status  = ${message.data["status"]}
        """.trimIndent()
        )
        val title =
            message.notification?.title ?: "BaksoKu"

        val body =
            message.notification?.body ?: ""

        NotificationHelper.showNotification(
            this,
            title,
            body
        )
    }

    override fun onNewToken(
        token: String
    ) {

        val uid =
            FirebaseAuth.getInstance()
                .currentUser?.uid ?: return

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .update(
                "fcmToken",
                token
            )
    }
}
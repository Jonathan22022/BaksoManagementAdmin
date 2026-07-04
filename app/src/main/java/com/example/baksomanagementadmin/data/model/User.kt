package com.example.baksomanagementadmin.data.model

data class User(
    val userId: String = "",
    val nama: String = "",
    val email: String = "",
    val noTelp: String = "",
    val role: String = "admin",
    val profilePicture: String = "ic_account_",
    //default profile picture di res/drawable/ic_account_.xml
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val fcmToken: String = "",
    val alamat: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
)
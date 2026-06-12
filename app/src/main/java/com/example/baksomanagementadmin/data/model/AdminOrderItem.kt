package com.example.baksomanagementadmin.data.model

data class AdminOrderItem(
    val orderId:String = "",
    val userID:String = "",
    val namaUser:String = "",
    val createdAt:Long = System.currentTimeMillis(),
    val nama:String = "",
    val imageUrl:String = "",
    val addons: List<AddOn> = emptyList(),
    val quantity:Int = 0,
    val total:Int = 0,
    val status:String = "",
    val completed:Boolean = false,
    val itemCount:Int = 1
)
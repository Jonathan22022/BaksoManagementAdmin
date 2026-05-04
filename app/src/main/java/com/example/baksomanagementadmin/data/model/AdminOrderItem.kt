package com.example.baksomanagementadmin.data.model

data class AdminOrderItem(
    val orderId: String = "",
    val userID: String = "",
    val createdAt: Long = 0,
    val nama: String = "",
    val addons: List<AddOn> = emptyList(),
    val quantity: Int = 0,
    val total: Int = 0
)
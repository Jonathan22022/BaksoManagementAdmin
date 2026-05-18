package com.example.baksomanagementadmin.data.model

data class HistoryOrder(
    val orderId: String = "",
    val userID: String = "",
    val nama: String = "",
    val imageUrl: String = "",
    val createdAt: Long = 0L,
    val status: String = "",
    val addons: List<AddOn> = emptyList(),
    val quantity: Int = 0,
    val total: Int = 0
)
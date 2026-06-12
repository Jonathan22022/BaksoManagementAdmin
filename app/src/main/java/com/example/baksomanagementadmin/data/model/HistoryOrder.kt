package com.example.baksomanagementadmin.data.model

data class HistoryOrder(
    val orderId: String = "",
    val userID: String = "",
    val nama: String = "",
    val imageUrl: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val status: String = "",
    val addons: List<AddOn> = emptyList(),
    val quantity: Int = 0,
    val total: Int = 0,
    var selected: Boolean = false
)

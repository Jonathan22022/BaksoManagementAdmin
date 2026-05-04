package com.example.baksomanagementadmin.data.model

data class OrderItem(
    val id: String = "",
    val menu_id: String = "",
    val nama: String = "",
    val harga: Int = 0,
    val quantity: Int = 0,
    val catatan: String = "",
    val addons: List<AddOn> = emptyList()
)
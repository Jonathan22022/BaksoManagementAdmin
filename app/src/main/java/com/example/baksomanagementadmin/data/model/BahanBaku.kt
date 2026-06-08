package com.example.baksomanagementadmin.data.model

data class BahanBaku(
    val id: String = "",
    val nama: String = "",
    val harga: Int = 0,
    val berat: Double = 0.0,
    val satuan: String = "kg",
    val gambarUrl: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
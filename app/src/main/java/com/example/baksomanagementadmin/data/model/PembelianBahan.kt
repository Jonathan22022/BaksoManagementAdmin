package com.example.baksomanagementadmin.data.model

data class PembelianBahan(
    val id: String = "",
    val bahanId: String = "",
    val namaBahan: String = "",
    val beratBeli: Double = 0.0,
    val hargaBeli: Int = 0,
    val satuan: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
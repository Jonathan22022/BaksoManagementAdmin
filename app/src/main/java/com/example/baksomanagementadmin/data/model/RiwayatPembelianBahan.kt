package com.example.baksomanagementadmin.data.model

data class RiwayatPembelianBahan(
    val id: String = "",
    val bahanId: String = "",
    val namaBahan: String = "",
    val gambarUrl: String = "",
    val jumlah: Double = 0.0,
    val hargaBeli: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

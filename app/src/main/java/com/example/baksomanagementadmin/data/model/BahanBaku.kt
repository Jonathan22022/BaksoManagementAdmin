package com.example.baksomanagementadmin.data.model

data class BahanBaku(
    val id: String = "",

    val nama: String = "",

    // modal awal
    val hargaAwal: Int = 0,
    val beratAwal: Double = 0.0,

    // stok sekarang
    val hargaTerbaru: Int = 0,
    val berat: Double = 0.0,

    val satuan: String = "",
    val gambarUrl: String = "",

    val createdAt: Long = System.currentTimeMillis()
)
package com.example.baksomanagementadmin.data.model

data class KekuranganBahan(
    val bahanId: String,
    val namaBahan: String,
    val kebutuhan: Double,
    val stok: Double,
    val satuan: String,
    val kekurangan: Double
)
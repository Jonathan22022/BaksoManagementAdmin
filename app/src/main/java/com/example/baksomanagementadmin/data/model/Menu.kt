package com.example.baksomanagementadmin.data.model

data class Menu(
    val id: String = "",
    val namaMenu: String = "",
    val harga: Int = 0,
    val gambarUrl: String = "",
    val description: String = ""
    //val bahanList: List<BahanItem> = emptyList()
)
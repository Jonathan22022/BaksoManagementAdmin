package com.example.baksomanagementadmin.data.model

data class Menu(
    val id: String = "",
    val namaMenu: String = "",
    val harga: Int = 0,
    val gambarUrl: String = "",

    val bihun: Boolean = false,
    val mie: Boolean = false,
    val keduanya: Boolean = false,

    val addons: List<AddOn> = emptyList()
)
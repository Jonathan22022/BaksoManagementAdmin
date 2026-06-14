package com.example.baksomanagementadmin.data.model

data class MenuStok(
    val menu: Menu,
    val tersedia: Boolean,
    val kekurangan: List<KekuranganBahan>
)
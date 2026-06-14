package com.example.baksomanagementadmin.service

import com.example.baksomanagementadmin.data.model.BahanBaku
import com.example.baksomanagementadmin.data.model.KekuranganBahan
import com.example.baksomanagementadmin.data.model.Menu

object MenuStokChecker {

    fun checkMenuStock(
        menu: Menu,
        bahanList: List<BahanBaku>
    ): List<KekuranganBahan> {

        val kekuranganList =
            mutableListOf<KekuranganBahan>()

        menu.bahanList.forEach { kebutuhan ->

            val bahan =
                bahanList.find {
                    it.id == kebutuhan.bahanId
                }

            if (bahan != null &&
                bahan.berat < kebutuhan.jumlah
            ) {

                kekuranganList.add(
                    KekuranganBahan(
                        bahanId = bahan.id,
                        namaBahan = bahan.nama,
                        kebutuhan = kebutuhan.jumlah,
                        stok = bahan.berat,
                        satuan = bahan.satuan,
                        kekurangan =
                            kebutuhan.jumlah - bahan.berat
                    )
                )
            }
        }

        return kekuranganList
    }
}
package com.example.baksomanagementadmin.service

import com.example.baksomanagementadmin.data.model.AddOn
import com.example.baksomanagementadmin.data.model.BahanBaku
import com.example.baksomanagementadmin.data.model.KekuranganBahan

object AddOnStokChecker {

    fun checkAddOnStock(
        addOn: AddOn,
        bahanList: List<BahanBaku>
    ): List<KekuranganBahan> {

        val result = mutableListOf<KekuranganBahan>()

        addOn.bahanList.forEach { kebutuhan ->

            val bahan =
                bahanList.find {
                    it.id == kebutuhan.bahanId
                }

            if (
                bahan != null &&
                bahan.berat < kebutuhan.jumlah
            ) {

                result.add(
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

        return result
    }
}
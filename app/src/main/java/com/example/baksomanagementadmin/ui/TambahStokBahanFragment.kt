package com.example.baksomanagementadmin.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Button
import android.widget.Toast
import com.example.baksomanagementadmin.R
import com.example.baksomanagementadmin.data.model.BahanBaku
import com.example.baksomanagementadmin.data.repository.BahanBakuRepository
import com.example.baksomanagementadmin.data.model.RiwayatPembelianBahan
import com.example.baksomanagementadmin.data.remote.FirebaseClient
import com.example.baksomanagementadmin.data.repository.RiwayatPembelianRepository

class TambahStokBahanFragment : Fragment() {

    private lateinit var repository: BahanBakuRepository
    private val riwayatRepository = RiwayatPembelianRepository()
    private lateinit var spBahan: Spinner
    private lateinit var etHarga: EditText
    private lateinit var etJumlah: EditText
    private lateinit var btnSimpan: Button

    private var bahanList = listOf<BahanBaku>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.fragment_tambah_stok_bahan,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        repository = BahanBakuRepository()

        spBahan = view.findViewById(R.id.spBahan)
        etHarga = view.findViewById(R.id.etHargaBeli)
        etJumlah = view.findViewById(R.id.etJumlah)
        btnSimpan = view.findViewById(R.id.btnSimpan)

        loadBahan()

        btnSimpan.setOnClickListener {
            simpan()
        }
    }

    private fun loadBahan() {

        repository.getBahanBakuList { list ->

            bahanList = list

            val namaList = list.map { it.nama }

            val adapter = ArrayAdapter(
                requireContext(),
                R.layout.item_spinner_status,
                namaList
            )

            adapter.setDropDownViewResource(
                R.layout.item_spinner_status_dropdown
            )

            spBahan.adapter = adapter
        }
    }

    private fun simpan() {

        val bahan =
            bahanList[spBahan.selectedItemPosition]

        val harga =
            etHarga.text.toString()
                .toIntOrNull() ?: 0

        val jumlah =
            etJumlah.text.toString()
                .toDoubleOrNull() ?: 0.0

        if (harga <= 0 || jumlah <= 0.0)
            return

        val stokBaru =
            bahan.berat + jumlah

        val updated = bahan.copy(
            berat = bahan.berat + jumlah,
            hargaTerbaru = harga
        )

        repository.updateBahanBaku(
            updated,
            onSuccess = {

                riwayatRepository.addPembelian(
                    RiwayatPembelianBahan(
                        bahanId = bahan.id,
                        namaBahan = bahan.nama,
                        gambarUrl = bahan.gambarUrl,
                        jumlah = jumlah,
                        hargaBeli = harga,
                        createdAt = System.currentTimeMillis()
                    ),
                    onSuccess = {},
                    onError = {}
                )

                Toast.makeText(
                    requireContext(),
                    "Stok berhasil ditambah",
                    Toast.LENGTH_SHORT
                ).show()
            },
            onError = {}
        )
    }
}
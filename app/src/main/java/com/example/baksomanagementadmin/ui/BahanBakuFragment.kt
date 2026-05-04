package com.example.baksomanagementadmin.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.baksomanagementadmin.R
import com.example.baksomanagementadmin.data.model.BahanBaku
import com.example.baksomanagementadmin.data.repository.BahanBakuRepository

class BahanBakuFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    private val bahanBakuRepository = BahanBakuRepository()

    private val TAG = "BahanBakuFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_bahan_baku, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.recyclerBahanBaku)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val btnTambah = view.findViewById<Button>(R.id.btnTambahBahan)
        btnTambah.setOnClickListener {
            findNavController().navigate(R.id.action_bahanBakuFragment_to_addBahanBakuFragment)
        }
        loadBahanBaku()
    }

    private fun loadBahanBaku() {
        Log.d(TAG, "Memuat data bahan baku...")

        bahanBakuRepository.getBahanBakuList { list ->
            Log.d(TAG, "Jumlah data: ${list.size}")

            recyclerView.adapter = BahanBakuAdapter(
                list,
                onEdit = { bahanBaku ->
                    openEditBahanBaku(bahanBaku)
                },
                onDelete = { bahanBaku ->
                    deleteBahanBaku(bahanBaku)
                }
            )
        }
    }

    private fun openEditBahanBaku(bahanBaku: BahanBaku) {
        val bundle = Bundle().apply {
            putString("BAHANBAKU_ID", bahanBaku.id)
        }

        findNavController().navigate(
            R.id.action_bahanBakuFragment_to_editBahanBakuFragment,
            bundle
        )
    }

    private fun deleteBahanBaku(bahanBaku: BahanBaku) {
        Log.d(TAG, "Delete: ${bahanBaku.id}")

        bahanBakuRepository.deleteBahanBaku(
            bahanBaku,
            onSuccess = {
                Log.d(TAG, "Berhasil hapus")
                loadBahanBaku() // 🔥 sekarang valid
            },
            onError = {
                Log.e(TAG, "Gagal hapus: ${it.message}")
            }
        )
    }
}
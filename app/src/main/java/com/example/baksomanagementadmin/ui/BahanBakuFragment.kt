package com.example.baksomanagementadmin.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.baksomanagementadmin.R
import com.example.baksomanagementadmin.data.model.BahanBaku
import com.example.baksomanagementadmin.data.repository.BahanBakuRepository
import com.example.baksomanagementadmin.data.repository.RiwayatPembelianRepository

enum class TabBahan {
    MODAL_AWAL,
    MODAL_TERBARU,
    RIWAYAT
}

class BahanBakuFragment : Fragment() {

    private lateinit var btnModalAwal: TextView
    private lateinit var btnModalTerbaru: TextView
    private lateinit var btnRiwayat: TextView

    private lateinit var recyclerModalAwal: RecyclerView
    private lateinit var recyclerModalTerbaru: RecyclerView
    private lateinit var recyclerRiwayat: RecyclerView

    private lateinit var headerModalAwal: View
    private lateinit var headerModalTerbaru: View
    private lateinit var headerRiwayat: View

    private var currentTab = TabBahan.MODAL_AWAL

    private val bahanBakuRepository = BahanBakuRepository()
    private val riwayatRepository = RiwayatPembelianRepository()

    private lateinit var btnTambah: Button
    private lateinit var btnTambahStok: Button

    private val TAG = "BahanBakuFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        Log.d(TAG, "onCreateView() dipanggil")

        return inflater.inflate(
            R.layout.fragment_bahan_baku,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        Log.d(TAG, "onViewCreated() dipanggil")

        recyclerModalAwal =
            view.findViewById(R.id.recyclerBahanBaku)

        recyclerModalTerbaru =
            view.findViewById(R.id.recyclerBahanBakuTerbaru)

        recyclerRiwayat =
            view.findViewById(R.id.recyclerRiwayatPembelian)

        recyclerModalAwal.layoutManager =
            LinearLayoutManager(requireContext())

        recyclerModalTerbaru.layoutManager =
            LinearLayoutManager(requireContext())

        recyclerRiwayat.layoutManager =
            LinearLayoutManager(requireContext())

        btnModalAwal =
            view.findViewById(R.id.btnModalAwal)

        btnModalTerbaru =
            view.findViewById(R.id.btnUpdateModalTerbaru)

        btnRiwayat =
            view.findViewById(R.id.btnRiwayatModal)

        btnTambahStok =
            view.findViewById(R.id.btnTambahStok)

        btnTambah =
            view.findViewById(R.id.btnTambahBahan)

        headerModalAwal =
            view.findViewById(R.id.headerModalAwal)

        headerModalTerbaru =
            view.findViewById(R.id.headerModalTerbaru)

        headerRiwayat =
            view.findViewById(R.id.headerRiwayat)

        btnTambahStok.setOnClickListener {

            Log.d(TAG, "Klik Tambah Stok")

            findNavController().navigate(
                R.id.action_bahanBakuFragment_to_tambahStokBahanFragment
            )
        }

        btnTambah.setOnClickListener {

            Log.d(TAG, "Klik Tambah Bahan")

            findNavController().navigate(
                R.id.action_bahanBakuFragment_to_addBahanBakuFragment
            )
        }

        btnModalAwal.setOnClickListener {

            Log.d(TAG, "Tab MODAL_AWAL dipilih")

            currentTab = TabBahan.MODAL_AWAL

            updateTabUI()

            loadModalAwal()
        }

        btnModalTerbaru.setOnClickListener {

            Log.d(TAG, "Tab MODAL_TERBARU dipilih")

            currentTab = TabBahan.MODAL_TERBARU

            updateTabUI()

            loadModalTerbaru()
        }

        btnRiwayat.setOnClickListener {

            Log.d(TAG, "Tab RIWAYAT dipilih")

            currentTab = TabBahan.RIWAYAT

            updateTabUI()

            loadRiwayat()
        }

        updateTabUI()

        loadModalAwal()
    }

    private fun updateTabUI() {

        Log.d(TAG, "updateTabUI() -> $currentTab")

        if (currentTab == TabBahan.MODAL_AWAL) {

            btnModalAwal.setBackgroundResource(
                R.drawable.bg_tab_active
            )

            btnModalAwal.setTextColor(
                resources.getColor(
                    android.R.color.white,
                    null
                )
            )

        } else {

            btnModalAwal.background = null

            btnModalAwal.setTextColor(
                resources.getColor(
                    R.color.red,
                    null
                )
            )
        }

        if (currentTab == TabBahan.MODAL_TERBARU) {

            btnModalTerbaru.setBackgroundResource(
                R.drawable.bg_tab_active
            )

            btnModalTerbaru.setTextColor(
                resources.getColor(
                    android.R.color.white,
                    null
                )
            )

        } else {

            btnModalTerbaru.background = null

            btnModalTerbaru.setTextColor(
                resources.getColor(
                    R.color.red,
                    null
                )
            )
        }

        if (currentTab == TabBahan.RIWAYAT) {

            btnRiwayat.setBackgroundResource(
                R.drawable.bg_tab_active
            )

            btnRiwayat.setTextColor(
                resources.getColor(
                    android.R.color.white,
                    null
                )
            )

        } else {

            btnRiwayat.background = null

            btnRiwayat.setTextColor(
                resources.getColor(
                    R.color.red,
                    null
                )
            )
        }

        headerModalAwal.visibility =
            if (currentTab == TabBahan.MODAL_AWAL)
                View.VISIBLE
            else
                View.GONE

        headerModalTerbaru.visibility =
            if (currentTab == TabBahan.MODAL_TERBARU)
                View.VISIBLE
            else
                View.GONE

        headerRiwayat.visibility =
            if (currentTab == TabBahan.RIWAYAT)
                View.VISIBLE
            else
                View.GONE
    }

    private fun updateActionButtons(
        hasData: Boolean
    ) {

        Log.d(TAG, "updateActionButtons() hasData=$hasData")

        btnTambah.visibility = View.VISIBLE

        btnTambahStok.visibility =
            if (hasData)
                View.VISIBLE
            else
                View.GONE
    }

    private fun loadModalAwal() {

        Log.d(TAG, "loadModalAwal()")

        bahanBakuRepository.getBahanBakuList { list ->

            Log.d(
                TAG,
                "Modal Awal berhasil dimuat. Jumlah data = ${list.size}"
            )

            updateActionButtons(
                list.isNotEmpty()
            )

            recyclerModalAwal.adapter =
                BahanBakuAdapter(
                    list,
                    onEdit = {
                        openEditBahanBaku(it)
                    },
                    onDelete = {
                        deleteBahanBaku(it)
                    }
                )

            Log.d(
                TAG,
                "Adapter Modal Awal berhasil dipasang"
            )
        }
    }

    private fun loadModalTerbaru() {

        Log.d(TAG, "loadModalTerbaru()")

        bahanBakuRepository.getBahanBakuList { list ->

            Log.d(
                TAG,
                "Modal Terbaru berhasil dimuat. Jumlah data = ${list.size}"
            )

            updateActionButtons(
                list.isNotEmpty()
            )

            recyclerModalTerbaru.adapter =
                ModalTerbaruAdapter(
                    list,
                    onEdit = {
                        openEditBahanBaku(it)
                    },
                    onDelete = {
                        deleteBahanBaku(it)
                    }
                )

            Log.d(
                TAG,
                "Adapter Modal Terbaru berhasil dipasang"
            )
        }
    }

    private fun loadRiwayat() {

        Log.d(TAG, "loadRiwayat()")

        bahanBakuRepository.getBahanBakuList { listBahan ->

            Log.d(
                TAG,
                "Jumlah bahan baku = ${listBahan.size}"
            )

            updateActionButtons(
                listBahan.isNotEmpty()
            )

            riwayatRepository.getAllPembelian { listRiwayat ->

                Log.d(
                    TAG,
                    "Jumlah riwayat pembelian = ${listRiwayat.size}"
                )

                listRiwayat.forEachIndexed { index, item ->

                    Log.d(
                        TAG,
                        """
                        Riwayat[$index]
                        nama=${item.namaBahan}
                        jumlah=${item.jumlah}
                        harga=${item.hargaBeli}
                        createdAt=${item.createdAt}
                        gambar=${item.gambarUrl}
                        """.trimIndent()
                    )
                }

                recyclerRiwayat.adapter =
                    RiwayatPembelianAdapter(
                        listRiwayat
                    )

                Log.d(
                    TAG,
                    "Adapter Riwayat berhasil dipasang"
                )
            }
        }
    }

    private fun openEditBahanBaku(
        bahanBaku: BahanBaku
    ) {


        val bundle = Bundle().apply {

            putString(
                "BAHANBAKU_ID",
                bahanBaku.id
            )
        }

        findNavController().navigate(
            R.id.action_bahanBakuFragment_to_editBahanBakuFragment,
            bundle
        )
    }

    private fun deleteBahanBaku(
        bahanBaku: BahanBaku
    ) {

        bahanBakuRepository.deleteBahanBaku(
            bahanBaku,
            onSuccess = {

                Log.d(
                    TAG,
                    "Berhasil menghapus bahan baku"
                )

                when (currentTab) {

                    TabBahan.MODAL_AWAL ->
                        loadModalAwal()

                    TabBahan.MODAL_TERBARU ->
                        loadModalTerbaru()

                    TabBahan.RIWAYAT ->
                        loadRiwayat()
                }
            },
            onError = {

                Log.e(
                    TAG,
                    "Gagal hapus data",
                    it
                )
            }
        )
    }
}
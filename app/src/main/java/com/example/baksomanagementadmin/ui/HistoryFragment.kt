package com.example.baksomanagementadmin.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.baksomanagementadmin.R
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.baksomanagementadmin.data.model.History
import com.example.baksomanagementadmin.ui.HistoryAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AlertDialog
import android.widget.Toast
import androidx.navigation.fragment.findNavController

class HistoryFragment : Fragment() {

    private lateinit var adapter: HistoryAdapter
    private val historyList = mutableListOf<History>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_history, container, false)

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerHistory)
        val emptyLayout = view.findViewById<View>(R.id.layoutEmptyHistory)
        val btnAddOrder = view.findViewById<View>(R.id.btnAddOrder)

        recycler.layoutManager = LinearLayoutManager(requireContext())

        val fabDelete = view.findViewById<FloatingActionButton>(R.id.fabDelete)
        adapter = HistoryAdapter(historyList)

        if (historyList.isEmpty()) {
            recycler.visibility = View.GONE
            emptyLayout.visibility = View.VISIBLE
            fabDelete.visibility = View.GONE
        } else {
            recycler.visibility = View.VISIBLE
            emptyLayout.visibility = View.GONE
            fabDelete.visibility = View.VISIBLE
            recycler.adapter = adapter
        }

        // BUTTON TAMBAH PESANAN
        btnAddOrder.setOnClickListener {
            findNavController().navigate(R.id.action_historyFragment_to_menuFragment)
        }

        // FAB DELETE
        fabDelete.setOnClickListener {

            AlertDialog.Builder(requireContext())
                .setTitle("Hapus History")
                .setMessage("Apakah kamu yakin ingin menghapus semua history?")
                .setPositiveButton("Hapus") { _, _ ->

                    historyList.clear()
                    adapter.notifyDataSetChanged()

                    Toast.makeText(
                        requireContext(),
                        "History berhasil dihapus",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .setNegativeButton("Batal", null)
                .show()
        }

        return view
    }
}
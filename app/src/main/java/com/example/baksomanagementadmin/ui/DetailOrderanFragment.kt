package com.example.baksomanagementadmin.ui

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.baksomanagementadmin.R
import com.example.baksomanagementadmin.data.model.OrderItem
import com.example.baksomanagementadmin.data.remote.FirebaseClient

class DetailOrderanFragment : Fragment(R.layout.fragment_detail_orderan) {

    private val firestore = FirebaseClient.firestore

    private lateinit var tvTitle: TextView
    private lateinit var tvUser: TextView

    private lateinit var rvItems: RecyclerView
    private lateinit var spStatus: Spinner

    private lateinit var btnSelesai: Button
    private lateinit var btnCancel: Button

    private var orderId: String = ""

    private val statusList = listOf(
        "pending",
        "diproses",
        "selesai",
        "dibatalkan"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        orderId = arguments?.getString("ORDER_ID") ?: ""
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)

        setupRecyclerView()

        setupSpinner()

        loadOrderDetail()

        btnSelesai.setOnClickListener {
            showConfirmationDialog("selesai")
        }

        btnCancel.setOnClickListener {
            showConfirmationDialog("dibatalkan")
        }

        val btnUpdateStatus =
            view.findViewById<Button>(R.id.btnUpdateStatus)

        btnUpdateStatus.setOnClickListener {

            val selectedStatus =
                spStatus.selectedItem.toString()

            updateStatus(selectedStatus)
        }
    }

    private fun initViews(view: View) {

        tvTitle = view.findViewById(R.id.tvTitle)
        tvUser = view.findViewById(R.id.tvUser)

        rvItems = view.findViewById(R.id.rvItems)
        spStatus = view.findViewById(R.id.spStatus)

        btnSelesai = view.findViewById(R.id.btnSelesai)
        btnCancel = view.findViewById(R.id.btnCancel)
    }

    private fun setupRecyclerView() {

        rvItems.layoutManager =
            LinearLayoutManager(requireContext())
    }

    private fun setupSpinner() {

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            statusList
        )

        spStatus.adapter = adapter
    }

    private fun loadOrderDetail() {

        firestore.collection("orders")
            .document(orderId)
            .get()
            .addOnSuccessListener { orderDoc ->

                if (!orderDoc.exists()) {

                    Toast.makeText(
                        requireContext(),
                        "Order tidak ditemukan",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@addOnSuccessListener
                }

                val userId =
                    orderDoc.getString("userID") ?: "-"

                val status =
                    orderDoc.getString("status") ?: "pending"

                tvTitle.text = "Orderan #$orderId"

                tvUser.text = "User ID : $userId"

                val selectedPosition =
                    statusList.indexOf(status)

                if (selectedPosition >= 0) {
                    spStatus.setSelection(selectedPosition)
                }

                loadOrderItems()
            }
            .addOnFailureListener {

                Toast.makeText(
                    requireContext(),
                    "Gagal mengambil data order",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun loadOrderItems() {

        firestore.collection("orders")
            .document(orderId)
            .collection("items")
            .get()
            .addOnSuccessListener { result ->

                val itemList =
                    mutableListOf<OrderItem>()

                result.documents.forEach { document ->

                    val item =
                        document.toObject(
                            OrderItem::class.java
                        )

                    if (item != null) {
                        itemList.add(item)
                    }
                }

                rvItems.adapter =
                    AdminDetailItemAdapter(itemList)
            }
            .addOnFailureListener {

                Toast.makeText(
                    requireContext(),
                    "Gagal mengambil item order",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun showConfirmationDialog(
        status: String
    ) {

        val message =
            if (status == "selesai") {
                "Yakin ingin menyelesaikan orderan ini?"
            } else {
                "Yakin ingin membatalkan orderan ini?"
            }

        AlertDialog.Builder(requireContext())
            .setTitle("Konfirmasi")
            .setMessage(message)

            .setPositiveButton("Ya") { _, _ ->

                updateStatus(status)
            }

            .setNegativeButton(
                "Tidak",
                null
            )
            .show()
    }

    private fun updateStatus(
        status: String
    ) {

        firestore.collection("orders")
            .document(orderId)
            .update("status", status)

            .addOnSuccessListener {

                Toast.makeText(
                    requireContext(),
                    "Status berhasil diperbarui",
                    Toast.LENGTH_SHORT
                ).show()

                requireActivity()
                    .onBackPressedDispatcher
                    .onBackPressed()
            }

            .addOnFailureListener {

                Toast.makeText(
                    requireContext(),
                    "Gagal memperbarui status",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}
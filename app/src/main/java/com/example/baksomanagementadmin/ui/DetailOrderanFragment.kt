package com.example.baksomanagementadmin.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.baksomanagementadmin.R
import com.example.baksomanagementadmin.data.model.OrderItem
import com.example.baksomanagementadmin.data.remote.FirebaseClient

class DetailOrderanFragment : Fragment() {

    private val firestore = FirebaseClient.firestore

    private var orderId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        orderId = arguments?.getString("ORDER_ID") ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_detail_orderan, container, false)
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvUser = view.findViewById<TextView>(R.id.tvUser)

        val rvItems = view.findViewById<RecyclerView>(R.id.rvItems)

        val btnSelesai = view.findViewById<Button>(R.id.btnSelesai)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)

        val spStatus = view.findViewById<Spinner>(R.id.spStatus)

        rvItems.layoutManager = LinearLayoutManager(requireContext())

        val statusList = listOf(
            "pending",
            "diproses",
            "selesai",
            "dibatalkan"
        )

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            statusList
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spStatus.adapter = adapter

        firestore.collection("orders")
            .document(orderId)
            .get()
            .addOnSuccessListener { orderDoc ->

                val userId =
                    orderDoc.getString("userID") ?: ""

                val status =
                    orderDoc.getString("status") ?: "pending"

                tvTitle.text = "Orderan #$orderId"

                tvUser.text = "User ID: $userId"

                val pos = statusList.indexOf(status)

                if (pos >= 0) {
                    spStatus.setSelection(pos)
                }

                firestore.collection("orders")
                    .document(orderId)
                    .collection("items")
                    .get()
                    .addOnSuccessListener { result ->

                        val items = mutableListOf<OrderItem>()

                        result.documents.forEach { doc ->

                            val item =
                                doc.toObject(OrderItem::class.java)

                            if (item != null) {
                                items.add(item)
                            }
                        }

                        rvItems.adapter =
                            AdminDetailItemAdapter(items)
                    }
            }

        btnSelesai.setOnClickListener {
            updateStatus("selesai")
        }

        btnCancel.setOnClickListener {
            updateStatus("dibatalkan")
        }
    }

    private fun updateStatus(status: String) {

        val message =
            if (status == "selesai") {
                "Yakin ingin menyelesaikan orderan ini?"
            } else {
                "Yakin ingin membatalkan orderan ini?"
            }

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Konfirmasi")
            .setMessage(message)

            .setPositiveButton("Ya") { _, _ ->

                firestore.collection("orders")
                    .document(orderId)
                    .update("status", status)

                    .addOnSuccessListener {

                        Toast.makeText(
                            requireContext(),
                            "Status berhasil diupdate",
                            Toast.LENGTH_SHORT
                        ).show()

                        // kembali ke halaman notification
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }

                    .addOnFailureListener {

                        Toast.makeText(
                            requireContext(),
                            "Gagal update status",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }

            .setNegativeButton("Tidak", null)
            .show()
    }
}
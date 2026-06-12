package com.example.baksomanagementadmin.ui

import android.os.Bundle
import android.util.Log
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
import com.example.baksomanagementadmin.data.repository.OrderRepository
import com.example.baksomanagementadmin.data.repository.UserRepository

class DetailOrderanFragment :
    Fragment(R.layout.fragment_detail_orderan) {

    private val repository =
        OrderRepository()

    private val userRepository =
        UserRepository()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        val orderId =
            arguments?.getString(
                "ORDER_ID"
            ) ?: return

        val tvTitle =
            view.findViewById<TextView>(
                R.id.tvTitle
            )

        val tvNamaUser =
            view.findViewById<TextView>(
                R.id.tvNamaUser
            )

        val rvItems =
            view.findViewById<RecyclerView>(
                R.id.rvItems
            )

        val spStatus =
            view.findViewById<Spinner>(
                R.id.spStatus
            )

        val btnUpdate =
            view.findViewById<Button>(
                R.id.btnUpdateStatus
            )

        val btnSiapDiambil =
            view.findViewById<Button>(
                R.id.btnSiapDiambil
            )

        val btnCancel =
            view.findViewById<Button>(
                R.id.btnCancel
            )

        rvItems.layoutManager =
            LinearLayoutManager(
                requireContext()
            )

        val statusList =
            listOf(
                "pending",
                "diproses"
            )

        spStatus.adapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                statusList
            )

        repository.getOrderDetail(
            orderId
        ){ order, items ->

            if(order == null) return@getOrderDetail

            tvTitle.text =
                "Order #${order.id.take(8)}"

            rvItems.adapter =
                DetailOrderItemAdapter(
                    items
                )

            userRepository.getUserById(
                order.userID
            ){ user ->

                tvNamaUser.text =
                    buildString {

                        append("Nama : ")
                        append(user?.nama)
                        append("\n")
                        append("Email : ")
                        append(user?.email)
                        append("\n")
                        append("No HP : ")
                        append(user?.noTelp)
                    }
            }

            val index =
                statusList.indexOf(
                    order.status
                )

            if(index >= 0)
                spStatus.setSelection(index)
        }

        btnUpdate.setOnClickListener {

            repository.updateOrderStatus(
                orderId,
                spStatus.selectedItem.toString()
            ){
                Toast.makeText(
                    requireContext(),
                    "Status diperbarui",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        btnSiapDiambil.setOnClickListener {

            repository.updateOrderStatus(
                orderId,
                "siap_diambil"
            ){

                Toast.makeText(
                    requireContext(),
                    "Pesanan siap diambil",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        btnCancel.setOnClickListener {

            AlertDialog.Builder(requireContext())
                .setTitle("Batalkan Pesanan")
                .setMessage(
                    "Yakin ingin membatalkan pesanan ini?"
                )
                .setPositiveButton("Ya") { _, _ ->

                    repository.cancelOrder(
                        orderId
                    ) {

                        Toast.makeText(
                            requireContext(),
                            "Pesanan dibatalkan",
                            Toast.LENGTH_SHORT
                        ).show()

                        requireActivity()
                            .onBackPressedDispatcher
                            .onBackPressed()
                    }
                }
                .setNegativeButton(
                    "Tidak",
                    null
                )
                .show()
        }
    }
}
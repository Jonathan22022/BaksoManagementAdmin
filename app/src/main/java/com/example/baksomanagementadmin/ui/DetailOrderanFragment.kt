package com.example.baksomanagementadmin.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.baksomanagementadmin.R
import com.example.baksomanagementadmin.data.remote.FirebaseClient

class DetailOrderanFragment : Fragment() {

    private val firestore = FirebaseClient.firestore
    private var orderId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        orderId = arguments?.getString("ORDER_ID") ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_detail_orderan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvUser = view.findViewById<TextView>(R.id.tvUser)
        val tvMenu = view.findViewById<TextView>(R.id.tvMenu)
        val tvDesc = view.findViewById<TextView>(R.id.tvDesc)
        val tvAddon = view.findViewById<TextView>(R.id.tvAddon)
        val tvQty = view.findViewById<TextView>(R.id.tvQty)
        val tvLocation = view.findViewById<TextView>(R.id.tvLocation)
        val btnSelesai = view.findViewById<Button>(R.id.btnSelesai)
        val spStatus = view.findViewById<Spinner>(R.id.spStatus)

        val statusList = listOf("pending", "diproses", "selesai")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statusList)
        spStatus.adapter = adapter

        loadDetail(
            tvTitle, tvUser, tvMenu, tvDesc,
            tvAddon, tvQty, tvLocation, spStatus
        )

        btnSelesai.setOnClickListener {
            updateStatus("selesai")
        }
    }

    private fun loadDetail(
        tvTitle: TextView,
        tvUser: TextView,
        tvMenu: TextView,
        tvDesc: TextView,
        tvAddon: TextView,
        tvQty: TextView,
        tvLocation: TextView,
        spStatus: Spinner
    ) {

        firestore.collection("orders").document(orderId)
            .get()
            .addOnSuccessListener { orderDoc ->

                val user = orderDoc.getString("userID") ?: ""
                val status = orderDoc.getString("status") ?: "pending"

                tvTitle.text = "Orderan #$orderId"
                tvUser.text = "Dipesan oleh: $user"
                tvLocation.text = "UMN (dummy lokasi)" // bisa kamu ambil dari DB nanti

                // set spinner
                val pos = (spStatus.adapter as ArrayAdapter<String>).getPosition(status)
                spStatus.setSelection(pos)

                // ambil item
                firestore.collection("orders")
                    .document(orderId)
                    .collection("items")
                    .get()
                    .addOnSuccessListener { items ->

                        val item = items.documents.firstOrNull() ?: return@addOnSuccessListener

                        val nama = item.getString("nama") ?: ""
                        val qty = item.getLong("quantity") ?: 0
                        val desc = item.getString("catatan") ?: ""

                        val addons = item.get("addons") as? List<Map<String, Any>> ?: emptyList()

                        val addonText = if (addons.isEmpty()) {
                            "No add-on"
                        } else {
                            addons.joinToString(", ") { it["name"].toString() }
                        }

                        tvMenu.text = "Nama Menu: $nama"
                        tvDesc.text = desc
                        tvAddon.text = addonText
                        tvQty.text = "Jumlah: $qty"
                    }
            }
    }

    private fun updateStatus(newStatus: String) {
        firestore.collection("orders").document(orderId)
            .update("status", newStatus)
    }
}
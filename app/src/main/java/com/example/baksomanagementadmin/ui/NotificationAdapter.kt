package com.example.baksomanagementadmin.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.baksomanagementadmin.R
import com.example.baksomanagementadmin.data.model.AdminOrderItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.util.Log

class NotificationAdapter(
    private val list: List<AdminOrderItem>,
    private val onClick: (AdminOrderItem) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvId: TextView = view.findViewById(R.id.tvId)
        val tvNama: TextView = view.findViewById(R.id.tvNama)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvAddon: TextView = view.findViewById(R.id.tvAddon)
        val tvQty: TextView = view.findViewById(R.id.tvQty)
        val tvTotal: TextView = view.findViewById(R.id.tvTotal)
        val btnDetail: Button = view.findViewById(R.id.btnOrderDetail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_notification, parent, false)
        Log.e("NotificationAdapter", "onCreateViewHolder called")
        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = list[position]

        holder.tvId.text = item.userID
        holder.tvNama.text = item.nama

        // format tanggal
        val sdf = java.text.SimpleDateFormat("dd MMM yyyy, HH:mm", java.util.Locale.getDefault())
        holder.tvDate.text = sdf.format(java.util.Date(item.createdAt))

        holder.tvQty.text = "Jumlah: ${item.quantity}"
        holder.tvTotal.text = "Rp ${item.total}"

        holder.tvAddon.text =
            if (item.addons.isEmpty()) "No add-on"
            else item.addons.joinToString(", ") { it.name }

        Log.e("NotificationAdapter", "onBindViewHolder called")
        Log.e("NotificationAdapter", "item: $item")
        Log.e("NotificationAdapter", "holder: $holder")
        Log.e("NotificationAdapter", "position: $position")
        Log.e("NotificationAdapter", "list size: ${list.size}")
        Log.e("NotificationAdapter", "list: $list")
        holder.btnDetail.setOnClickListener {
            onClick(item)
        }
    }
}
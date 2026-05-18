package com.example.baksomanagementadmin.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.baksomanagementadmin.R
import com.example.baksomanagementadmin.data.model.OrderItem


class AdminDetailItemAdapter(
    private val list: List<OrderItem>
) : RecyclerView.Adapter<AdminDetailItemAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val imgMenu: ImageView = view.findViewById(R.id.imgMenu)
        val tvMenu: TextView = view.findViewById(R.id.tvMenu)
        val tvDesc: TextView = view.findViewById(R.id.tvDesc)
        val tvAddon: TextView = view.findViewById(R.id.tvAddon)
        val tvQty: TextView = view.findViewById(R.id.tvQty)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_detail_order_admin, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = list[position]

        Glide.with(holder.imgMenu.context)
            .load(item.imageUrl)
            .into(holder.imgMenu)

        holder.tvMenu.text = item.nama
        holder.tvDesc.text = item.catatan

        holder.tvQty.text = "Jumlah: ${item.quantity}"

        holder.tvAddon.text =
            if (item.addons.isEmpty()) {
                "No add-on"
            } else {
                item.addons.joinToString(", ") { it.name }
            }
    }
}
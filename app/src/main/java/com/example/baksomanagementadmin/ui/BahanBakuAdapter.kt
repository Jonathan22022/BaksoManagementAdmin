package com.example.baksomanagementadmin.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.baksomanagementadmin.R
import com.example.baksomanagementadmin.data.model.BahanBaku

class BahanBakuAdapter(
    private val list: List<BahanBaku>,
    private val onEdit: (BahanBaku) -> Unit,
    private val onDelete: (BahanBaku) -> Unit
) : RecyclerView.Adapter<BahanBakuAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView = view.findViewById(R.id.imgBahanBaku)
        val nama: TextView = view.findViewById(R.id.tvBahanBakuName)
        val harga: TextView = view.findViewById(R.id.tvBahanBakuHarga)
        val btnEdit: ImageView = view.findViewById(R.id.btnEdit)
        val btnDelete: ImageView = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bahanbaku, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.nama.text = item.nama
        holder.harga.text = "Rp ${item.harga}"

        Glide.with(holder.itemView.context)
            .load(item.gambarUrl)
            .into(holder.img)

        holder.btnEdit.setOnClickListener {
            onEdit(item)
        }

        holder.btnDelete.setOnClickListener {
            onDelete(item)
        }
    }
}
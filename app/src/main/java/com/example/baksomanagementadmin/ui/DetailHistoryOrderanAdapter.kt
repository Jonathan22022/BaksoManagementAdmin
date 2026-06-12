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

class DetailHistoryOrderanAdapter(
    private val list: List<OrderItem>
) : RecyclerView.Adapter<DetailHistoryOrderanAdapter.ViewHolder>() {

    class ViewHolder(
        view: View
    ) : RecyclerView.ViewHolder(view) {

        val imgMenu: ImageView =
            view.findViewById(R.id.imgMenu)

        val tvMenu: TextView =
            view.findViewById(R.id.tvMenu)

        val tvDesc: TextView =
            view.findViewById(R.id.tvDesc)

        val tvAddon: TextView =
            view.findViewById(R.id.tvAddon)

        val tvQty: TextView =
            view.findViewById(R.id.tvQty)

        val tvHarga: TextView =
            view.findViewById(R.id.tvHarga)

        val tvSubtotal: TextView =
            view.findViewById(R.id.tvSubtotal)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_detail_order_admin,
                    parent,
                    false
                )
        )
    }

    override fun getItemCount(): Int =
        list.size

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val item = list[position]

        holder.tvMenu.text =
            item.nama

        holder.tvDesc.text =
            if (item.catatan.isBlank())
                "Catatan : -"
            else
                "Catatan : ${item.catatan}"

        holder.tvAddon.text =
            if (item.addons.isEmpty())
                "Addon : -"
            else
                "Addon : ${
                    item.addons.joinToString {
                        it.name
                    }
                }"

        holder.tvQty.text =
            "Jumlah : ${item.quantity}"

        holder.tvHarga.text =
            "Harga : Rp ${item.harga}"

        val addonTotal =
            item.addons.sumOf { it.price }

        val subtotal =
            (item.harga + addonTotal) *
                    item.quantity

        holder.tvSubtotal.text =
            "Subtotal : Rp $subtotal"

        Glide.with(holder.itemView.context)
            .load(item.imageUrl)
            .placeholder(R.drawable.bakso)
            .error(R.drawable.bakso)
            .into(holder.imgMenu)
    }
}
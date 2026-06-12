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

class DetailOrderItemAdapter(
    private val items: List<OrderItem>
) : RecyclerView.Adapter<DetailOrderItemAdapter.ViewHolder>() {

    class ViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

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

        val view = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.item_detail_order_admin,
                parent,
                false
            )

        return ViewHolder(view)
    }

    override fun getItemCount(): Int =
        items.size

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val item = items[position]

        val addonPrice =
            item.addons.sumOf { it.price }

        val subtotal =
            (item.harga + addonPrice) *
                    item.quantity

        holder.tvHarga.text =
            "Harga : Rp ${item.harga} (+Rp $addonPrice addon)"

        holder.tvSubtotal.text =
            "Subtotal : Rp $subtotal"

        holder.tvMenu.text =
            item.nama

        holder.tvDesc.text =
            if (item.catatan.isBlank())
                "Catatan : -"
            else
                "Catatan : ${item.catatan}"

        holder.tvQty.text =
            "Jumlah : ${item.quantity}"

        holder.tvAddon.text =
            if (item.addons.isEmpty()) {

                "Addon : Tidak ada"

            } else {

                "Addon : " +
                        item.addons.joinToString(", ") {
                            "${it.name} (+Rp ${it.price})"
                        }
            }

        Glide.with(holder.itemView.context)
            .load(item.imageUrl)
            .placeholder(R.drawable.bakso)
            .error(R.drawable.bakso)
            .into(holder.imgMenu)
    }
}
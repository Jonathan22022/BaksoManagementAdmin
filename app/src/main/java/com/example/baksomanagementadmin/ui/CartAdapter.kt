package com.example.baksomanagementadmin.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.baksomanagementadmin.R
import com.example.baksomanagementadmin.data.model.Cart

class CartAdapter(private val cartList: List<Cart>) :
    RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val img = itemView.findViewById<ImageView>(R.id.imgCart)
        val name = itemView.findViewById<TextView>(R.id.tvName)
        val desc = itemView.findViewById<TextView>(R.id.tvDesc)
        val addons = itemView.findViewById<TextView>(R.id.tvAddons)
        val extra = itemView.findViewById<TextView>(R.id.tvExtra)
        val total = itemView.findViewById<TextView>(R.id.tvTotal)
        val btnOrder = itemView.findViewById<Button>(R.id.btnOrder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int = cartList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val cart = cartList[position]

        holder.name.text = cart.name
        holder.desc.text = cart.desc
        holder.addons.text = cart.addons
        holder.extra.text = cart.extra
        holder.total.text = "Total : Rp. ${cart.total}"
        holder.img.setImageResource(cart.image)

        holder.btnOrder.setOnClickListener {

            Toast.makeText(
                holder.itemView.context,
                "Lanjut ke pembayaran",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
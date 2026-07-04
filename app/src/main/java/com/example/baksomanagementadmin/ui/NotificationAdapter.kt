package com.example.baksomanagementadmin.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.baksomanagementadmin.R
import com.example.baksomanagementadmin.data.model.AdminOrderItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificationAdapter(
    private var list: List<AdminOrderItem>,
    private val onClick:(AdminOrderItem)->Unit
)
    : RecyclerView.Adapter<
        NotificationAdapter.ViewHolder>() {

    class ViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView){

        val img =
            itemView.findViewById<ImageView>(
                R.id.imgMenu
            )

        val tvId =
            itemView.findViewById<TextView>(
                R.id.tvId
            )

        val tvNama =
            itemView.findViewById<TextView>(
                R.id.tvNama
            )

        val tvNamaUser =
            itemView.findViewById<TextView>(
                R.id.tvNamaUser
            )

        val tvDate =
            itemView.findViewById<TextView>(
                R.id.tvDate
            )

        val tvAddon =
            itemView.findViewById<TextView>(
                R.id.tvAddon
            )

        val tvQty =
            itemView.findViewById<TextView>(
                R.id.tvQty
            )

        val tvTotal =
            itemView.findViewById<TextView>(
                R.id.tvTotal
            )

        val tvOther =
            itemView.findViewById<TextView>(
                R.id.tvOther
            )

        val tvPickupType =
            itemView.findViewById<TextView>(
                R.id.tvPickupType
            )

        val btnDetail =
            itemView.findViewById<Button>(
                R.id.btnOrderDetail
            )
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_order_notification,
                    parent,
                    false
                )
        )
    }

    override fun getItemCount() =
        list.size

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val item = list[position]

        holder.tvId.text =
            item.orderId.take(8)

        holder.tvNama.text =
            item.nama

        holder.tvNamaUser.text =
            item.namaUser

        holder.tvQty.text =
            "Jumlah : ${item.quantity}"

        holder.tvTotal.text =
            "Rp ${item.total}"

        holder.tvPickupType.text =
            when (item.pickupType) {
                "delivery" -> "Delivery"
                else -> "Dine In"
            }

        holder.tvAddon.text =
            item.addons.joinToString {
                it.name
            }

        val date =
            SimpleDateFormat(
                "dd MMM yyyy HH:mm",
                Locale("id")
            )
                .format(
                    Date(item.createdAt)
                )

        holder.tvDate.text = date

        if(item.itemCount > 1){

            holder.tvOther.visibility =
                View.VISIBLE

            holder.tvOther.text =
                "dan ${item.itemCount - 1} menu lainnya"
        }else{

            holder.tvOther.visibility =
                View.GONE
        }

        Glide.with(holder.itemView)
            .load(item.imageUrl)
            .into(holder.img)

        holder.btnDetail.setOnClickListener {

            onClick(item)
        }
    }

    fun submitList(
        newList: List<AdminOrderItem>
    ) {
        list = newList
        notifyDataSetChanged()
    }
}
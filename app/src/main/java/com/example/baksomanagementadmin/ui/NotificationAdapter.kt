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
    private val list: List<AdminOrderItem>,
    private val onClick: (AdminOrderItem) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    companion object {
        private const val TAG = "NotificationAdapter"
    }

    init {
        Log.d(TAG, "Adapter initialized")
        Log.d(TAG, "Total items received = ${list.size}")
    }

    inner class ViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        val tvId: TextView = view.findViewById(R.id.tvId)
        val imgMenu: ImageView = view.findViewById(R.id.imgMenu)
        val tvNama: TextView = view.findViewById(R.id.tvNama)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvAddon: TextView = view.findViewById(R.id.tvAddon)
        val tvQty: TextView = view.findViewById(R.id.tvQty)
        val tvTotal: TextView = view.findViewById(R.id.tvTotal)
        val btnDetail: Button = view.findViewById(R.id.btnOrderDetail)

        init {
            Log.d(TAG, "ViewHolder created")
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        Log.d(TAG, "onCreateViewHolder() called")

        val view = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.item_order_notification,
                parent,
                false
            )

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {

        Log.d(TAG, "getItemCount() = ${list.size}")

        return list.size
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val item = list[position]

        Log.d(
            TAG,
            """
            ========= BIND ITEM =========
            Position    : $position
            Order ID    : ${item.orderId}
            User ID     : ${item.userID}
            Nama Menu   : ${item.nama}
            Quantity    : ${item.quantity}
            Total       : ${item.total}
            Status      : ${item.status}
            Created At  : ${item.createdAt}
            Image URL   : ${item.imageUrl}
            Addons Size : ${item.addons.size}
            ============================
            """.trimIndent()
        )

        holder.tvId.text = item.userID
        holder.tvNama.text = item.nama

        val sdf = SimpleDateFormat(
            "EEEE, dd MMMM yyyy, HH:mm",
            Locale("id", "ID")
        )

        holder.tvDate.text =
            sdf.format(Date(item.createdAt))

        holder.tvQty.text =
            "Jumlah: ${item.quantity}"

        holder.tvTotal.text =
            "Rp ${item.total}"

        val addonText =
            if (item.addons.isEmpty()) {
                "No add-on"
            } else {
                item.addons.joinToString(", ") {
                    it.name
                }
            }

        holder.tvAddon.text = addonText

        Log.d(
            TAG,
            "Addon Text = $addonText"
        )

        if (item.imageUrl.isBlank()) {

            Log.w(
                TAG,
                "Image URL kosong untuk orderId=${item.orderId}"
            )

        } else {

            Log.d(
                TAG,
                "Loading image from: ${item.imageUrl}"
            )
        }

        Glide.with(holder.imgMenu.context)
            .load(item.imageUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(holder.imgMenu)

        holder.btnDetail.setOnClickListener {

            Log.d(
                TAG,
                """
                DETAIL BUTTON CLICKED
                Order ID : ${item.orderId}
                User ID  : ${item.userID}
                Nama     : ${item.nama}
                """.trimIndent()
            )

            onClick(item)
        }

        holder.itemView.setOnClickListener {

            Log.d(
                TAG,
                """
                ITEM CLICKED
                Position : $position
                Order ID : ${item.orderId}
                """.trimIndent()
            )
        }
    }

    override fun onViewRecycled(
        holder: ViewHolder
    ) {

        super.onViewRecycled(holder)

        Log.d(
            TAG,
            "View recycled at adapterPosition=${holder.adapterPosition}"
        )
    }

    override fun onAttachedToRecyclerView(
        recyclerView: RecyclerView
    ) {

        super.onAttachedToRecyclerView(recyclerView)

        Log.d(
            TAG,
            "Adapter attached to RecyclerView"
        )
    }

    override fun onDetachedFromRecyclerView(
        recyclerView: RecyclerView
    ) {

        super.onDetachedFromRecyclerView(recyclerView)

        Log.d(
            TAG,
            "Adapter detached from RecyclerView"
        )
    }
}
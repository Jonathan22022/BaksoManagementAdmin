package com.example.baksomanagementadmin.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.baksomanagementadmin.R
import com.example.baksomanagementadmin.data.model.HistoryOrder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryOrderAdapter(
    private val list: MutableList<HistoryOrder>,
    private val onCheckedChanged:(Boolean)->Unit,
    private val onDetailClick:(HistoryOrder)->Unit
) : RecyclerView.Adapter<HistoryOrderAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val imgMenu: ImageView =
            view.findViewById(R.id.imgMenu)

        val tvNama: TextView =
            view.findViewById(R.id.tvNama)

        val tvDate: TextView =
            view.findViewById(R.id.tvDate)

        val tvQty: TextView =
            view.findViewById(R.id.tvQty)

        val tvStatus: TextView =
            view.findViewById(R.id.tvStatus)

        val tvTotal: TextView =
            view.findViewById(R.id.tvTotal)

        val cbSelect =
            view.findViewById<CheckBox>(
                R.id.cbSelect
            )

        val btnDetail =
            view.findViewById<Button>(
                R.id.btnDetail
            )
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_history_order,
                parent,
                false
            )

        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val item = list[position]

        holder.tvNama.text = item.nama

        holder.tvQty.text =
            "Jumlah: ${item.quantity}"

        holder.tvStatus.text =
            item.status.replaceFirstChar { it.uppercase() }

        holder.tvTotal.text =
            "Rp ${item.total}"

        val sdf = SimpleDateFormat(
            "dd MMM yyyy HH:mm",
            Locale("id", "ID")
        )

        holder.tvDate.text =
            sdf.format(Date(item.createdAt))

        if (item.status == "selesai") {
            holder.tvStatus.setTextColor(
                android.graphics.Color.parseColor("#2E7D32")
            )
        } else {
            holder.tvStatus.setTextColor(
                android.graphics.Color.parseColor("#D32F2F")
            )
        }

        Glide.with(holder.imgMenu.context)
            .load(item.imageUrl)
            .placeholder(R.drawable.bakso)
            .error(R.drawable.bakso)
            .into(holder.imgMenu)

        holder.cbSelect.setOnCheckedChangeListener(
            null
        )

        holder.cbSelect.isChecked =
            item.selected

        holder.cbSelect.setOnCheckedChangeListener { _, checked ->

            item.selected = checked

            onCheckedChanged(
                list.any { it.selected }
            )
        }

        holder.btnDetail.setOnClickListener {

            onDetailClick(item)
        }
    }

    fun updateData(
        newList: MutableList<HistoryOrder>
    ) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }
}
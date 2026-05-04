package com.example.baksomanagementadmin.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.baksomanagementadmin.data.model.History
import android.widget.ImageView
import android.widget.TextView
import com.example.baksomanagementadmin.R

class HistoryAdapter(private val list: List<History>) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val img = view.findViewById<ImageView>(R.id.imgMenu)
        val name = view.findViewById<TextView>(R.id.tvMenuName)
        val date = view.findViewById<TextView>(R.id.tvDate)
        val desc = view.findViewById<TextView>(R.id.tvDesc)
        val price = view.findViewById<TextView>(R.id.tvPrice)
        val btn = view.findViewById<Button>(R.id.btnOrderAgain)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history,parent,false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.name.text = item.name
        holder.date.text = item.date
        holder.desc.text = item.desc
        holder.price.text = item.price
        holder.img.setImageResource(item.image)
    }
}
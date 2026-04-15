package com.example.baksomanagementadmin.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.baksomanagementadmin.R
import com.example.baksomanagementadmin.data.model.Menu
import android.util.Log

class MenuAdapter(
    private val menuList: List<Menu>,
    private val onItemClick: (Menu) -> Unit
) : RecyclerView.Adapter<MenuAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.imgMenu)
        val name: TextView = itemView.findViewById(R.id.tvMenuName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_menu, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int = menuList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val menu = menuList[position]

        holder.name.text = menu.namaMenu

        Glide.with(holder.itemView.context)
            .load(menu.gambarUrl)
            .into(holder.image)

        holder.itemView.setOnClickListener {
            onItemClick(menu)
        }
    }
}
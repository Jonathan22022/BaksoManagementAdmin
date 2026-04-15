package com.example.baksomanagementadmin.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.baksomanagementadmin.R
import com.example.baksomanagementadmin.data.model.Favourite

class FavouriteAdapter(private val list: List<Favourite>) :
    RecyclerView.Adapter<FavouriteAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val img = view.findViewById<ImageView>(R.id.imgMenu)
        val name = view.findViewById<TextView>(R.id.tvMenuName)
        val star = view.findViewById<ImageView>(R.id.imgFav)
        val btn = view.findViewById<Button>(R.id.btnMenu)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favourite,parent,false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = list[position]

        holder.name.text = item.name
        holder.img.setImageResource(item.image)

    }
}
package com.example.baksomanagementadmin.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.baksomanagementadmin.R
import com.example.baksomanagementadmin.data.model.AddOn
import android.util.Log

class AddOnAdapter(
    private val addOnList: List<AddOn>,
    private val onEditClick: (AddOn) -> Unit,
    private val onDeleteClick: (AddOn) -> Unit
) : RecyclerView.Adapter<AddOnAdapter.ViewHolder>() {

    private val TAG = "AddOnAdapter"

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.imgAddOn)
        val name: TextView = itemView.findViewById(R.id.tvAddOnName)
        val price: TextView = itemView.findViewById(R.id.tvAddOnHarga)
        val btnEdit: ImageView = itemView.findViewById(R.id.btnEdit)
        val btnDelete: ImageView = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d(TAG, "onCreateViewHolder called")

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_addon, parent, false)

        Log.d(TAG, "ViewHolder created")

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        val size = addOnList.size
        Log.d(TAG, "getItemCount: $size")
        return size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val addOn = addOnList[position]

        Log.d(TAG, "onBindViewHolder position: $position, id: ${addOn.id}, nama: ${addOn.name}")

        holder.name.text = addOn.name
        holder.price.text = "Rp. ${addOn.price}"

        if (addOn.gambarUrl.isNullOrEmpty()) {
            Log.w(TAG, "Image URL kosong untuk menu id: ${addOn.id}")
        }

        Glide.with(holder.itemView.context)
            .load(addOn.gambarUrl)
            .into(holder.image)

        holder.btnEdit.setOnClickListener {
            Log.d(TAG, "Edit clicked: ${addOn.id}")
            onEditClick(addOn)
        }

        holder.btnDelete.setOnClickListener {
            Log.d(TAG, "Delete clicked: ${addOn.id}")
            onDeleteClick(addOn)
        }
    }
}
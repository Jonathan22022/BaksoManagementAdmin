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
import com.example.baksomanagementadmin.data.model.BahanBaku
import com.example.baksomanagementadmin.service.MenuStokChecker

class MenuAdapter(
    private val menuList: List<Menu>,
    private val bahanList: List<BahanBaku>,
    private val onEditClick: (Menu) -> Unit,
    private val onDeleteClick: (Menu) -> Unit
) : RecyclerView.Adapter<MenuAdapter.ViewHolder>() {

    private val TAG = "MenuAdapter"

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.imgMenu)
        val name: TextView = itemView.findViewById(R.id.tvMenuName)
        val desc: TextView = itemView.findViewById(R.id.tvMenuDesc)
        val price: TextView = itemView.findViewById(R.id.tvMenuHarga)
        val btnEdit: ImageView = itemView.findViewById(R.id.btnEdit)
        val btnDelete: ImageView = itemView.findViewById(R.id.btnDelete)
        val tvStockStatus = itemView.findViewById<TextView>(R.id.tvStockStatus)
        val tvBahanDibutuhkan: TextView = itemView.findViewById(R.id.tvBahanDibutuhkan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d(TAG, "onCreateViewHolder called")

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_menu_admin, parent, false)

        Log.d(TAG, "ViewHolder created")

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        val size = menuList.size
        Log.d(TAG, "getItemCount: $size")
        return size
    }

    private fun formatJumlah(
        jumlah: Double,
        satuan: String
    ): String {

        return if (jumlah % 1.0 == 0.0) {
            "${jumlah.toInt()} $satuan"
        } else {
            "$jumlah $satuan"
        }
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val menu = menuList[position]
        val kekuranganList =
            MenuStokChecker.checkMenuStock(
                menu,
                bahanList
            )
        Log.d(TAG, "onBindViewHolder position: $position, id: ${menu.id}, nama: ${menu.namaMenu}")

        holder.name.text = menu.namaMenu

        holder.desc.text = menu.description

        holder.price.text =
            "Rp %,d".format(menu.harga)
                .replace(',', '.')

        if (menu.gambarUrl.isNullOrEmpty()) {
            Log.w(TAG, "Image URL kosong untuk menu id: ${menu.id}")
        }

        Glide.with(holder.itemView.context)
            .load(menu.gambarUrl)
            .into(holder.image)

        if (kekuranganList.isEmpty()) {

            holder.tvStockStatus.text =
                "✅ Stok tersedia"

        } else {

            holder.tvStockStatus.text =
                kekuranganList.joinToString("\n") {

                    "❌ ${it.namaBahan} kurang ${
                        formatJumlah(
                            it.kekurangan,
                            it.satuan
                        )
                    }"
                }
        }

        holder.tvBahanDibutuhkan.text =
            menu.bahanList.joinToString("\n") { bahanItem ->

                val bahan =
                    bahanList.find {
                        it.id == bahanItem.bahanId
                    }

                if (bahan != null) {
                    "${formatJumlah(
                        bahanItem.jumlah,
                        bahan.satuan
                    )} ${bahan.nama}"
                } else {
                    "Bahan tidak ditemukan"
                }
            }

        holder.btnEdit.setOnClickListener {
            Log.d(TAG, "Edit clicked: ${menu.id}")
            onEditClick(menu)
        }

        holder.btnDelete.setOnClickListener {
            Log.d(TAG, "Delete clicked: ${menu.id}")
            onDeleteClick(menu)
        }
    }
}
package com.example.baksomanagementadmin.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.baksomanagementadmin.R
import com.example.baksomanagementadmin.data.model.RiwayatPembelianBahan
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RiwayatPembelianAdapter(
    private val list: List<RiwayatPembelianBahan>
) : RecyclerView.Adapter<RiwayatPembelianAdapter.ViewHolder>() {

    companion object {
        private const val TAG = "RiwayatPembelian"
    }

    init {
        Log.d(TAG, "Adapter dibuat, jumlah data: ${list.size}")
    }

    inner class ViewHolder(view: View)
        : RecyclerView.ViewHolder(view) {

        val imgBahan: ImageView =
            view.findViewById(R.id.imgBahan)

        val tvNama: TextView =
            view.findViewById(R.id.tvNama)

        val tvJumlah: TextView =
            view.findViewById(R.id.tvJumlah)

        val tvHarga: TextView =
            view.findViewById(R.id.tvHarga)

        val tvTanggal: TextView =
            view.findViewById(R.id.tvTanggal)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        Log.d(TAG, "onCreateViewHolder dipanggil")

        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_riwayat_pembelian,
                    parent,
                    false
                )
        )
    }

    override fun getItemCount(): Int {
        Log.d(TAG, "getItemCount: ${list.size}")
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
            Binding item posisi=$position
            nama=${item.namaBahan}
            jumlah=${item.jumlah}
            harga=${item.hargaBeli}
            createdAt=${item.createdAt}
            gambarUrl=${item.gambarUrl}
            """.trimIndent()
        )

        holder.tvNama.text = item.namaBahan
        holder.tvJumlah.text = "Tambah ${item.jumlah} kg"
        holder.tvHarga.text = "Rp ${item.hargaBeli}"

        holder.tvTanggal.text =
            SimpleDateFormat(
                "dd/MM/yyyy",
                Locale("id", "ID")
            ).format(
                Date(item.createdAt)
            )

        Glide.with(holder.itemView.context)
            .load(item.gambarUrl)
            .into(holder.imgBahan)

        Log.d(TAG, "Glide load image: ${item.gambarUrl}")
    }
}
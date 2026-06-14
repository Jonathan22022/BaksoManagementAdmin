package com.example.baksomanagementadmin.ui.addon

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.baksomanagementadmin.R
import com.example.baksomanagementadmin.data.model.BahanBaku
import com.example.baksomanagementadmin.data.model.BahanItem
class BahanAddOnAdapter(
    private val list: List<BahanBaku>,
    selectedList: List<BahanItem> = emptyList()
) : RecyclerView.Adapter<BahanAddOnAdapter.ViewHolder>() {
    private val selectedMap =
        mutableMapOf<String, Double>()

    init {

        selectedList.forEach {

            selectedMap[it.bahanId] = it.jumlah
        }
    }

    inner class ViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        val cbBahan: CheckBox =
            view.findViewById(R.id.cbBahan)
        val tvNama: TextView =
            view.findViewById(R.id.tvNamaBahan)
        val etJumlah: EditText =
            view.findViewById(R.id.etJumlah)
        val tvSatuan: TextView =
            view.findViewById(R.id.tvSatuan)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_bahan_addon,
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
        holder.tvSatuan.text = "/ ${item.satuan} (stok ${item.berat})"

        val selectedJumlah = selectedMap[item.id]


        holder.cbBahan.setOnCheckedChangeListener(null)

        val isSelected = selectedJumlah != null

        holder.cbBahan.isChecked = isSelected
        holder.etJumlah.isEnabled = isSelected

        if (selectedJumlah != null) {
            holder.etJumlah.setText(selectedJumlah.toString())
        } else {
            holder.etJumlah.setText("")
        }

        holder.cbBahan.setOnCheckedChangeListener { _, isChecked ->

            holder.etJumlah.isEnabled = isChecked

            if (isChecked) {

                val jumlah =
                    holder.etJumlah.text.toString()
                        .toDoubleOrNull() ?: 0.0

                selectedMap[item.id] = jumlah

            } else {

                selectedMap.remove(item.id)
                holder.etJumlah.setText("")
            }
        }

        holder.etJumlah.addTextChangedListener(
            object : android.text.TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {}

                override fun afterTextChanged(
                    s: android.text.Editable?
                ) {

                    if (holder.cbBahan.isChecked) {

                        val jumlah =
                            s.toString()
                                .toDoubleOrNull() ?: 0.0

                        selectedMap[item.id] = jumlah
                    }
                }
            }
        )
    }

    fun getSelectedBahan(): List<BahanItem> {

        return selectedMap.map {

            BahanItem(
                bahanId = it.key,
                jumlah = it.value
            )
        }
    }
}
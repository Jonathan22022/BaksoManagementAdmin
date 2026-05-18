package com.example.baksomanagementadmin.ui

import android.text.Editable
import android.text.TextWatcher
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

class BahanMenuAdapter(
    private val list: List<BahanBaku>,
    oldSelected: List<BahanItem> = emptyList()
) : RecyclerView.Adapter<BahanMenuAdapter.ViewHolder>() {

    private val selectedBahan =
        mutableMapOf<String, Double>()

    init {

        oldSelected.forEach {
            selectedBahan[it.bahanId] = it.jumlah
        }
    }

    inner class ViewHolder(view: View)
        : RecyclerView.ViewHolder(view) {

        val cbBahan: CheckBox =
            view.findViewById(R.id.cbBahan)

        val tvNama: TextView =
            view.findViewById(R.id.tvNamaBahan)

        val etJumlah: EditText =
            view.findViewById(R.id.etJumlah)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_bahan_menu,
                parent,
                false
            )

        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val item = list[position]

        holder.tvNama.text = item.nama

        val isSelected =
            selectedBahan.containsKey(item.id)

        holder.cbBahan.setOnCheckedChangeListener(null)

        holder.cbBahan.isChecked = isSelected

        holder.etJumlah.isEnabled = isSelected

        if (isSelected) {

            holder.etJumlah.setText(
                selectedBahan[item.id].toString()
            )

        } else {

            holder.etJumlah.setText("")
        }

        holder.cbBahan.setOnCheckedChangeListener { _, checked ->
            holder.etJumlah.isEnabled = checked
            if (checked) {
                val jumlah =
                    holder.etJumlah.text.toString()
                        .toDoubleOrNull() ?: 0.0
                selectedBahan[item.id] = jumlah
            } else {
                selectedBahan.remove(item.id)
                holder.etJumlah.setText("")
            }
        }

        holder.etJumlah.addTextChangedListener(
            object : TextWatcher {

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

                override fun afterTextChanged(s: Editable?) {

                    if (holder.cbBahan.isChecked) {

                        val jumlah =
                            s.toString()
                                .toDoubleOrNull() ?: 0.0

                        selectedBahan[item.id] = jumlah
                    }
                }
            }
        )
    }

    fun getSelectedBahan(): List<BahanItem> {
        return selectedBahan.map {
            BahanItem(
                bahanId = it.key,
                jumlah = it.value
            )
        }
    }
}
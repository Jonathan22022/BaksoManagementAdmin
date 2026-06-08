package com.example.baksomanagementadmin.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.baksomanagementadmin.R
import com.example.baksomanagementadmin.data.model.HistoryOrder
import com.example.baksomanagementadmin.data.repository.HistoryOrderRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.widget.TextView

class HistoryOrderFragment : Fragment() {

    private lateinit var rv: RecyclerView

    private lateinit var etSearch: EditText

    private lateinit var btnSelesai: TextView

    private lateinit var btnCancel: TextView

    private val repository = HistoryOrderRepository()

    private var fullList = mutableListOf<HistoryOrder>()

    private var currentStatus = "selesai"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(
            R.layout.fragment_history_order,
            container,
            false
        )

        rv = view.findViewById(R.id.rvHistory)

        etSearch = view.findViewById(R.id.etSearchDate)

        btnSelesai = view.findViewById(R.id.btnSelesai)

        btnCancel = view.findViewById(R.id.btnCancel)

        rv.layoutManager =
            LinearLayoutManager(requireContext())

        loadData()

        btnSelesai.setOnClickListener {
            currentStatus = "selesai"
            switchTab(true)
            filterData()
        }

        btnCancel.setOnClickListener {
            currentStatus = "dibatalkan"
            switchTab(false)
            filterData()
        }

        etSearch.addTextChangedListener(object : TextWatcher {

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
            ) {
                filterData()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        return view
    }

    private fun loadData() {

        repository.getHistoryOrders { list ->

            fullList.clear()

            fullList.addAll(list)

            filterData()
        }
    }

    private fun filterData() {

        val keyword =
            etSearch.text.toString().lowercase()

        val sdf = SimpleDateFormat(
            "dd/MM/yyyy",
            Locale.getDefault()
        )

        val filtered = fullList.filter {

            val date =
                sdf.format(Date(it.createdAt))

            it.status == currentStatus &&
                    date.contains(keyword)
        }

        rv.adapter = HistoryOrderAdapter(filtered)
    }

    private fun switchTab(isSelesai: Boolean) {

        if (isSelesai) {

            btnSelesai.setBackgroundResource(R.drawable.bg_tab_active)
            btnCancel.setBackgroundResource(android.R.color.transparent)

            btnSelesai.setTextColor(resources.getColor(android.R.color.white))
            btnCancel.setTextColor(resources.getColor(R.color.dark_red))

        } else {

            btnCancel.setBackgroundResource(R.drawable.bg_tab_active)
            btnSelesai.setBackgroundResource(android.R.color.transparent)

            btnCancel.setTextColor(resources.getColor(android.R.color.white))
            btnSelesai.setTextColor(resources.getColor(R.color.dark_red))
        }
    }
}
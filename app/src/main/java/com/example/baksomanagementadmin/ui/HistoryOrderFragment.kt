package com.example.baksomanagementadmin.ui

import android.app.AlertDialog
import android.os.Bundle
import android.app.DatePickerDialog
import java.util.Calendar
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
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HistoryOrderFragment : Fragment() {

    private lateinit var rv: RecyclerView
    private lateinit var fabDelete: FloatingActionButton

    private lateinit var adapter: HistoryOrderAdapter

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
        fabDelete = view.findViewById(R.id.fabDelete)

        rv.layoutManager =
            LinearLayoutManager(requireContext())

        loadData()

        btnSelesai.setOnClickListener {
            currentStatus = "selesai"
            switchTab(true)
            filterData()
        }

        btnCancel.setOnClickListener {
            currentStatus = "cancel"
            switchTab(false)
            filterData()
        }

        etSearch.setOnClickListener {
            showDatePicker()
        }

        etSearch.setOnLongClickListener {

            etSearch.setText("")

            filterData()

            true
        }

        fabDelete.setOnClickListener {

            AlertDialog.Builder(
                requireContext()
            )
                .setTitle("Hapus Riwayat")
                .setMessage(
                    "Hapus riwayat terpilih?"
                )
                .setPositiveButton("Ya"){_,_->

                    val selectedOrders =
                        fullList.filter {
                            it.selected
                        }

                    selectedOrders.forEach { order ->
                        fullList.removeAll {
                            it.orderId == order.orderId
                        }
                    }

                    filterData()

                    fabDelete.visibility = View.GONE
                }
                .setNegativeButton(
                    "Tidak",
                    null
                )
                .show()
        }

        return view
    }

    private fun showDatePicker() {

        val calendar = Calendar.getInstance()

        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->

                val selectedCalendar = Calendar.getInstance()

                selectedCalendar.set(
                    year,
                    month,
                    day,
                    0,
                    0,
                    0
                )

                selectedCalendar.set(
                    Calendar.MILLISECOND,
                    0
                )

                val formatter = SimpleDateFormat(
                    "dd/MM/yyyy",
                    Locale.getDefault()
                )

                etSearch.setText(
                    formatter.format(selectedCalendar.time)
                )

                filterData()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun loadData() {

        repository.getHistoryOrders {

            fullList.clear()

            fullList.addAll(it)

            adapter =
                HistoryOrderAdapter(
                    fullList,
                    currentStatus,
                    { showFab ->

                        fabDelete.visibility =
                            if(showFab)
                                View.VISIBLE
                            else
                                View.GONE
                    },
                    { order ->

                        val bundle =
                            Bundle().apply {

                                putString(
                                    "ORDER_ID",
                                    order.orderId
                                )
                            }

                        findNavController().navigate(R.id.action_historyOrderFragment_to_detailHistoryOrderan, bundle)
                    }
                )

            rv.adapter = adapter

            filterData()
        }
    }

    private fun filterData() {

        val selectedDate =
            etSearch.text.toString().trim()

        val sdf = SimpleDateFormat(
            "dd/MM/yyyy",
            Locale.getDefault()
        )

        val filtered =
            fullList.filter {

                val date =
                    sdf.format(
                        Date(it.createdAt)
                    )

                it.status == currentStatus &&
                        (
                                selectedDate.isEmpty()
                                        || date == selectedDate
                                )
            }.toMutableList()

        adapter =
            HistoryOrderAdapter(
                filtered,
                currentStatus,
                { showFab ->

                    fabDelete.visibility =
                        if (showFab)
                            View.VISIBLE
                        else
                            View.GONE
                },

                { order ->

                    val bundle =
                        Bundle().apply {

                            putString(
                                "ORDER_ID",
                                order.orderId
                            )
                        }

                    findNavController().navigate(
                        R.id.action_historyOrderFragment_to_detailHistoryOrderan,
                        bundle
                    )
                }
            )

        rv.adapter = adapter

        adapter.updateData(
            filtered.toMutableList()
        )
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
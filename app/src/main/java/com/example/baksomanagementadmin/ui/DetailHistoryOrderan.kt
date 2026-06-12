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
class DetailHistoryOrderan :
    Fragment(
        R.layout.fragment_detail_history_orderan
    ) {

    private val repository =
        HistoryOrderRepository()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        val orderId =
            arguments?.getString(
                "ORDER_ID"
            ) ?: return

        val rv =
            view.findViewById<RecyclerView>(
                R.id.rvHistoryItems
            )

        rv.layoutManager =
            LinearLayoutManager(
                requireContext()
            )

        repository.getHistoryOrderItems(
            orderId
        ){ items ->

            rv.adapter =
                DetailHistoryOrderanAdapter(
                    items
                )
        }
    }
}
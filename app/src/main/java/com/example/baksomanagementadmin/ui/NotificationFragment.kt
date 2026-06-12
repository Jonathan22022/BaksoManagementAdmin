package com.example.baksomanagementadmin.ui

import android.Manifest
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.baksomanagementadmin.R
import com.example.baksomanagementadmin.data.model.AdminOrderItem
import com.example.baksomanagementadmin.data.remote.FirebaseClient
import com.example.baksomanagementadmin.data.repository.OrderRepository
import com.example.baksomanagementadmin.utils.NotificationHelper
import com.google.firebase.firestore.ListenerRegistration
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class NotificationFragment : Fragment() {

    private lateinit var rv: RecyclerView
    private lateinit var etDate: EditText
    private lateinit var adapter: NotificationAdapter
    private var selectedDateMillis: Long? = null
    private var allOrders: List<AdminOrderItem> = emptyList()
    private val repository =
        OrderRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(
            R.layout.fragment_notification,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        rv = view.findViewById(R.id.rvNotification)

        rv.layoutManager =
            LinearLayoutManager(requireContext())

        adapter =
            NotificationAdapter(emptyList()) { selectedOrder ->

                val bundle = Bundle().apply {
                    putString(
                        "ORDER_ID",
                        selectedOrder.orderId
                    )
                }

                findNavController().navigate(
                    R.id.action_notificationFragment_to_detailOrderanFragment,
                    bundle
                )
            }

        rv.adapter = adapter

        etDate =
            view.findViewById(R.id.etDate)

        etDate.setOnClickListener {
            showDatePicker(etDate)
        }

        loadOrders()
    }

    private fun showDatePicker(
        etDate: EditText
    ) {

        val calendar =
            Calendar.getInstance()

        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->

                val selectedCalendar =
                    Calendar.getInstance()

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

                val startOfDay =
                    selectedCalendar.timeInMillis

                selectedCalendar.add(
                    Calendar.DAY_OF_MONTH,
                    1
                )

                val endOfDay =
                    selectedCalendar.timeInMillis

                val formatter =
                    SimpleDateFormat(
                        "dd/MM/yyyy",
                        Locale.getDefault()
                    )

                etDate.setText(
                    formatter.format(
                        Date(startOfDay)
                    )
                )

                filterByDate(
                    startOfDay,
                    endOfDay
                )

            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
    private fun filterByDate(
        startOfDay: Long,
        endOfDay: Long
    ) {

        val filtered =
            allOrders.filter {

                it.createdAt >= startOfDay &&
                        it.createdAt < endOfDay
            }

        adapter.submitList(filtered)
    }
    private fun loadOrders() {

        repository.getAllOrderItems { orders ->

            allOrders = orders

            adapter.submitList(orders)
        }
    }
}
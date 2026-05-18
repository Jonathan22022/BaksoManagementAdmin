package com.example.baksomanagementadmin.ui

import android.Manifest
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.EditText
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    private var listener: ListenerRegistration? = null
    private var orderListener: ListenerRegistration? = null

    private var firstLoad = true

    private lateinit var rv: RecyclerView
    private lateinit var etDate: EditText

    private val orderRepository = OrderRepository()

    private var fullList =
        mutableListOf<AdminOrderItem>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(
            R.layout.fragment_notification,
            container,
            false
        )

        rv = view.findViewById(R.id.rvNotification)
        etDate = view.findViewById(R.id.etDate)

        rv.layoutManager =
            LinearLayoutManager(requireContext())

        loadOrdersRealtime()

        return view
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        requestNotificationPermission()

        listenNewOrders()

        etDate.setOnClickListener {
            showDatePickerDialog()
        }
    }

    private fun showDatePickerDialog() {

        val calendar = Calendar.getInstance()

        val dialog = DatePickerDialog(
            requireContext(),
            { _, year, month, day ->

                val selectedCalendar =
                    Calendar.getInstance()

                selectedCalendar.set(
                    year,
                    month,
                    day
                )

                val dateFormat =
                    SimpleDateFormat(
                        "dd/MM/yyyy",
                        Locale.getDefault()
                    )

                val selectedDateText =
                    dateFormat.format(
                        selectedCalendar.time
                    )

                etDate.setText(selectedDateText)

                filterOrdersByDate(
                    selectedCalendar.timeInMillis
                )
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        dialog.show()
    }

    private fun filterOrdersByDate(
        selectedDateMillis: Long
    ) {

        val sdf = SimpleDateFormat(
            "dd/MM/yyyy",
            Locale.getDefault()
        )

        val filteredList = fullList.filter {

            val itemDate =
                sdf.format(Date(it.createdAt))

            val selectedDate =
                sdf.format(Date(selectedDateMillis))

            itemDate == selectedDate
        }

        setupAdapter(filteredList)
    }

    private fun loadOrdersRealtime() {

        orderListener?.remove()

        orderListener = FirebaseClient.firestore
            .collection("orders")
            .addSnapshotListener { _, error ->

                if (error != null) return@addSnapshotListener

                orderRepository.getAllOrderItems { list ->

                    val activeOrders =
                        list.filter {

                            it.status == "pending" ||
                                    it.status == "diproses"
                        }

                    fullList.clear()
                    fullList.addAll(activeOrders)

                    setupAdapter(activeOrders)
                }
            }
    }

    private fun setupAdapter(
        list: List<AdminOrderItem>
    ) {

        rv.adapter = NotificationAdapter(list) { selected ->

            val bundle = Bundle().apply {

                putString(
                    "ORDER_ID",
                    selected.orderId
                )
            }

            findNavController().navigate(
                R.id.action_notificationFragment_to_detailOrderanFragment,
                bundle
            )
        }
    }

    private fun requestNotificationPermission() {

        if (
            android.os.Build.VERSION.SDK_INT >=
            android.os.Build.VERSION_CODES.TIRAMISU
        ) {

            if (
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(
                        Manifest.permission.POST_NOTIFICATIONS
                    ),
                    100
                )
            }
        }
    }

    private fun listenNewOrders() {

        listener?.remove()

        listener = FirebaseClient.firestore
            .collection("orders")
            .addSnapshotListener { value, error ->

                if (error != null) {
                    return@addSnapshotListener
                }

                value?.documentChanges?.forEach { change ->

                    if (
                        change.type.name == "ADDED" &&
                        !firstLoad
                    ) {

                        NotificationHelper
                            .showNewOrderNotification(
                                requireContext()
                            )
                    }
                }

                firstLoad = false
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        listener?.remove()
        orderListener?.remove()
    }
}
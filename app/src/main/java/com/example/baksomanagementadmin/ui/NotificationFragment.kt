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

    companion object {
        private const val TAG = "NotificationFragment"
    }

    private var listener: ListenerRegistration? = null
    private var orderListener: ListenerRegistration? = null

    private var firstLoad = true

    private lateinit var rv: RecyclerView
    private lateinit var etDate: EditText

    private val orderRepository = OrderRepository()

    private var fullList = mutableListOf<AdminOrderItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate()")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        Log.d(TAG, "onCreateView()")

        val view = inflater.inflate(
            R.layout.fragment_notification,
            container,
            false
        )

        rv = view.findViewById(R.id.rvNotification)
        etDate = view.findViewById(R.id.etDate)

        rv.layoutManager =
            LinearLayoutManager(requireContext())

        Log.d(TAG, "RecyclerView initialized")

        loadOrdersRealtime()

        return view
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(TAG, "onViewCreated()")

        requestNotificationPermission()

        listenNewOrders()

        etDate.setOnClickListener {

            Log.d(TAG, "Date field clicked")

            showDatePickerDialog()
        }
    }

    private fun showDatePickerDialog() {

        Log.d(TAG, "Opening DatePickerDialog")

        val calendar = Calendar.getInstance()

        val dialog = DatePickerDialog(
            requireContext(),
            { _, year, month, day ->

                Log.d(
                    TAG,
                    "Date selected = $day/${month + 1}/$year"
                )

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

        val selectedDate =
            sdf.format(Date(selectedDateMillis))

        Log.d(
            TAG,
            "Filtering orders for date: $selectedDate"
        )

        val filteredList = fullList.filter {

            val itemDate =
                sdf.format(Date(it.createdAt))

            itemDate == selectedDate
        }

        Log.d(
            TAG,
            "Filter result: ${filteredList.size} orders found"
        )

        setupAdapter(filteredList)
    }

    private fun loadOrdersRealtime() {

        Log.d(TAG, "Starting realtime order listener")

        orderListener?.remove()

        orderListener = FirebaseClient.firestore
            .collection("orders")
            .addSnapshotListener { snapshot, error ->

                if (error != null) {

                    Log.e(
                        TAG,
                        "Firestore listener error",
                        error
                    )

                    return@addSnapshotListener
                }

                Log.d(
                    TAG,
                    "Orders collection changed. Documents = ${snapshot?.size()}"
                )

                orderRepository.getAllOrderItems { list ->

                    Log.d(
                        TAG,
                        "Repository returned ${list.size} orders"
                    )

                    list.forEachIndexed { index, item ->

                        Log.d(
                            TAG,
                            """
                            Order[$index]
                            orderId=${item.orderId}
                            status=${item.status}
                            createdAt=${item.createdAt}
                            """.trimIndent()
                        )
                    }

                    val activeOrders =
                        list.filter {

                            it.status == "pending" ||
                                    it.status == "diproses"
                        }

                    Log.d(
                        TAG,
                        "Active orders count = ${activeOrders.size}"
                    )

                    fullList.clear()
                    fullList.addAll(activeOrders)

                    setupAdapter(activeOrders)
                }
            }
    }

    private fun setupAdapter(
        list: List<AdminOrderItem>
    ) {

        Log.d(
            TAG,
            "Setting adapter with ${list.size} items"
        )

        rv.adapter = NotificationAdapter(list) { selected ->

            Log.d(
                TAG,
                """
                Notification clicked
                orderId=${selected.orderId}
                status=${selected.status}
                """.trimIndent()
            )

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

        Log.d(TAG, "Checking notification permission")

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

                Log.d(
                    TAG,
                    "Notification permission NOT granted"
                )

                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(
                        Manifest.permission.POST_NOTIFICATIONS
                    ),
                    100
                )

            } else {

                Log.d(
                    TAG,
                    "Notification permission already granted"
                )
            }
        }
    }

    private fun listenNewOrders() {

        Log.d(TAG, "Starting new order notification listener")

        listener?.remove()

        listener = FirebaseClient.firestore
            .collection("orders")
            .addSnapshotListener { value, error ->

                if (error != null) {

                    Log.e(
                        TAG,
                        "Notification listener error",
                        error
                    )

                    return@addSnapshotListener
                }

                Log.d(
                    TAG,
                    "Order change detected"
                )

                value?.documentChanges?.forEach { change ->

                    Log.d(
                        TAG,
                        """
                        Change Type = ${change.type}
                        Order ID = ${change.document.id}
                        """.trimIndent()
                    )

                    if (
                        change.type.name == "ADDED" &&
                        !firstLoad
                    ) {

                        Log.d(
                            TAG,
                            "NEW ORDER DETECTED -> SHOW NOTIFICATION"
                        )

                        NotificationHelper
                            .showNewOrderNotification(
                                requireContext()
                            )
                    }
                }

                firstLoad = false

                Log.d(
                    TAG,
                    "First load completed"
                )
            }
    }

    override fun onDestroyView() {

        Log.d(TAG, "onDestroyView()")

        listener?.remove()
        orderListener?.remove()

        Log.d(
            TAG,
            "Firestore listeners removed"
        )

        super.onDestroyView()
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart()")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume()")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause()")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop()")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy()")
    }
}
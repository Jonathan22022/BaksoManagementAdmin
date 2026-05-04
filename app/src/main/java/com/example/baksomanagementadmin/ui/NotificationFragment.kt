package com.example.baksomanagementadmin.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.baksomanagementadmin.R
import com.example.baksomanagementadmin.data.repository.OrderRepository

class NotificationFragment : Fragment() {

    private lateinit var rv: RecyclerView
    private val orderRepository = OrderRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_notification, container, false)
        rv = view.findViewById(R.id.rvNotification)
        rv.layoutManager = LinearLayoutManager(requireContext())
        loadOrders()
        return view
    }

    private fun loadOrders() {
        orderRepository.getAllOrderItems { list ->
            rv.adapter = NotificationAdapter(list) { selected ->
                val bundle = Bundle().apply {
                    putString("ORDER_ID", selected.orderId)
                }
                findNavController().navigate(
                    R.id.action_notificationFragment_to_detailOrderanFragment,
                    bundle
                )
            }
        }
    }
}
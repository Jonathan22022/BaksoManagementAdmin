package com.example.baksomanagementadmin.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.baksomanagementadmin.R
import com.example.baksomanagementadmin.data.model.Cart
import androidx.navigation.fragment.findNavController

class CartFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_cart, container, false)

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerCart)
        val emptyLayout = view.findViewById<View>(R.id.layoutEmpty)
        val btnAddMenu = view.findViewById<View>(R.id.btnAddMenu)

        recycler.layoutManager = LinearLayoutManager(requireContext())

        // 🔥 COBA KOSONGKAN UNTUK TEST
        val cartList = listOf<Cart>() // kosong

        // val cartList = listOf(...) // isi seperti sebelumnya

        if (cartList.isEmpty()) {
            recycler.visibility = View.GONE
            emptyLayout.visibility = View.VISIBLE
        } else {
            recycler.visibility = View.VISIBLE
            emptyLayout.visibility = View.GONE
            recycler.adapter = CartAdapter(cartList)
        }

        // BUTTON TAMBAHKAN PESANAN
        btnAddMenu.setOnClickListener {
            findNavController().navigate(R.id.action_cartFragment_to_menuFragment)
        }

        return view
    }
}
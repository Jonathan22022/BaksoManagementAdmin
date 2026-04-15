package com.example.baksomanagementadmin.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.baksomanagementadmin.R
import com.example.baksomanagementadmin.data.model.Menu
import com.example.baksomanagementadmin.data.repository.MenuRepository
import androidx.navigation.fragment.findNavController

class DetailMenuFragment : Fragment() {

    private lateinit var menuRepository: MenuRepository
    private var menuId: String = ""

    private var quantity = 1
    private var basePrice = 0
    private var selectedAddonPrice = 0

    private lateinit var tvQty: TextView
    private lateinit var tvTotal: TextView
    private lateinit var layoutAddons: LinearLayout
    private lateinit var btnEditMenu: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        menuId = arguments?.getString("MENU_ID") ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_detail_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        menuRepository = MenuRepository()

        val tvName = view.findViewById<TextView>(R.id.tvMenuName)
        val imgMenu = view.findViewById<ImageView>(R.id.imgMenu)
        val tvDesc = view.findViewById<TextView>(R.id.tvDescription)

        tvQty = view.findViewById(R.id.tvQty)
        tvTotal = view.findViewById(R.id.tvTotal)
        layoutAddons = view.findViewById(R.id.layoutAddonsContainer)

        val btnPlus = view.findViewById<Button>(R.id.btnPlus)
        val btnMinus = view.findViewById<Button>(R.id.btnMinus)
        btnEditMenu = view.findViewById(R.id.btnEditMenu)
        menuRepository.getMenuById(menuId) { menu ->

            menu?.let {

                tvName.text = it.namaMenu
                tvDesc.text = "Menu spesial pilihan"

                basePrice = it.harga
                updateTotal()

                Glide.with(requireContext())
                    .load(it.gambarUrl)
                    .into(imgMenu)

                loadAddons(it)
            }
        }

        // PLUS
        btnPlus.setOnClickListener {
            quantity++
            tvQty.text = quantity.toString()
            updateTotal()
        }

        // MINUS
        btnMinus.setOnClickListener {
            if (quantity > 1) {
                quantity--
                tvQty.text = quantity.toString()
                updateTotal()
            }
        }

        btnEditMenu.setOnClickListener {

            val fragment = EditMenuFragment().apply {
                arguments = Bundle().apply {
                    putString("MENU_ID", menuId)
                }
            }

            findNavController().navigate(R.id.action_menuDetailFragment_to_editMenuFragment)
        }
    }

    private fun loadAddons(menu: Menu) {

        layoutAddons.removeAllViews()

        val btnNoAdd = Button(requireContext())
        btnNoAdd.text = "No addons"
        btnNoAdd.setOnClickListener {
            selectedAddonPrice = 0
            updateTotal()
        }

        layoutAddons.addView(btnNoAdd)

        for (addon in menu.addons) {

            val btn = Button(requireContext())
            btn.text = "${addon.name} (+ Rp ${addon.price})"

            btn.setOnClickListener {
                selectedAddonPrice = addon.price
                updateTotal()
            }

            layoutAddons.addView(btn)
        }
    }

    private fun updateTotal() {
        val total = (basePrice + selectedAddonPrice) * quantity
        tvTotal.text = "Rp. $total"
    }
}
package com.example.baksomanagementadmin.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.baksomanagementadmin.R
import com.example.baksomanagementadmin.data.model.Menu
import com.example.baksomanagementadmin.data.repository.AddOnRepository
import com.example.baksomanagementadmin.data.repository.MenuRepository

class AddOnFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var addOnRepository: AddOnRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_add_on, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        recyclerView = view.findViewById(R.id.recyclerAddOn)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        addOnRepository = AddOnRepository()

        val btnTambah = view.findViewById<Button>(R.id.btnTambahAddOn)

        btnTambah.setOnClickListener {
            goToTambahAddOn()
        }
        loadAddOn()
    }

    private fun goToTambahAddOn() {
        findNavController().navigate(R.id.action_addOnFragment_to_tambahAddOnFragment)
    }

    private fun loadAddOn() {
        addOnRepository.getAddOnList { addOnList ->

            val adapter = AddOnAdapter(
                addOnList,
                onEditClick = { addon ->
                    val bundle = Bundle().apply {
                        putString("ADDON_ID", addon.id)
                    }
                    findNavController().navigate(
                        R.id.action_addOnFragment_to_editAddOnFragment,
                        bundle
                    )
                },
                onDeleteClick = { addon ->
                    addOnRepository.deleteAddOn(
                        addon,
                        onSuccess = {
                            loadAddOn() // refresh list setelah delete
                        },
                        onError = {
                            Log.e("MenuFragment", "Delete gagal: ${it.message}")
                        }
                    )
                }
            )

            recyclerView.adapter = adapter
        }
    }
}
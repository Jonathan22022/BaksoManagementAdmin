package com.example.baksomanagementadmin.ui.addon

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.baksomanagementadmin.R
import com.example.baksomanagementadmin.data.repository.AddOnRepository
import com.example.baksomanagementadmin.data.repository.BahanBakuRepository

class AddOnFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var addOnRepository: AddOnRepository
    private val bahanRepository = BahanBakuRepository()

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

        bahanRepository.getBahanBakuList { bahanList ->

            addOnRepository.getAddOnList { addOnList ->

                recyclerView.adapter =
                    AddOnAdapter(
                        addOnList = addOnList,
                        bahanList = bahanList,
                        onEditClick = { addon ->

                            val bundle = Bundle().apply {
                                putString(
                                    "ADDON_ID",
                                    addon.id
                                )
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
                                    loadAddOn()
                                },
                                onError = {
                                    Log.e(
                                        "AddOnFragment",
                                        it.message ?: ""
                                    )
                                }
                            )
                        }
                    )
            }
        }
    }
}
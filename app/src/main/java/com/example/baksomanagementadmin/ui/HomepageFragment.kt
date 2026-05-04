package com.example.baksomanagementadmin.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.baksomanagementadmin.R
import com.example.baksomanagementadmin.data.model.Menu
import com.example.baksomanagementadmin.data.repository.MenuRepository
import androidx.navigation.fragment.findNavController
import android.util.Log
import android.widget.Button

class HomepageFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var menuRepository: MenuRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_homepage, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        recyclerView = view.findViewById(R.id.recyclerMenu)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        menuRepository = MenuRepository()

        val btnTambah = view.findViewById<Button>(R.id.btnTambahMenu)

        btnTambah.setOnClickListener {
            goToAddMenu()
        }
        loadMenu()
    }

    private fun goToAddMenu() {
        findNavController().navigate(
            R.id.action_homepageFragment_to_addMenuFragment
        )
    }

    private fun loadMenu() {
        menuRepository.getMenuList { menuList ->

            val adapter = MenuAdapter(
                menuList,
                onItemClick = { selectedMenu ->
                    openDetailMenu(selectedMenu)
                },
                onEditClick = { menu ->
                    val bundle = Bundle().apply {
                        putString("MENU_ID", menu.id)
                    }
                    findNavController().navigate(
                        R.id.action_homepageFragment_to_editMenuFragment,
                        bundle
                    )
                },
                onDeleteClick = { menu ->
                    menuRepository.deleteMenu(
                        menu,
                        onSuccess = {
                            loadMenu() // refresh list setelah delete
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

    private fun openDetailMenu(menu: Menu) {
        val bundle = Bundle().apply {
            putString("MENU_ID", menu.id)
        }

        findNavController().navigate(
            R.id.action_homepageFragment_to_detailMenuFragment,
            bundle
        )
    }
}
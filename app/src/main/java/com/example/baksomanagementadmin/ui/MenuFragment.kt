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

class MenuFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var menuRepository: MenuRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        recyclerView = view.findViewById(R.id.recyclerMenu)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        menuRepository = MenuRepository()

        loadMenu()
    }

    private fun loadMenu() {
        menuRepository.getMenuList { menuList ->

            val adapter = MenuAdapter(menuList) { selectedMenu ->
                openDetailMenu(selectedMenu)
            }

            recyclerView.adapter = adapter
        }
    }

    private fun openDetailMenu(menu: Menu) {

        val fragment = DetailMenuFragment().apply {
            arguments = Bundle().apply {
                putString("MENU_ID", menu.id)
            }
        }

        findNavController().navigate(R.id.action_menuFragment_to_detailMenuFragment)
    }
}
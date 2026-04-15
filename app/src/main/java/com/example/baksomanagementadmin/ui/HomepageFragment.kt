package com.example.baksomanagementadmin.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.baksomanagementadmin.R
import com.example.baksomanagementadmin.data.repository.MenuRepository
import android.util.Log

class HomepageFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private val repository = MenuRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_homepage, container, false)

        recyclerView = view.findViewById(R.id.recyclerMenu)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        loadMenu()

        return view
    }

    private fun loadMenu() {
        repository.getMenuList { menuList ->

            val adapter = MenuAdapter(menuList) { menu ->

                val fragment = DetailMenuFragment()

                parentFragmentManager.beginTransaction()
                    .replace(R.id.home_fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            }

            recyclerView.adapter = adapter
        }
    }
}
package com.example.baksomanagementadmin.ui

import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.baksomanagementadmin.R
import com.example.baksomanagementadmin.data.model.Menu
import com.example.baksomanagementadmin.data.repository.MenuRepository

class SearchFragment : Fragment() {

    private lateinit var etSearch: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvNoResults: TextView

    private lateinit var menuRepository: MenuRepository

    private var fullMenuList = listOf<Menu>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        etSearch = view.findViewById(R.id.etSearch)
        recyclerView = view.findViewById(R.id.recyclerViewSearchResults)
        tvNoResults = view.findViewById(R.id.tvNoResults)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        menuRepository = MenuRepository()

        menuRepository.getMenuList { list ->
            fullMenuList = list
        }

        etSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(query: CharSequence?, start: Int, before: Int, count: Int) {
                filterMenu(query.toString())
            }
        })
    }

    private fun filterMenu(query: String) {

        val filteredList = fullMenuList.filter {
            it.namaMenu.contains(query, ignoreCase = true)
        }

        if (filteredList.isEmpty()) {
            tvNoResults.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            tvNoResults.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE

            val adapter = MenuAdapter(filteredList) { selectedMenu ->
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
        findNavController().navigate(R.id.action_searchFragment_to_detailMenuFragment)
    }
}
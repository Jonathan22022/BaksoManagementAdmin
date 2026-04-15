package com.example.baksomanagementadmin.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.baksomanagementadmin.R
import com.example.baksomanagementadmin.data.model.Favourite

class FavouriteFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_favourite, container, false)

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerFavourite)
        val emptyLayout = view.findViewById<View>(R.id.layoutEmpty)

        recycler.layoutManager = LinearLayoutManager(requireContext())

        val favouriteList = listOf<Favourite>() // 🔥 kosongkan untuk test

        if (favouriteList.isEmpty()) {
            recycler.visibility = View.GONE
            emptyLayout.visibility = View.VISIBLE
        } else {
            recycler.visibility = View.VISIBLE
            emptyLayout.visibility = View.GONE
            recycler.adapter = FavouriteAdapter(favouriteList)
        }

        return view
    }
}
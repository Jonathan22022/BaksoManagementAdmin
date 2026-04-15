package com.example.baksomanagementadmin.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.baksomanagementadmin.R
import androidx.navigation.fragment.findNavController

class CheckoutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_checkout, container, false)

        val btnCheckout = view.findViewById<Button>(R.id.btnCheckout)

        btnCheckout.setOnClickListener {

            showConfirmationDialog()
        }

        return view
    }

    private fun showConfirmationDialog() {

        AlertDialog.Builder(requireContext())
            .setTitle("Konfirmasi Order")
            .setMessage("Apakah orderan sudah sesuai?")
            .setPositiveButton("Ya") { _, _ ->
                findNavController().navigate(R.id.action_checkoutFragment_to_paymentMethodFragment)
            }
            .setNegativeButton("Cek lagi", null)
            .show()
    }
}
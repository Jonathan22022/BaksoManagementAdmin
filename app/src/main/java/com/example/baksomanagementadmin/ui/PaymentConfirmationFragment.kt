package com.example.baksomanagementadmin.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.baksomanagementadmin.R
import androidx.navigation.fragment.findNavController

class PaymentConfirmationFragment : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnPay = view.findViewById<View>(R.id.btnPay)

        btnPay.setOnClickListener {
            findNavController().navigate(R.id.action_paymentConfirmationFragment_to_enterPinFragment)
        }
    }
}
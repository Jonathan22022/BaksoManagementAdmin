package com.example.baksomanagementadmin.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.baksomanagementadmin.R
import androidx.navigation.fragment.findNavController

class PaymentMethodFragment : Fragment() {

    private lateinit var content: ViewGroup

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_payment_method, container, false)

        val tabEwallet = view.findViewById<View>(R.id.tabEwallet)
        val tabBank = view.findViewById<View>(R.id.tabBank)
        val tabQRIS = view.findViewById<View>(R.id.tabQRIS)
        content = view.findViewById(R.id.paymentContent)

        // default
        loadContent(R.layout.layout_ewallet)
        setActiveTab(tabEwallet, tabBank, tabQRIS)

        tabEwallet.setOnClickListener {
            loadContent(R.layout.layout_ewallet)
            setActiveTab(tabEwallet, tabBank, tabQRIS)
        }

        tabBank.setOnClickListener {
            loadContent(R.layout.layout_bank)
            setActiveTab(tabBank, tabEwallet, tabQRIS)
        }

        tabQRIS.setOnClickListener {
            loadContent(R.layout.layout_qris)
            setActiveTab(tabQRIS, tabEwallet, tabBank)
        }

        val btnChoose = view.findViewById<View>(R.id.btnChoosePayment)

        btnChoose.setOnClickListener {
            findNavController().navigate(R.id.action_paymentMethodFragment_to_paymentConfirmationFragment)
        }

        return view
    }

    private fun loadContent(layoutId: Int) {
        content.removeAllViews()
        LayoutInflater.from(requireContext())
            .inflate(layoutId, content, true)
    }

    private fun setActiveTab(active: View, vararg inactive: View) {

        active.setBackgroundResource(R.drawable.bg_tab_active)

        inactive.forEach {
            it.setBackgroundResource(R.drawable.bg_tab_inactive)
        }
    }
}
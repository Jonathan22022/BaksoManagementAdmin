package com.example.baksomanagementadmin.ui

import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.baksomanagementadmin.R
import com.example.baksomanagementadmin.viewmodel.LupaPasswordViewModel

class LupaPasswordFragment : Fragment() {

    private val viewModel: LupaPasswordViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_lupa_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val etEmail = view.findViewById<EditText>(R.id.etEmail)
        val btnSend = view.findViewById<Button>(R.id.btnSend)
        val btnBack = view.findViewById<ImageView>(R.id.btnBack)

        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        btnSend.setOnClickListener {

            val email = etEmail.text.toString().trim()
            viewModel.sendResetEmail(email)
        }

        viewModel.resetResult.observe(viewLifecycleOwner) { result ->

            if (result.first) {
                Toast.makeText(
                    requireContext(),
                    "Email reset password berhasil dikirim",
                    Toast.LENGTH_LONG
                ).show()

                findNavController().popBackStack()

            } else {
                Toast.makeText(
                    requireContext(),
                    result.second ?: "Gagal mengirim email",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
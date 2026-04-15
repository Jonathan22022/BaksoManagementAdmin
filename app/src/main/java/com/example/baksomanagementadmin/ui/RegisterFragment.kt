package com.example.baksomanagementadmin.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.baksomanagementadmin.R
import com.example.baksomanagementadmin.viewmodel.RegisterViewModel
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController

class RegisterFragment : Fragment() {

    private val viewModel: RegisterViewModel by viewModels()
    private val TAG = "RegisterFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate dipanggil")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView dipanggil")
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        Log.d(TAG, "onViewCreated dipanggil")

        val etUsername = view.findViewById<EditText>(R.id.etUsername)
        val etEmail = view.findViewById<EditText>(R.id.etEmail)
        val etPassword = view.findViewById<EditText>(R.id.etPassword)
        val etConfirmPassword = view.findViewById<EditText>(R.id.etConfirmPassword)
        val etPhone = view.findViewById<EditText>(R.id.etPhone)
        val btnRegister = view.findViewById<Button>(R.id.btnRegister)

        btnRegister.setOnClickListener {

            val nama = etUsername.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()
            val phone = etPhone.text.toString().trim()

            Log.d(TAG, "Tombol Register ditekan")
            Log.d(TAG, "Nama: $nama")
            Log.d(TAG, "Email: $email")
            Log.d(TAG, "Phone: $phone")
            Log.d(TAG, "Password length: ${password.length}")

            viewModel.register(
                email,
                password,
                confirmPassword,
                nama,
                phone
            ) { success, message ->

                Log.d(TAG, "Hasil register: $success")

                if (success) {

                    Log.i(TAG, "Register berhasil, kembali ke FirstPageFragment")

                    Toast.makeText(
                        requireContext(),
                        "Register berhasil!",
                        Toast.LENGTH_SHORT
                    ).show()

                    findNavController().navigate(R.id.action_registerFragment_to_firstPageFragment)

                } else {

                    Log.e(TAG, "Register gagal: $message")

                    Toast.makeText(
                        requireContext(),
                        message ?: "Register gagal",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        val btnBack = view.findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            Log.d(TAG, "Tombol back ditekan")
            findNavController().navigate(R.id.action_registerFragment_to_firstPageFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView dipanggil")
    }
}
package com.example.baksomanagementadmin.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.baksomanagementadmin.HomepageActivity
import com.example.baksomanagementadmin.R
import com.example.baksomanagementadmin.data.repository.AuthRepository
import com.example.baksomanagementadmin.data.repository.UserRepository
import com.example.baksomanagementadmin.utils.SavedAccountManager
import com.example.baksomanagementadmin.utils.SessionManager
import com.example.baksomanagementadmin.viewmodel.LoginViewModel

class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModels()
    private val TAG = "LoginFragment"

    private var currentEmail: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate dipanggil")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView dipanggil")
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        Log.d(TAG, "onViewCreated dipanggil")

        val etEmail = view.findViewById<EditText>(R.id.etEmail)
        val etPassword = view.findViewById<EditText>(R.id.etPassword)
        val btnLogin = view.findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {

            currentEmail = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            Log.d(TAG, "Tombol Login ditekan")
            Log.d(TAG, "Email: $currentEmail")
            Log.d(TAG, "Password length: ${password.length}")

            viewModel.login(currentEmail, password)
        }

        viewModel.loginResult.observe(viewLifecycleOwner) { result ->

            Log.d(TAG, "Hasil login diterima: ${result.first}")

            if (result.first) {

                Log.i(TAG, "Login Firebase berhasil")

                val userRepository = UserRepository()

                userRepository.getCurrentUserDetail { user, error ->

                    if (user != null) {

                        Log.d(TAG, "Role user: ${user.role}")

                        if (user.role == "admin") {

                            Toast.makeText(
                                requireContext(),
                                "Login Berhasil",
                                Toast.LENGTH_SHORT
                            ).show()

                            SavedAccountManager.saveAccount(
                                requireContext(),
                                currentEmail
                            )

                            val intent = Intent(
                                requireContext(),
                                HomepageActivity::class.java
                            )

                            startActivity(intent)
                            requireActivity().finish()

                        } else {

                            Log.e(TAG, "Akses ditolak karena role bukan admin")

                            AuthRepository().logout()

                            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                                .setTitle("Akses Ditolak")
                                .setMessage("User tidak dapat mengakses aplikasi admin")
                                .setPositiveButton("OK", null)
                                .show()
                        }

                    } else {

                        Log.e(TAG, "Gagal mengambil data user: $error")

                        Toast.makeText(
                            requireContext(),
                            error ?: "Data user tidak ditemukan",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            } else {

                Log.e(TAG, "Login gagal: ${result.second}")

                Toast.makeText(
                    requireContext(),
                    result.second ?: "Login Gagal",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        val tvForgot = view.findViewById<TextView>(R.id.tvForgotPassword)

        tvForgot.setOnClickListener {
            findNavController().navigate(
                R.id.action_loginFragment_to_lupaPasswordFragment
            )
        }

        val btnBack = view.findViewById<ImageView>(R.id.btnBack)

        btnBack.setOnClickListener {

            Log.d(TAG, "Tombol back ditekan")

            findNavController().navigate(
                R.id.action_loginFragment_to_firstPageFragment
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView dipanggil")
    }
}
package com.example.baksomanagementadmin.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.baksomanagementadmin.R
import com.example.baksomanagementadmin.viewmodel.FirstPageViewModel

class FirstPageFragment : Fragment() {

    private val viewModel: FirstPageViewModel by viewModels()

    private val TAG = "FirstPageFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate dipanggil")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView dipanggil")
        return inflater.inflate(R.layout.fragment_first_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        Log.d(TAG, "onViewCreated dipanggil")

        val btnLogin = view.findViewById<Button>(R.id.btnLogin)
        val btnRegister = view.findViewById<Button>(R.id.btnRegister)

        btnLogin.setOnClickListener {
            Log.d(TAG, "Tombol Login ditekan")

            viewModel.onLoginClicked()

            findNavController().navigate(R.id.action_firstPageFragment_to_loginFragment)

            Log.d(TAG, "Berpindah ke LoginFragment")
        }

        btnRegister.setOnClickListener {
            Log.d(TAG, "Tombol Register ditekan")

            viewModel.onRegisterClicked()

            findNavController().navigate(R.id.action_firstPageFragment_to_registerFragment)

            Log.d(TAG, "Berpindah ke RegisterFragment")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView dipanggil")
    }
}
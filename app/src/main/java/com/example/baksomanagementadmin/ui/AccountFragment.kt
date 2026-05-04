package com.example.baksomanagementadmin.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.baksomanagementadmin.R
import com.example.baksomanagementadmin.data.remote.FirebaseClient
import com.example.baksomanagementadmin.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth

class AccountFragment : Fragment() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseClient.firestore
    private val userRepository = UserRepository()
    private val TAG = "AccountFragment"

    private lateinit var imgProfile: ImageView
    private lateinit var tvNama: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvAccountAge: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_account, container, false)

        imgProfile = view.findViewById(R.id.imgProfile)
        tvNama = view.findViewById(R.id.tvNama)
        tvEmail = view.findViewById(R.id.tvEmail)
        tvPhone = view.findViewById(R.id.tvPhone)
        tvAccountAge = view.findViewById(R.id.tvAccountAge)

        Log.d(TAG, "onCreateView dipanggil")

        loadUserData()

        return view
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume dipanggil - reload data user")
        loadUserData()
    }

    private fun loadUserData() {
        userRepository.getCurrentUserDetail { user, error ->
            if (user != null) {
                tvNama.text = user.nama
                tvEmail.text = user.email
                tvPhone.text = user.noTelp
                if (user.profilePicture.isNotEmpty()) {
                    Glide.with(this)
                        .load(user.profilePicture)
                        .placeholder(R.drawable.ic_account_)
                        .into(imgProfile)
                } else {
                    imgProfile.setImageResource(R.drawable.ic_account_)
                }

                val now = System.currentTimeMillis()
                val diff = now - user.createdAt

                val days = diff / (1000 * 60 * 60 * 24)
                val months = days / 30
                val years = days / 365

                val ageText = when {
                    years > 0 -> "$years tahun"
                    months > 0 -> "$months bulan"
                    else -> "$days hari"
                }
                tvAccountAge.text = ageText
            } else {
                Log.e(TAG, "Error: $error")

                Toast.makeText(
                    requireContext(),
                    error ?: "Gagal ambil data user",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
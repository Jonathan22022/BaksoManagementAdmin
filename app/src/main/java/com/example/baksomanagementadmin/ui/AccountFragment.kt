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
import com.google.firebase.auth.FirebaseAuth

class AccountFragment : Fragment() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseClient.firestore

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

        val userId = auth.currentUser?.uid

        Log.d(TAG, "User ID: $userId")

        if (userId == null) {
            Log.e(TAG, "User belum login")
            Toast.makeText(requireContext(), "User belum login", Toast.LENGTH_SHORT).show()
            return
        }

        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->

                Log.d(TAG, "Firestore berhasil diambil")

                if (document.exists()) {

                    Log.d(TAG, "Document user ditemukan")

                    val nama = document.getString("nama")
                    val email = document.getString("email")
                    val phone = document.getString("noTelp")
                    val imageUrl = document.getString("profilePicture")
                    val createdAt = document.getLong("createdAt") ?: 0L

                    Log.d(TAG, "Nama: $nama")
                    Log.d(TAG, "Email: $email")
                    Log.d(TAG, "Phone: $phone")
                    Log.d(TAG, "Image URL: $imageUrl")
                    Log.d(TAG, "Created At: $createdAt")

                    tvNama.text = nama
                    tvEmail.text = email
                    tvPhone.text = phone

                    // load foto profil
                    if (!imageUrl.isNullOrEmpty()) {

                        Log.d(TAG, "Load gambar dengan Glide")

                        Glide.with(this)
                            .load(imageUrl)
                            .placeholder(R.drawable.ic_account_)
                            .into(imgProfile)

                    } else {

                        Log.d(TAG, "Image URL kosong, gunakan placeholder")

                    }

                    // hitung usia akun
                    val now = System.currentTimeMillis()
                    val diff = now - createdAt

                    val days = diff / (1000 * 60 * 60 * 24)
                    val months = days / 30
                    val years = days / 365

                    Log.d(TAG, "Days: $days, Months: $months, Years: $years")

                    val ageText = when {
                        years > 0 -> "$years tahun"
                        months > 0 -> "$months bulan"
                        else -> "$days hari"
                    }

                    tvAccountAge.text = ageText

                    Log.d(TAG, "Account age: $ageText")

                } else {

                    Log.e(TAG, "Document user tidak ditemukan")

                }
            }
            .addOnFailureListener { e ->

                Log.e(TAG, "Gagal mengambil data user", e)

                Toast.makeText(
                    requireContext(),
                    "Gagal mengambil data user",
                    Toast.LENGTH_SHORT
                ).show()

            }
    }
}
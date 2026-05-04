package com.example.baksomanagementadmin.data.repository

import com.example.baksomanagementadmin.data.model.User
import com.example.baksomanagementadmin.data.remote.FirebaseClient

class UserRepository {

    private val auth = FirebaseClient.auth
    private val firestore = FirebaseClient.firestore

    fun registerUser(
        email: String,
        password: String,
        nama: String,
        noTelp: String,
        onResult: (Boolean, String?) -> Unit
    ) {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->

                val userId = result.user?.uid ?: ""

                val user = User(
                    userId = userId,
                    nama = nama,
                    email = email,
                    noTelp = noTelp
                )

                firestore.collection("users")
                    .document(userId)
                    .set(user)
                    .addOnSuccessListener {
                        onResult(true, null)
                    }
                    .addOnFailureListener { e ->
                        onResult(false, e.message)
                    }
            }
            .addOnFailureListener { e ->
                onResult(false, e.message)
            }
    }

    fun getCurrentUserData(
        onResult: (User?) -> Unit
    ) {
        val userId = auth.currentUser?.uid

        if (userId == null) {
            onResult(null)
            return
        }

        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                onResult(user)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    fun getCurrentUserDetail(
        onResult: (User?, String?) -> Unit
    ) {
        val userId = auth.currentUser?.uid

        if (userId == null) {
            onResult(null, "User belum login")
            return
        }

        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->

                if (document.exists()) {
                    val user = document.toObject(User::class.java)
                    onResult(user, null)
                } else {
                    onResult(null, "Data user tidak ditemukan")
                }
            }
            .addOnFailureListener { e ->
                onResult(null, e.message)
            }
    }
}
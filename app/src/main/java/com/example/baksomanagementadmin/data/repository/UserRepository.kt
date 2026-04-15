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
}
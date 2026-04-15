package com.example.baksomanagementadmin.data.repository

import com.example.baksomanagementadmin.data.remote.FirebaseClient

class AuthRepository {

    private val auth = FirebaseClient.auth

    fun loginUser(
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener {
                onResult(false, it.message)
            }
    }

    fun sendResetPasswordEmail(
        email: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener {
                onResult(false, it.message)
            }
    }

    fun logout() {
        auth.signOut()
    }
}
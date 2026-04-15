package com.example.baksomanagementadmin.viewmodel

import androidx.lifecycle.ViewModel
import com.example.baksomanagementadmin.data.repository.UserRepository

class RegisterViewModel : ViewModel() {

    private val repository = UserRepository()

    fun validatePassword(password: String): String? {

        if (password.length < 6) {
            return "Password minimal 6 karakter"
        }

        val hasLetter = password.any { it.isLetter() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecial = password.any { !it.isLetterOrDigit() }

        if (!hasLetter) return "Password harus mengandung huruf"
        if (!hasDigit) return "Password harus mengandung angka"
        if (!hasSpecial) return "Password harus mengandung karakter spesial"

        return null
    }

    fun register(
        email: String,
        password: String,
        confirmPassword: String,
        nama: String,
        noTelp: String,
        onResult: (Boolean, String?) -> Unit
    ) {

        // VALIDASI CONFIRM PASSWORD
        if (password != confirmPassword) {
            onResult(false, "Password dan Confirm Password tidak sama")
            return
        }

        // VALIDASI PASSWORD
        val passwordError = validatePassword(password)
        if (passwordError != null) {
            onResult(false, passwordError)
            return
        }

        repository.registerUser(email, password, nama, noTelp, onResult)
    }
}
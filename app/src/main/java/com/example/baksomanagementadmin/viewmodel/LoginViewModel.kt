package com.example.baksomanagementadmin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.baksomanagementadmin.data.repository.AuthRepository

class LoginViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _loginResult = MutableLiveData<Pair<Boolean, String?>>()
    val loginResult: LiveData<Pair<Boolean, String?>> = _loginResult

    fun login(email: String, password: String) {

        if (email.isEmpty() || password.isEmpty()) {
            _loginResult.value = Pair(false, "Email dan Password tidak boleh kosong")
            return
        }

        repository.loginUser(email, password) { success, message ->
            _loginResult.postValue(Pair(success, message))
        }
    }
}
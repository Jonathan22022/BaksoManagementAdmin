package com.example.baksomanagementadmin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.baksomanagementadmin.data.repository.AuthRepository

class LupaPasswordViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _resetResult = MutableLiveData<Pair<Boolean, String?>>()
    val resetResult: LiveData<Pair<Boolean, String?>> = _resetResult

    fun sendResetEmail(email: String) {

        if (email.isEmpty()) {
            _resetResult.value = Pair(false, "Email tidak boleh kosong")
            return
        }

        repository.sendResetPasswordEmail(email) { success, message ->
            _resetResult.postValue(Pair(success, message))
        }
    }
}
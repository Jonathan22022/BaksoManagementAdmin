package com.example.baksomanagementadmin.data.remote.api

import com.example.baksomanagementadmin.data.model.PaymentRequest
import com.example.baksomanagementadmin.data.model.PaymentResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("api/payment/create-qris")

    fun createQris(

        @Body request:PaymentRequest

    ):Call<PaymentResponse>

}
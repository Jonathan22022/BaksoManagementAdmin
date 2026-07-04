package com.example.baksomanagementadmin.data.model

data class PaymentResponse(
    val success:Boolean,
    val message:String,
    val data:PaymentData
)

data class PaymentData(
    val transactionId:String,
    val qrUrl:String,
    val expiryTime:String
)
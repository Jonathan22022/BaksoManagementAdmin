package com.example.baksomanagementadmin.data.repository

import com.example.baksomanagementadmin.data.model.RiwayatPembelianBahan
import com.example.baksomanagementadmin.data.remote.FirebaseClient
import com.google.firebase.firestore.toObjects

class RiwayatPembelianRepository {

    private val firestore =
        FirebaseClient.firestore

    fun addPembelian(
        data: RiwayatPembelianBahan,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {

        val docRef =
            firestore.collection("riwayat_pembelian_bahan")
                .document()

        docRef.set(
            data.copy(id = docRef.id)
        )
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onError(it)
            }
    }

    fun getAllPembelian(
        callback: (List<RiwayatPembelianBahan>) -> Unit
    ) {

        firestore
            .collection("riwayat_pembelian_bahan")
            .orderBy("createdAt")
            .get()
            .addOnSuccessListener { snapshot ->

                val list =
                    snapshot.toObjects<RiwayatPembelianBahan>()

                callback(list)
            }
            .addOnFailureListener {

                callback(emptyList())
            }
    }
}
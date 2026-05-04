package com.example.baksomanagementadmin.data.repository

import com.example.baksomanagementadmin.data.model.BahanBaku
import com.example.baksomanagementadmin.data.remote.FirebaseClient
import com.google.firebase.firestore.ktx.toObject

class BahanBakuRepository {

    private val firestore = FirebaseClient.firestore

    fun getBahanById(id: String, onResult: (BahanBaku?) -> Unit) {
        firestore.collection("bahanbaku").document(id)
            .get()
            .addOnSuccessListener {
                val bahan = it.toObject(BahanBaku::class.java)?.copy(id = it.id)
                onResult(bahan)
            }
    }

    fun getBahanBakuList(onResult: (List<BahanBaku>) -> Unit) {
        firestore.collection("bahanbaku")
            .get()
            .addOnSuccessListener { result ->
                val list = result.documents.mapNotNull { doc ->
                    val data = doc.toObject<BahanBaku>()
                    data?.copy(id = doc.id)
                }
                onResult(list)
            }
    }

    fun addBahanBaku(
        bahanBaku: BahanBaku,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val docRef = firestore.collection("bahanbaku").document()
        val newData = bahanBaku.copy(id = docRef.id)

        docRef.set(newData)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    fun updateBahanBaku(
        bahanBaku: BahanBaku,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        firestore.collection("bahanbaku").document(bahanBaku.id)
            .set(bahanBaku)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    fun deleteBahanBaku(
        bahanBaku: BahanBaku,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        firestore.collection("bahanbaku").document(bahanBaku.id)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }
}
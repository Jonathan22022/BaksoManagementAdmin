package com.example.baksomanagementadmin.data.repository

import com.example.baksomanagementadmin.data.model.AddOn
import com.example.baksomanagementadmin.data.remote.FirebaseClient
import com.google.firebase.firestore.ktx.toObject


class AddOnRepository {
    private val firestore = FirebaseClient.firestore

    fun getAddOnList(onResult: (List<AddOn>) -> Unit) {
        firestore.collection("addons")
            .get()
            .addOnSuccessListener { result ->
                val addOnList = result.documents.mapNotNull { doc ->
                    val addOn = doc.toObject<AddOn>()
                    addOn?.copy(id = doc.id)
                }
                onResult(addOnList)
            }
    }

    fun addAddOn(addOn: AddOn, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        val docRef = firestore.collection("addons").document()

        val newAddOn = addOn.copy(id = docRef.id)

        docRef.set(newAddOn)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onError(it)
            }
    }

    fun updateAddOn(addOn: AddOn, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        firestore.collection("addons").document(addOn.id)
            .set(addOn)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    fun deleteAddOn(addOn: AddOn, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        firestore.collection("addons").document(addOn.id)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }
}
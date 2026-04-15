package com.example.baksomanagementadmin.data.repository

import android.os.Bundle
import com.example.baksomanagementadmin.data.model.Menu
import com.example.baksomanagementadmin.data.remote.FirebaseClient
import com.example.baksomanagementadmin.ui.EditMenuFragment
import com.google.firebase.firestore.ktx.toObject

class MenuRepository {

    private val firestore = FirebaseClient.firestore

    fun getMenuList(onResult: (List<Menu>) -> Unit) {
        firestore.collection("Menu")
            .get()
            .addOnSuccessListener { result ->
                val menuList = result.documents.mapNotNull { doc ->
                    val menu = doc.toObject<Menu>()
                    menu?.copy(id = doc.id)
                }
                onResult(menuList)
            }
    }

    fun addMenu(menu: Menu, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        val docRef = firestore.collection("Menu").document()

        val newMenu = menu.copy(id = docRef.id)

        docRef.set(newMenu)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onError(it)
            }
    }

    fun getMenuById(id: String, onResult: (Menu?) -> Unit) {
        firestore.collection("Menu").document(id)
            .get()
            .addOnSuccessListener {
                val menu = it.toObject(Menu::class.java)?.copy(id = it.id)
                onResult(menu)
            }
    }

    fun updateMenu(menu: Menu, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        firestore.collection("Menu").document(menu.id)
            .set(menu)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }
}
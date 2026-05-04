package com.example.baksomanagementadmin.data.repository

import com.example.baksomanagementadmin.data.model.AdminOrderItem
import com.example.baksomanagementadmin.data.model.Order
import com.example.baksomanagementadmin.data.model.AddOn
import com.example.baksomanagementadmin.data.model.OrderItem
import com.example.baksomanagementadmin.data.remote.FirebaseClient

class OrderRepository {

    private val firestore = FirebaseClient.firestore

    fun getOrderById(id: String, onResult: (Order?) -> Unit) {
        firestore.collection("order").document(id)
            .get()
            .addOnSuccessListener {
                val order = it.toObject(Order::class.java)?.copy(id = it.id)
                onResult(order)
            }
    }

    fun createOrder(
        order: Order,
        items: List<OrderItem>,
        onSuccess: () -> Unit
    ) {
        val orderRef = firestore.collection("orders").document()

        val orderData = hashMapOf(
            "userID" to order.userID,
            "createdAt" to order.createdAt,
            "status" to order.status,
            "total" to order.total
        )

        orderRef.set(orderData)
            .addOnSuccessListener {

                val batch = firestore.batch()

                items.forEach { item ->
                    val itemRef = orderRef.collection("items").document()

                    val itemData = hashMapOf(
                        "menu_id" to item.menu_id,
                        "nama" to item.nama,
                        "harga" to item.harga,
                        "quantity" to item.quantity,
                        "catatan" to item.catatan,
                        "addons" to item.addons
                    )

                    batch.set(itemRef, itemData)
                }

                batch.commit().addOnSuccessListener {
                    onSuccess()
                }
            }
    }

    fun getAllOrderItems(
        onResult: (List<AdminOrderItem>) -> Unit
    ) {
        firestore.collection("orders")
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->

                val finalList = mutableListOf<AdminOrderItem>()
                val totalOrders = result.documents.size

                if (totalOrders == 0) {
                    onResult(emptyList())
                    return@addOnSuccessListener
                }

                var processedOrders = 0

                result.documents.forEach { doc ->

                    val orderId = doc.id
                    val userID = doc.getString("userID") ?: ""
                    val createdAt = doc.getLong("createdAt") ?: 0L

                    firestore.collection("orders")
                        .document(orderId)
                        .collection("items")
                        .get()
                        .addOnSuccessListener { items ->

                            items.documents.forEach { itemDoc ->

                                val nama = itemDoc.getString("nama") ?: ""
                                val qty = itemDoc.getLong("quantity")?.toInt() ?: 0
                                val harga = itemDoc.getLong("harga")?.toInt() ?: 0

                                val addons = itemDoc.get("addons") as? List<Map<String, Any>> ?: emptyList()

                                val addonList = addons.map {
                                    AddOn(
                                        id = it["id"].toString(),
                                        name = it["name"].toString(),
                                        price = (it["price"] as Long).toInt()
                                    )
                                }
                                val total = (harga + addonList.sumOf { it.price }) * qty
                                finalList.add(
                                    AdminOrderItem(
                                        orderId = orderId,
                                        userID = userID,
                                        createdAt = createdAt,
                                        nama = nama,
                                        addons = addonList,
                                        quantity = qty,
                                        total = total
                                    )
                                )
                            }
                            processedOrders++
                            if (processedOrders == totalOrders) {
                                onResult(finalList)
                            }
                        }
                }
            }
    }
}
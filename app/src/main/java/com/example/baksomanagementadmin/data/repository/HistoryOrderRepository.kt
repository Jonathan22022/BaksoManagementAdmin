package com.example.baksomanagementadmin.data.repository

import com.example.baksomanagementadmin.data.model.HistoryOrder
import com.example.baksomanagementadmin.data.model.OrderItem
import com.google.firebase.firestore.FirebaseFirestore

class HistoryOrderRepository {

    private val firestore = FirebaseFirestore.getInstance()

    fun getHistoryOrders(
        onResult: (List<HistoryOrder>) -> Unit
    ) {

        firestore.collection("orders")
            .whereIn(
                "status",
                listOf("selesai", "dibatalkan")
            )
            .get()
            .addOnSuccessListener { orders ->

                val historyList = mutableListOf<HistoryOrder>()

                if (orders.isEmpty) {
                    onResult(emptyList())
                    return@addOnSuccessListener
                }

                var processed = 0

                orders.documents.forEach { orderDoc ->

                    val orderId = orderDoc.id

                    val userID =
                        orderDoc.getString("userID") ?: ""

                    val createdAt =
                        orderDoc.getLong("createdAt") ?: 0L

                    val status =
                        orderDoc.getString("status") ?: ""

                    firestore.collection("orders")
                        .document(orderId)
                        .collection("items")
                        .get()
                        .addOnSuccessListener { items ->

                            items.documents.forEach { itemDoc ->

                                val history = HistoryOrder(
                                    orderId = orderId,
                                    userID = userID,
                                    nama = itemDoc.getString("nama") ?: "",
                                    imageUrl = itemDoc.getString("imageUrl") ?: "",
                                    createdAt = createdAt,
                                    status = status,
                                    quantity = itemDoc.getLong("quantity")?.toInt() ?: 0,
                                    total = itemDoc.getLong("harga")?.toInt() ?: 0
                                )

                                historyList.add(history)
                            }

                            processed++

                            if (processed == orders.size()) {
                                onResult(historyList)
                            }
                        }
                }
            }
    }

    fun getHistoryOrderItems(
        orderId: String,
        onResult: (List<OrderItem>) -> Unit
    ) {

        firestore.collection("orders")
            .document(orderId)
            .collection("items")
            .get()
            .addOnSuccessListener { result ->

                val items = result.documents.mapNotNull {

                    it.toObject(OrderItem::class.java)
                        ?.copy(id = it.id)
                }

                onResult(items)
            }
    }

    fun deleteHistoryOrder(
        orderId: String,
        onSuccess: () -> Unit
    ) {

        firestore.collection("orders")
            .document(orderId)
            .collection("items")
            .get()
            .addOnSuccessListener { items ->

                val batch = firestore.batch()

                items.documents.forEach {

                    batch.delete(it.reference)
                }

                batch.commit()
                    .addOnSuccessListener {

                        firestore.collection("orders")
                            .document(orderId)
                            .delete()
                            .addOnSuccessListener {

                                onSuccess()
                            }
                    }
            }
    }
}
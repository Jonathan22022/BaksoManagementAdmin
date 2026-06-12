package com.example.baksomanagementadmin.data.repository

import com.example.baksomanagementadmin.data.model.AdminOrderItem
import com.example.baksomanagementadmin.data.model.Order
import com.example.baksomanagementadmin.data.model.AddOn
import com.example.baksomanagementadmin.data.model.OrderItem
import com.example.baksomanagementadmin.data.model.User
import com.example.baksomanagementadmin.data.remote.FirebaseClient

class OrderRepository {

    private val firestore = FirebaseClient.firestore

    fun getOrderById(
        id: String,
        onResult: (Order?) -> Unit
    ) {
        firestore.collection("orders")
            .document(id)
            .get()
            .addOnSuccessListener {

                val order =
                    it.toObject(Order::class.java)
                        ?.copy(id = it.id)

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
                        "addons" to item.addons,
                        "imageUrl" to item.imageUrl
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
            .orderBy(
                "createdAt",
                com.google.firebase.firestore.Query.Direction.ASCENDING
            )
            .get()
            .addOnSuccessListener { result ->

                val finalList =
                    mutableListOf<AdminOrderItem>()

                val docs =
                    result.documents.filter {

                        val status =
                            it.getString("status")
                                ?: "pending"

                        status != "selesai" &&
                                status != "cancel"
                    }

                if (docs.isEmpty()) {
                    onResult(emptyList())
                    return@addOnSuccessListener
                }

                var processed = 0

                docs.forEach { doc ->

                    val orderId = doc.id

                    val userID =
                        doc.getString("userID")
                            ?: ""

                    val createdAt =
                        doc.getLong("createdAt")
                            ?: 0L

                    val status =
                        doc.getString("status")
                            ?: "pending"

                    firestore.collection("orders")
                        .document(orderId)
                        .collection("items")
                        .get()
                        .addOnSuccessListener { items ->

                            val itemCount =
                                items.size()

                            val firstItem =
                                items.documents.firstOrNull()

                            if (firstItem != null) {

                                val nama =
                                    firstItem.getString("nama")
                                        ?: ""

                                val imageUrl =
                                    firstItem.getString("imageUrl")
                                        ?: ""

                                val qty =
                                    firstItem.getLong("quantity")
                                        ?.toInt()
                                        ?: 0

                                val harga =
                                    firstItem.getLong("harga")
                                        ?.toInt()
                                        ?: 0

                                val addonsRaw =
                                    firstItem.get("addons")
                                            as? List<Map<String, Any>>
                                        ?: emptyList()

                                val addonList =
                                    addonsRaw.map {

                                        AddOn(
                                            id = it["id"].toString(),
                                            name = it["name"].toString(),
                                            price = (
                                                    it["price"] as Long
                                                    ).toInt()
                                        )
                                    }

                                val total =
                                    (
                                            harga +
                                                    addonList.sumOf {
                                                        it.price
                                                    }
                                            ) * qty

                                finalList.add(
                                    AdminOrderItem(
                                        orderId = orderId,
                                        userID = userID,
                                        createdAt = createdAt,
                                        nama = nama,
                                        imageUrl = imageUrl,
                                        addons = addonList,
                                        quantity = qty,
                                        total = total,
                                        status = status,
                                        itemCount = itemCount
                                    )
                                )
                            }

                            processed++

                            if (processed == docs.size) {

                                onResult(
                                    finalList.sortedByDescending {
                                        it.createdAt
                                    }
                                )
                            }
                        }
                }
            }
    }

    fun getOrderDetail(
        orderId: String,
        onResult: (
            Order?,
            List<OrderItem>
        ) -> Unit
    ) {

        firestore.collection("orders")
            .document(orderId)
            .get()
            .addOnSuccessListener { orderDoc ->

                val order =
                    orderDoc.toObject(Order::class.java)
                        ?.copy(id = orderDoc.id)

                firestore.collection("orders")
                    .document(orderId)
                    .collection("items")
                    .get()
                    .addOnSuccessListener { itemDocs ->

                        val items =
                            itemDocs.toObjects(
                                OrderItem::class.java
                            )

                        onResult(order, items)
                    }
            }
    }

    fun updateOrderStatus(
        orderId: String,
        status: String,
        onSuccess: () -> Unit
    ) {

        firestore.collection("orders")
            .document(orderId)
            .update(
                "status",
                status
            )
            .addOnSuccessListener {
                onSuccess()
            }
    }

    fun completeOrder(
        orderId: String,
        onSuccess: () -> Unit
    ) {

        firestore.collection("orders")
            .document(orderId)
            .update(
                mapOf(
                    "status" to "selesai",
                    "completed" to true
                )
            )
            .addOnSuccessListener {
                onSuccess()
            }
    }

    fun cancelOrder(
        orderId: String,
        onSuccess: () -> Unit
    ) {

        firestore.collection("orders")
            .document(orderId)
            .update(
                mapOf(
                    "status" to "cancel"
                )
            )
            .addOnSuccessListener {
                onSuccess()
            }
    }
}
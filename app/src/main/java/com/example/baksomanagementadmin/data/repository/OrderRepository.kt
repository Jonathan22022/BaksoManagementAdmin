package com.example.baksomanagementadmin.data.repository

import android.util.Log
import com.example.baksomanagementadmin.data.model.AdminOrderItem
import com.example.baksomanagementadmin.data.model.Order
import com.example.baksomanagementadmin.data.model.AddOn
import com.example.baksomanagementadmin.data.model.OrderItem
import com.example.baksomanagementadmin.data.model.User
import com.example.baksomanagementadmin.data.remote.FirebaseClient

class OrderRepository {

    private val firestore = FirebaseClient.firestore
    private val menuRef = firestore.collection("bakso")
    private val bahanRef = firestore.collection("bahanbaku")
    private val addonRef = firestore.collection("addons")

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

                    val pickupType =
                        doc.getString("pickupType")
                            ?: "dine_in"

                    val deliveryAddress =
                        doc.getString("deliveryAddress")
                            ?: ""

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
                                        itemCount = itemCount,
                                        pickupType = pickupType,
                                        deliveryAddress = deliveryAddress
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

        Log.d(
            "ADMIN_ORDER_DEBUG",
            """
        UPDATE STATUS DIPANGGIL
        orderId = $orderId
        status  = $status
        """.trimIndent()
        )

        firestore.collection("orders")
            .document(orderId)
            .update(
                "status",
                status
            )
            .addOnSuccessListener {

                Log.d(
                    "ADMIN_ORDER_DEBUG",
                    "BERHASIL UPDATE STATUS"
                )

                onSuccess()
            }
            .addOnFailureListener {

                Log.e(
                    "ADMIN_ORDER_DEBUG",
                    "GAGAL UPDATE STATUS",
                    it
                )
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

        Log.d(
            "ADMIN_ORDER_DEBUG",
            """
        CANCEL ORDER
        orderId = $orderId
        """.trimIndent()
        )

        firestore.collection("orders")
            .document(orderId)
            .update(
                mapOf(
                    "status" to "cancel"
                )
            )
            .addOnSuccessListener {

                Log.d(
                    "ADMIN_ORDER_DEBUG",
                    "ORDER BERHASIL DICANCEL"
                )

                onSuccess()
            }
            .addOnFailureListener {

                Log.e(
                    "ADMIN_ORDER_DEBUG",
                    "GAGAL CANCEL ORDER",
                    it
                )
            }
    }

    fun gunakanBahanPesanan(
        orderId: String,
        onSuccess: () -> Unit
    ) {
        Log.d(
            "STOK_DEBUG",
            "gunakanBahanPesanan() dipanggil"
        )

        Log.d(
            "STOK_DEBUG",
            "orderId = $orderId"
        )
        firestore.collection("orders")
            .document(orderId)
            .collection("items")
            .get()
            .addOnSuccessListener { itemDocs ->
                Log.d(
                    "STOK_DEBUG",
                    "Jumlah item = ${itemDocs.size()}"
                )
                val batch =
                    firestore.batch()

                var totalTask = 0
                var selesaiTask = 0

                val bahanDipakai =
                    mutableMapOf<String, Double>()

                itemDocs.toObjects(
                    OrderItem::class.java
                ).forEach { item ->
                    Log.d(
                        "STOK_DEBUG",
                        """
                        MENU
                            menu_id=${item.menu_id}
                            nama=${item.nama}
                            qty=${item.quantity}
                            addon=${item.addons.size}
                            """.trimIndent()
                    )
                    totalTask++

                    menuRef.document(item.menu_id)
                        .get()
                        .addOnSuccessListener { menuDoc ->

                            val bahanList =
                                menuDoc.get("bahanList")
                                        as? List<Map<String, Any>>
                                    ?: emptyList()

                            bahanList.forEach { bahan ->

                                val bahanId =
                                    bahan["bahanId"].toString()

                                val jumlah =
                                    (bahan["jumlah"] as Number)
                                        .toDouble()
                                Log.d(
                                    "STOK_DEBUG",
                                    """
                                    BAHAN MENU
                                    bahanId=$bahanId
                                    jumlah=$jumlah
                                    qtyOrder=${item.quantity}
                                    total=${jumlah * item.quantity}
                                    """.trimIndent()
                                )

                                bahanDipakai[bahanId] =
                                    (bahanDipakai[bahanId] ?: 0.0) +
                                            jumlah * item.quantity
                            }

                            selesaiTask++

                            if (selesaiTask == totalTask) {

                                prosesAddon(
                                    itemDocs,
                                    bahanDipakai,
                                    batch,
                                    onSuccess
                                )
                            }
                        }
                }
            }
    }

    fun observeOrderStatus(
        orderId: String,
        onChanged: (String) -> Unit
    ) {

        firestore.collection("orders")
            .document(orderId)
            .addSnapshotListener { snapshot, _ ->

                if (snapshot != null && snapshot.exists()) {

                    val status =
                        snapshot.getString("status")
                            ?: "pending"

                    onChanged(status)
                }
            }
    }

    private fun prosesAddon(
        itemDocs: com.google.firebase.firestore.QuerySnapshot,
        bahanDipakai: MutableMap<String, Double>,
        batch: com.google.firebase.firestore.WriteBatch,
        onSuccess: () -> Unit
    ) {

        val items =
            itemDocs.toObjects(
                OrderItem::class.java
            )

        var totalAddon = 0
        var selesaiAddon = 0

        items.forEach { item ->

            item.addons.forEach { addon ->

                totalAddon++

                firestore.collection("addons")
                    .document(addon.id)
                    .get()
                    .addOnSuccessListener { addonDoc ->

                        val bahanList =
                            addonDoc.get("bahanList")
                                    as? List<Map<String, Any>>
                                ?: emptyList()

                        bahanList.forEach { bahan ->

                            val bahanId =
                                bahan["bahanId"].toString()

                            val jumlah =
                                (bahan["jumlah"] as Number)
                                    .toDouble()

                            bahanDipakai[bahanId] =
                                (bahanDipakai[bahanId] ?: 0.0) +
                                        jumlah
                        }

                        selesaiAddon++

                        if (selesaiAddon == totalAddon) {
                            updateStock(
                                bahanDipakai,
                                batch,
                                onSuccess
                            )
                        }
                    }
            }
        }

        if (totalAddon == 0) {
            updateStock(
                bahanDipakai,
                batch,
                onSuccess
            )
        }
    }

    private fun updateStock(
        bahanDipakai: Map<String, Double>,
        batch: com.google.firebase.firestore.WriteBatch,
        onSuccess: () -> Unit
    ) {

        firestore.collection("bahanbaku")
            .get()
            .addOnSuccessListener { bahanDocs ->

                bahanDocs.forEach { doc ->

                    val bahanId = doc.id

                    val dipakai =
                        bahanDipakai[bahanId]
                            ?: return@forEach

                    val stok =
                        doc.getDouble("berat")
                            ?: 0.0

                    batch.update(
                        doc.reference,
                        "berat",
                        stok - dipakai
                    )
                }

                batch.commit()
                    .addOnSuccessListener {
                        onSuccess()
                    }
            }
    }
}
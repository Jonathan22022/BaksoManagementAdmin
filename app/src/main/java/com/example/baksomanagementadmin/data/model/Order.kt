package com.example.baksomanagementadmin.data.model

//orders: {
//    orderId1: {
//        created_at: "...",
//        status: "pending",
//        items: {
//        item1: {
//        menu_id: "1",
//        nama: "Bakso",
//        harga: 10000,
//        quantity: 2,
//        catatan: "pedas",
//        addons: [
//        { id: "1", nama: "Keju", harga: 3000 },
//        { id: "2", nama: "Sambal", harga: 2000 }
//        ]
//    }
//    }
//    }
//}

data class Order(
    val id: String = "",
    val userID: String = "", //user mana yang melakukan pemesanan
    val createdAt: Long = System.currentTimeMillis(),
    val total: Int = 0, //total harga pesanan
    val status: String = "pending",
)

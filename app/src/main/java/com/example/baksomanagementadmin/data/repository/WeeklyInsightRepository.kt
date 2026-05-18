package com.example.baksomanagementadmin.data.repository

import com.example.baksomanagementadmin.data.model.WeeklyInsight
import com.example.baksomanagementadmin.data.remote.FirebaseClient
import java.util.Calendar

class WeeklyInsightRepository {

    private val firestore = FirebaseClient.firestore

    fun getWeeklyInsight(
        startMillis: Long,
        endMillis: Long,
        onResult: (WeeklyInsight) -> Unit
    ) {

        firestore.collection("orders")
            .whereEqualTo("status", "selesai")
            .get()
            .addOnSuccessListener { result ->

                val incomeMap = mutableMapOf(
                    "Sen" to 0f,
                    "Sel" to 0f,
                    "Rab" to 0f,
                    "Kam" to 0f,
                    "Jum" to 0f,
                    "Sab" to 0f,
                    "Min" to 0f
                )

                val expenseMap = mutableMapOf(
                    "Sen" to 0f,
                    "Sel" to 0f,
                    "Rab" to 0f,
                    "Kam" to 0f,
                    "Jum" to 0f,
                    "Sab" to 0f,
                    "Min" to 0f
                )

                var totalIncome = 0
                var totalExpense = 0

                val menuCounter = mutableMapOf<String, Int>()

                val filteredOrders = result.documents.filter {

                    val createdAt =
                        it.getLong("createdAt") ?: 0L

                    createdAt in startMillis..endMillis
                }

                if (filteredOrders.isEmpty()) {

                    loadExpenseData(
                        startMillis,
                        endMillis,
                        expenseMap
                    ) { expenseTotal ->

                        onResult(
                            WeeklyInsight(
                                dailyIncome = incomeMap,
                                dailyExpense = expenseMap,
                                totalIncome = 0,
                                totalExpense = expenseTotal
                            )
                        )
                    }

                    return@addOnSuccessListener
                }

                var processed = 0

                filteredOrders.forEach { orderDoc ->

                    val total =
                        orderDoc.getLong("total")?.toInt() ?: 0

                    val createdAt =
                        orderDoc.getLong("createdAt") ?: 0L

                    totalIncome += total

                    val dayName = getDayName(createdAt)

                    incomeMap[dayName] =
                        incomeMap[dayName]!! + total.toFloat()

                    firestore.collection("orders")
                        .document(orderDoc.id)
                        .collection("items")
                        .get()
                        .addOnSuccessListener { itemResult ->

                            itemResult.documents.forEach { item ->

                                val nama =
                                    item.getString("nama") ?: "-"

                                val qty =
                                    item.getLong("quantity")?.toInt() ?: 0

                                menuCounter[nama] =
                                    (menuCounter[nama] ?: 0) + qty
                            }

                            processed++

                            if (processed == filteredOrders.size) {

                                loadExpenseData(
                                    startMillis,
                                    endMillis,
                                    expenseMap
                                ) { expenseTotal ->

                                    totalExpense = expenseTotal

                                    val bestMenu =
                                        menuCounter.maxByOrNull {
                                            it.value
                                        }

                                    onResult(
                                        WeeklyInsight(
                                            dailyIncome = incomeMap,
                                            dailyExpense = expenseMap,
                                            totalIncome = totalIncome,
                                            totalExpense = totalExpense,
                                            bestMenu = bestMenu?.key ?: "-",
                                            totalBestMenuOrdered =
                                                bestMenu?.value ?: 0
                                        )
                                    )
                                }
                            }
                        }
                }
            }
    }

    private fun loadExpenseData(
        startMillis: Long,
        endMillis: Long,
        expenseMap: MutableMap<String, Float>,
        onComplete: (Int) -> Unit
    ) {

        firestore.collection("bahanbaku")
            .get()
            .addOnSuccessListener { result ->

                var totalExpense = 0

                result.documents.forEach { doc ->

                    val harga =
                        doc.getLong("harga")?.toInt() ?: 0

                    val createdAt =
                        doc.getLong("createdAt") ?: 0L

                    if (createdAt in startMillis..endMillis) {

                        totalExpense += harga

                        val dayName = getDayName(createdAt)

                        expenseMap[dayName] =
                            expenseMap[dayName]!! + harga.toFloat()
                    }
                }

                onComplete(totalExpense)
            }
    }

    private fun getDayName(timeMillis: Long): String {

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeMillis

        return when (
            calendar.get(Calendar.DAY_OF_WEEK)
        ) {
            Calendar.MONDAY -> "Sen"
            Calendar.TUESDAY -> "Sel"
            Calendar.WEDNESDAY -> "Rab"
            Calendar.THURSDAY -> "Kam"
            Calendar.FRIDAY -> "Jum"
            Calendar.SATURDAY -> "Sab"
            else -> "Min"
        }
    }
}
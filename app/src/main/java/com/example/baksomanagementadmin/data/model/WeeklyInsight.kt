package com.example.baksomanagementadmin.data.model

data class WeeklyInsight(
    val dailyIncome: MutableMap<String, Float> = mutableMapOf(),
    val dailyExpense: MutableMap<String, Float> = mutableMapOf(),
    val totalModalAwal: Int = 0,
    val totalIncome: Int = 0,
    val totalExpense: Int = 0,
    val bestMenu: String = "-",
    val totalBestMenuOrdered: Int = 0
)
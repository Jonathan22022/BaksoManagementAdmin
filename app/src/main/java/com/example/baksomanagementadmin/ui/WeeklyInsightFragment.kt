package com.example.baksomanagementadmin.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.baksomanagementadmin.R
import com.example.baksomanagementadmin.data.repository.WeeklyInsightRepository
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class WeeklyInsightFragment : Fragment() {

    private lateinit var etWeek: EditText
    private lateinit var barChart: BarChart
    private lateinit var tvIncome: TextView
    private lateinit var tvBestMenu: TextView
    private lateinit var tvExpense: TextView

    private val repository = WeeklyInsightRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(
            R.layout.fragment_weekly_insight,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        etWeek = view.findViewById(R.id.etWeek)
        barChart = view.findViewById(R.id.barChart)
        tvIncome = view.findViewById(R.id.tvIncome)
        tvBestMenu = view.findViewById(R.id.tvBestMenu)
        tvExpense = view.findViewById(R.id.tvExpense)
        loadCurrentWeek()

        etWeek.setOnClickListener {
            pickWeek()
        }
    }

    private fun pickWeek() {

        val calendar = Calendar.getInstance()

        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->

                val selected = Calendar.getInstance()

                selected.set(year, month, day)

                loadWeek(selected)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun loadCurrentWeek() {

        val calendar = Calendar.getInstance()

        loadWeek(calendar)
    }

    private fun loadWeek(calendar: Calendar) {

        val start = calendar.clone() as Calendar

        start.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        start.set(Calendar.HOUR_OF_DAY, 0)
        start.set(Calendar.MINUTE, 0)
        start.set(Calendar.SECOND, 0)

        val end = start.clone() as Calendar

        end.add(Calendar.DAY_OF_MONTH, 6)

        val sdf = SimpleDateFormat(
            "dd MMM yyyy",
            Locale("id", "ID")
        )

        etWeek.setText(
            "${sdf.format(start.time)} - ${sdf.format(end.time)}"
        )

        repository.getWeeklyInsight(
            start.timeInMillis,
            end.timeInMillis
        ) { insight ->

            tvIncome.text =
                "Total Pendapatan : Rp ${insight.totalIncome}"

            tvExpense.text =
                "Total Pengeluaran : Rp ${insight.totalExpense}"

            tvBestMenu.text =
                "Menu Terlaris : ${insight.bestMenu} (${insight.totalBestMenuOrdered}x)"

            setupChart(
                insight.dailyIncome,
                insight.dailyExpense
            )
        }
    }

    private fun setupChart(
        incomeMap: MutableMap<String, Float>,
        expenseMap: MutableMap<String, Float>
    ) {

        val incomeEntries = arrayListOf<BarEntry>()
        val expenseEntries = arrayListOf<BarEntry>()

        val labels = arrayListOf<String>()

        var index = 0f

        incomeMap.forEach { (day, incomeValue) ->

            val expenseValue =
                expenseMap[day] ?: 0f

            incomeEntries.add(
                BarEntry(index, incomeValue)
            )

            expenseEntries.add(
                BarEntry(index, expenseValue)
            )

            labels.add(day)

            index++
        }

        val incomeDataSet =
            BarDataSet(incomeEntries, "Pendapatan")

        val expenseDataSet =
            BarDataSet(expenseEntries, "Pengeluaran")

        val barData =
            BarData(incomeDataSet, expenseDataSet)

        barData.barWidth = 0.3f

        barChart.data = barData

        barChart.xAxis.valueFormatter =
            IndexAxisValueFormatter(labels)

        barChart.xAxis.granularity = 1f

        barChart.groupBars(
            0f,
            0.2f,
            0.02f
        )

        barChart.description.isEnabled = false

        barChart.invalidate()
    }
}
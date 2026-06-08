package com.example.baksomanagementadmin.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.content.res.Configuration
import android.graphics.Color
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
    private var startDateMillis = 0L
    private var endDateMillis = 0L

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
            pickDateRange()
        }
    }

    private fun isDarkMode(): Boolean {
        return resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK ==
                Configuration.UI_MODE_NIGHT_YES
    }

    private fun pickDateRange() {

        val calendar = Calendar.getInstance()

        DatePickerDialog(
            requireContext(),
            { _, startYear, startMonth, startDay ->

                val startCalendar = Calendar.getInstance()

                startCalendar.set(
                    startYear,
                    startMonth,
                    startDay,
                    0,
                    0,
                    0
                )

                DatePickerDialog(
                    requireContext(),
                    { _, endYear, endMonth, endDay ->

                        val endCalendar = Calendar.getInstance()

                        endCalendar.set(
                            endYear,
                            endMonth,
                            endDay,
                            23,
                            59,
                            59
                        )

                        // VALIDASI
                        if (endCalendar.before(startCalendar)) {

                            androidx.appcompat.app.AlertDialog.Builder(
                                requireContext()
                            )
                                .setTitle("Range Tidak Valid")
                                .setMessage(
                                    "Tanggal akhir tidak boleh lebih kecil dari tanggal awal."
                                )
                                .setPositiveButton("OK", null)
                                .show()

                            return@DatePickerDialog
                        }

                        val diffDays =
                            (endCalendar.timeInMillis -
                                    startCalendar.timeInMillis) / (1000 * 60 * 60 * 24)

                        if (diffDays > 6) {

                            androidx.appcompat.app.AlertDialog.Builder(
                                requireContext()
                            )
                                .setTitle("Range Maksimal")
                                .setMessage(
                                    "Range hanya boleh maksimal 7 hari.\n\nContoh:\n8 Juni - 14 Juni"
                                )
                                .setPositiveButton("OK", null)
                                .show()

                            return@DatePickerDialog
                        }

                        startDateMillis =
                            startCalendar.timeInMillis

                        endDateMillis =
                            endCalendar.timeInMillis

                        val sdf = SimpleDateFormat(
                            "dd MMM yyyy",
                            Locale("id", "ID")
                        )

                        etWeek.setText(
                            "${sdf.format(startCalendar.time)} - ${
                                sdf.format(endCalendar.time)
                            }"
                        )

                        loadInsightData()

                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()

            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun loadInsightData() {

        repository.getWeeklyInsight(
            startDateMillis,
            endDateMillis
        ) { insight ->

            requireActivity().runOnUiThread {

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

        val isDark =
            resources.configuration.uiMode and
                    android.content.res.Configuration.UI_MODE_NIGHT_MASK ==
                    android.content.res.Configuration.UI_MODE_NIGHT_YES

        val chartTextColor =
            if (isDark)
                android.graphics.Color.WHITE
            else
                android.graphics.Color.BLACK

        val labels = incomeMap.keys.toList()

        val incomeEntries = ArrayList<BarEntry>()
        val expenseEntries = ArrayList<BarEntry>()

        labels.forEachIndexed { index, day ->

            incomeEntries.add(
                BarEntry(
                    index.toFloat(),
                    incomeMap[day] ?: 0f
                )
            )

            expenseEntries.add(
                BarEntry(
                    index.toFloat(),
                    expenseMap[day] ?: 0f
                )
            )
        }

        val incomeDataSet =
            BarDataSet(
                incomeEntries,
                "Pendapatan"
            )

        incomeDataSet.color =
            Color.parseColor("#4CAF50")

        val expenseDataSet =
            BarDataSet(
                expenseEntries,
                "Pengeluaran"
            )

        expenseDataSet.color =
            Color.parseColor("#E53935")

        var textColor =
            if (isDarkMode())
                Color.WHITE
            else
                Color.BLACK

        incomeDataSet.valueTextColor =
            textColor

        expenseDataSet.valueTextColor =
            textColor

        incomeDataSet.valueTextSize = 10f
        expenseDataSet.valueTextSize = 10f

        incomeDataSet.valueTextColor =
            chartTextColor

        expenseDataSet.valueTextColor =
            chartTextColor

        val data =
            BarData(
                incomeDataSet,
                expenseDataSet
            )

        data.barWidth = 0.35f

        barChart.data = data

        // X AXIS
        barChart.xAxis.apply {

            valueFormatter =
                IndexAxisValueFormatter(labels)

            granularity = 1f

            setCenterAxisLabels(true)

            position =
                com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM

            textSize = 12f

            textColor = chartTextColor

            setDrawGridLines(false)

            axisLineColor = chartTextColor
        }

        // LEFT AXIS
        barChart.axisLeft.apply {

            textSize = 11f

            textColor = chartTextColor

            axisLineColor = chartTextColor

            setDrawGridLines(true)
        }

        // RIGHT AXIS
        barChart.axisRight.isEnabled = false

        // LEGEND
        barChart.legend.apply {

            isEnabled = true

            textSize = 12f

            formSize = 12f

            textColor = chartTextColor
        }

        // DESCRIPTION
        barChart.description.isEnabled = false

        val groupSpace = 0.1f
        val barSpace = 0.05f

        barChart.groupBars(
            0f,
            groupSpace,
            barSpace
        )

        barChart.animateY(800)

        barChart.invalidate()
    }
}
package com.yurazhovnir.myfeeling.ui.main.analytiic.steps

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.yurazhovnir.myfeeling.R
import com.yurazhovnir.myfeeling.dp
import com.yurazhovnir.myfeeling.base.BaseFragment
import com.yurazhovnir.myfeeling.base.RoundedBarChartTime
import com.yurazhovnir.myfeeling.databinding.FragmentAnalyticStepsBinding
import com.yurazhovnir.myfeeling.model.Filing
//import io.realm.kotlin.ext.copyFromRealm
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Locale

private const val BAR_CHART_VIEW_ID = 123456

class AnalyticStepsFragment :
    BaseFragment<FragmentAnalyticStepsBinding>(FragmentAnalyticStepsBinding::inflate){
    override val TAG: String
        get() = "AnalyticStepsFragment"


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.clickable = this
//        setupMonthLabels()
//        setupMonthChart()
    }
//    private fun setupMonthBarChart(monthProjectCounts: Map<String, Int>) {
//        val maxScore = monthProjectCounts.values.maxOrNull() ?: 1
//        val averageScore = if (monthProjectCounts.isNotEmpty()) {
//            monthProjectCounts.values.sum().toDouble() / monthProjectCounts.size
//        } else {
//            0.0
//        }
//
//        binding.chartMonthLayout.removeAllViews()
//        RoundedBarChartTime(requireContext()).apply {
//            id = BAR_CHART_VIEW_ID + 1 // Ensure unique ID
//            setupNewChart(this, monthProjectCounts, maxScore, averageScore)
//            binding.chartMonthLayout.addView(this)
//            (layoutParams as? LinearLayout.LayoutParams)?.height = 180.dp
//        }
//    }
//    private fun setupMonthChart() {
//        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
//        val outputDateFormat = SimpleDateFormat("d", Locale.ENGLISH) // Day of month
//        val calendar = Calendar.getInstance()
//
//        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
//        val monthDays = (1..daysInMonth).map { it.toString() }
//
//        val monthCounts = mutableMapOf<String, Int>()
//
////        monthDays.forEach { day ->
////            val dayFilings = filings?.filter { filing ->
////                try {
////                    val filingDate = filing.startsAt?.let { inputDateFormat.parse(it) }
////                    filingDate?.let {
////                        val cal = Calendar.getInstance()
////                        cal.time = it
////                        cal.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
////                                cal.get(Calendar.DAY_OF_MONTH).toString() == day
////                    } ?: false
////                } catch (e: Exception) {
////                    false
////                }
////            }
//
//            val totalSteps = dayFilings?.sumOf { it.steps ?: 0 } ?: 0
//            monthCounts[day] = totalSteps
//        }
//
//        setupMonthBarChart(monthCounts)
//    }
//    private fun setupMonthLabels() {
//        val filings = DatabaseHelper(requireContext()).getAllFilings()
//
//        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
//        val outputDateFormat = SimpleDateFormat("EEE", Locale.ENGLISH)
//
//        val sharedPreferences =
//            requireActivity().getSharedPreferences("app_prefs", AppCompatActivity.MODE_PRIVATE)
//        val selectedStartDayString = sharedPreferences.getString("selected_start_day", null)
//
//        val selectedStartDay = when (selectedStartDayString) {
//            "Monday" -> Calendar.MONDAY
//            "Saturday" -> Calendar.SATURDAY
//            "Sunday" -> Calendar.SUNDAY
//            else -> Calendar.MONDAY
//        }
//        val calendar = Calendar.getInstance()
//        calendar.firstDayOfWeek = selectedStartDay
//        calendar.set(Calendar.DAY_OF_WEEK, selectedStartDay)
//
//        val currentWeekDays = mutableListOf<String>()
//        for (i in 0 until 7) {
//            currentWeekDays.add(outputDateFormat.format(calendar.time).uppercase(Locale.ENGLISH))
//            calendar.add(Calendar.DAY_OF_WEEK, 1)
//        }
//
//        val weekCounts = mutableMapOf<String, Int>()
//
//        currentWeekDays.forEach { weekDay ->
//            val dayFilings = filings?.filter { filing ->
//                try {
//                    val filingDate = filing.startsAt?.let { inputDateFormat.parse(it) }
//                    filingDate?.let {
//                        outputDateFormat.format(it).uppercase(Locale.ENGLISH) == weekDay
//                    } ?: false
//                } catch (e: Exception) {
//                    false
//                }
//            }
//
//            val totalSteps = dayFilings?.sumOf { filing ->
//                filing.steps ?: 0
//            } ?: 0
//            weekCounts[weekDay] = totalSteps
//        }
//        setupBarChart(weekCounts)
//    }
//
//    private fun setupBarChart(weekProjectCounts: Map<String, Int>) {
//        val maxScore = weekProjectCounts.values.maxOrNull() ?: 1
//        val averageScore = if (weekProjectCounts.isNotEmpty()) {
//            weekProjectCounts.values.sum().toDouble() / weekProjectCounts.size
//        } else {
//            0.0
//        }
//
//        binding.chartWeekLayout.removeAllViews()
//        RoundedBarChartTime(context ?: return).apply {
//            id = BAR_CHART_VIEW_ID
//            setupNewChart(this, weekProjectCounts, maxScore, averageScore)
//            binding.chartWeekLayout.addView(this)
//            (layoutParams as? LinearLayout.LayoutParams)?.height = 180.dp
//        }
//    }
//
//    override fun onDestroyView() {
//        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
//        super.onDestroyView()
//    }
//    private fun RoundedBarChartTime.setupNewChart(
//        chart: RoundedBarChartTime,
//        weekProjectCounts: Map<String, Int>,
//        maxScore: Int,
//        averageScore: Double,
//    ) {
//        chart.apply {
//            description?.isEnabled = false
//            legend?.isEnabled = false
//            isDoubleTapToZoomEnabled = false
//            isHorizontalScrollBarEnabled = false
//            isDragEnabled = true
//            setTouchEnabled(true)
//            setDrawBorders(false)
//            setScaleEnabled(false)
//            setPinchZoom(false)
//            setDrawGridBackground(false)
//            setDrawValueAboveBar(true)
//            animateY(1000)
//            setRadius(9)
//            setBackgroundColor(Color.TRANSPARENT)
//
//            axisLeft.apply {
//                isEnabled = true
//                valueFormatter = object : ValueFormatter() {
//                    override fun getFormattedValue(value: Float): String {
//                        return when (value.toInt()) {
//                            0, averageScore.toInt(), maxScore -> "${value.toInt()}h"
//                            else -> ""
//                        }
//                    }
//                }
//                axisMinimum = 0f
//                axisMaximum = maxScore.toFloat().plus(100)
//                setDrawGridLines(false)
//                textColor = resources.getColor(R.color.text_icon_secondary)
//                axisLineColor = Color.TRANSPARENT
//                gridColor = Color.TRANSPARENT
//            }
//
//            xAxis.apply {
//                isEnabled = true
//                setDrawGridLines(false)
//                valueFormatter = IndexAxisValueFormatter(weekProjectCounts.keys.toTypedArray())
//                position = XAxis.XAxisPosition.BOTTOM
//                granularity = 1f
//                labelCount = 7
//                axisLineColor = Color.TRANSPARENT
//                gridColor = Color.TRANSPARENT
//                textColor = resources.getColor(R.color.text_icon_secondary)
//                typeface = Typeface.DEFAULT
//                setDrawAxisLine(false)
//            }
//            axisRight.isEnabled = false
//
////            marker = CustomFocusMarkerView(
////                requireContext(),
////                R.layout.focus_marker_view
////            )
//
//            data = createBarData(weekProjectCounts, maxScore)
//        }
//    }
//
//    private fun createBarData(
//        weekProjectCounts: Map<String, Int>,
//        maxScore: Int,
//    ): BarData {
//        val filledValues = ArrayList<BarEntry>()
//        val emptyValues = ArrayList<BarEntry>()
//
//        weekProjectCounts.values.forEachIndexed { index, count ->
//            if (count > 0) {
//                filledValues.add(BarEntry(index.toFloat(), count.toFloat()))
//            } else {
//                emptyValues.add(BarEntry(index.toFloat(), 0f))
//            }
//        }
//
//        val filledBarDataSet = BarDataSet(filledValues, "Filled Data").apply {
//            setGradientColor(
//                ContextCompat.getColor(requireContext(), R.color.background_brand_tertiary),
//                ContextCompat.getColor(requireContext(), R.color.border_primary)
//            )
//            valueTextColor = ContextCompat.getColor(requireContext(), R.color.text_icon_primary)
//            setDrawValues(false)
//            barShadowColor = ContextCompat.getColor(requireContext(), R.color.transparent)
//        }
//
//        val emptyBarDataSet = BarDataSet(emptyValues, "Empty Data").apply {
//            setGradientColor(
//                ContextCompat.getColor(requireContext(), R.color.border_primary),
//                ContextCompat.getColor(requireContext(), R.color.background_secondary)
//            )
//            color = ContextCompat.getColor(requireContext(), R.color.border_primary)
//            valueTextColor = ContextCompat.getColor(requireContext(), R.color.text_icon_primary)
//            setDrawValues(false)
//            barShadowColor = ContextCompat.getColor(requireContext(), R.color.transparent)
//        }
//
//        return BarData(filledBarDataSet, emptyBarDataSet).apply {
//            barWidth = 0.7f
//        }
//    }
//
//
//    @SuppressLint("ViewConstructor")
//    inner class CustomFocusMarkerView(
//        context: Context,
//        layoutResource: Int,
//    ) : MarkerView(context, layoutResource) {
////        private val date: TextView = findViewById(R.id.dateText)
//        private val time: TextView = findViewById(R.id.timeText)
//
//        @SuppressLint("SetTextI18n")
//        override fun refreshContent(e: Entry?, highlight: Highlight?) {
//            e?.let {
//
//                val selectedBarIndex = e.x.toInt()
////                val selectedMonth = weeklyProjectCounts.keys.toList().getOrNull(selectedBarIndex)
//
////                val totalMinutes = weeklyProjectCounts[selectedMonth] ?: 0
////                val hours = totalMinutes / 60
////                val minutes = totalMinutes % 60
////                date.text = "$selectedMonth"
////                time.text = "${hours}h ${minutes}m"
//
//            }
//            super.refreshContent(e, highlight)
//        }
//
//        override fun getOffset(): MPPointF {
//            return MPPointF(-(width / 2f), -height.toFloat())
//        }
//    }
}
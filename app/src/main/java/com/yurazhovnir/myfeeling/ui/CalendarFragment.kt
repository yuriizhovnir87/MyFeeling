package com.yurazhovnir.myfeeling.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import com.yurazhovnir.myfeeling.R
import com.yurazhovnir.myfeeling.base.BaseFragment
import com.yurazhovnir.myfeeling.model.Filing
import com.yurazhovnir.myfeeling.databinding.CalendarViewBinding
import com.yurazhovnir.myfeeling.databinding.CalendarViewHeaderBinding
import com.yurazhovnir.myfeeling.databinding.FragmentCalendarBinding
import com.yurazhovnir.myfeeling.databinding.ItemFillBinding
import com.yurazhovnir.myfeeling.helper.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class CalendarFragment : BaseFragment<FragmentCalendarBinding>(FragmentCalendarBinding::inflate) {

    override val TAG: String get() = "CalendarFragment"

    private val calendarProjectAdapter = CalendarFilingAdapter()
    private var selectedDateLocal: LocalDate? = null
    private var selectedDate: LocalDateTime? = LocalDateTime.now()
    private lateinit var currentMonth: YearMonth
    private val currentDate: LocalDate = LocalDate.now()
    private val startOfWeekDay: DayOfWeek = DayOfWeek.MONDAY

    private val databaseHelper by lazy { AppDatabase.getDatabase(requireContext()).filingDao() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        loadFilings()
        currentMonth = YearMonth.now()
        setupCalendar()
    }

    private fun initAdapter() {
        binding.recyclerView.apply {
            adapter = calendarProjectAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun loadFilings() {
        lifecycleScope.launch {
            val allFilings = withContext(Dispatchers.IO) {
                databaseHelper.getAllFilings()
            }
            calendarProjectAdapter.setItems(allFilings)
            binding.calendarView.notifyCalendarChanged()
        }
    }

    private fun setupCalendar() {
        val startMonth = YearMonth.now()
        val endMonth = startMonth.plusYears(1)

        binding.calendarView.setup(startMonth, endMonth, startOfWeekDay)
        binding.calendarView.scrollToMonth(currentMonth)

        binding.calendarView.monthHeaderBinder = object : MonthHeaderFooterBinder<HeaderViewContainer> {
            override fun create(view: View) = HeaderViewContainer(view)
            override fun bind(container: HeaderViewContainer, data: CalendarMonth) {
                container.bind(data)
            }
        }

        binding.calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view) { selectedDay ->
                selectedDateLocal = selectedDay.date
                onDateSelected(selectedDay.date)
                binding.calendarView.notifyCalendarChanged()
            }

            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.bind(data)
            }
        }

        binding.calendarView.monthScrollListener = { month ->
            currentMonth = month.yearMonth
            updateMonthYearText(currentMonth)
        }

        updateMonthYearText(currentMonth)
    }

    private fun updateMonthYearText(month: YearMonth) {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())
        binding.monthYearText.text = month.format(formatter)
    }

    private fun checkIfProjectsExistForDate(date: LocalDate): Boolean {
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedDate = date.format(dateFormatter)
        return calendarProjectAdapter.items.any { it.startsAt?.startsWith(formattedDate) == true }
    }

    fun onDateSelected(date: LocalDate) {
        selectedDate = date.atStartOfDay()
    }

//    override fun onDataChanged() {
//        loadFilings()
//    }

    override fun onDestroyView() {
//        databaseHelper.removeDatabaseChangeListener(this)
        super.onDestroyView()
    }

    // ----------------------- ViewContainers -------------------------

    inner class DayViewContainer(view: View, private val onDayClick: (CalendarDay) -> Unit) : ViewContainer(view) {
        val binding: CalendarViewBinding = CalendarViewBinding.bind(view)
        private lateinit var day: CalendarDay

        init {
            view.setOnClickListener { onDayClick(day) }
        }

        fun bind(day: CalendarDay) {
            this.day = day
            val isSelected = day.date == selectedDateLocal
            val isToday = day.date == currentDate
            val isPast = day.date < currentDate
            val hasProjects = checkIfProjectsExistForDate(day.date)
            val inMonth = YearMonth.from(day.date) == currentMonth

            binding.calendarDayText.text = day.date.dayOfMonth.toString()

            when {
                isToday -> {
                    binding.calendarDayText.setTextColor(ContextCompat.getColor(requireContext(), R.color.base_white))
                    binding.calendarDayText.setBackgroundResource(R.drawable.bg_690acf_11)
                }
                isSelected -> {
                    binding.calendarDayText.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_icon_primary_inverse))
                    binding.calendarDayText.setBackgroundResource(R.drawable.bg_000000_11)
                }
                isPast -> {
                    val color = if (hasProjects) R.color.text_icon_primary else R.color.text_icon_disabled
                    binding.calendarDayText.setTextColor(ContextCompat.getColor(requireContext(), color))
                    binding.calendarDayText.setBackgroundResource(
                        if (hasProjects) R.drawable.bg_border_secondary_11 else 0
                    )
                }
                else -> {
                    val color = if (hasProjects) R.color.text_icon_primary else if (inMonth) R.color.border_tertiary else R.color.text_icon_disabled
                    binding.calendarDayText.setTextColor(ContextCompat.getColor(requireContext(), color))
                    binding.calendarDayText.setBackgroundResource(
                        if (hasProjects) R.drawable.bg_border_secondary_11 else 0
                    )
                }
            }

            if (selectedDateLocal != currentDate) updateTodayStyle()

            binding.executePendingBindings()
        }

        private fun updateTodayStyle() {
            if (day.date == LocalDate.now()) {
                binding.calendarDayText.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_icon_brand))
                binding.calendarDayText.setBackgroundResource(R.drawable.bg_10_border_brand_11)
            }
        }
    }

    inner class HeaderViewContainer(view: View) : ViewContainer(view) {
        val binding: CalendarViewHeaderBinding = CalendarViewHeaderBinding.bind(view)

        fun bind(data: CalendarMonth) {
            val weekDays = DayOfWeek.values().let {
                it.dropWhile { day -> day != startOfWeekDay } + it.takeWhile { day -> day != startOfWeekDay }
            }
            val dayTextViews = listOf(
                binding.mondayText, binding.tuesdayText, binding.wednesdayText,
                binding.thursdayText, binding.fridayText, binding.saturdayText, binding.sundayText
            )

            dayTextViews.forEachIndexed { index, textView ->
                textView.text = weekDays[index].getDisplayName(TextStyle.SHORT, Locale.getDefault()).take(1)
            }
        }
    }

    // ----------------------- Adapter -------------------------

    class CalendarFilingAdapter : RecyclerView.Adapter<CalendarFilingAdapter.ViewHolder>() {
        val items = mutableListOf<Filing>()

        @SuppressLint("NotifyDataSetChanged")
        fun setItems(newItems: List<Filing>) {
            items.clear()
            items.addAll(newItems)
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemFillBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount(): Int = items.size

        inner class ViewHolder(private val binding: ItemFillBinding) : RecyclerView.ViewHolder(binding.root) {
            fun bind(filing: Filing) {
                binding.filing = filing
                binding.executePendingBindings()
            }
        }
    }
}

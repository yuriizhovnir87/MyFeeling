package com.yurazhovnir.myfeeling.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.BaseAdapter
import androidx.core.content.ContextCompat
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
import com.yurazhovnir.myfeeling.helper.DatabaseChangeListener
import com.yurazhovnir.myfeeling.helper.DatabaseHelper
import io.realm.kotlin.ext.copyFromRealm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class CalendarFragment : BaseFragment<FragmentCalendarBinding>(FragmentCalendarBinding::inflate), DatabaseChangeListener {
    override val TAG: String
        get() = "CalendarFragment"
    private var calendarProjectAdapter = CalendarFilingAdapter()
    var selectedDateLocal: LocalDate? = null
    private var selectedDate: LocalDateTime? = LocalDateTime.now()
    private lateinit var currentMonth: YearMonth
    val currentDate: LocalDate = LocalDate.now()
    private lateinit var databaseHelper: DatabaseHelper

    private var startOfWeekDay: DayOfWeek = DayOfWeek.MONDAY
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        databaseHelper = DatabaseHelper(requireContext())
        databaseHelper.addDatabaseChangeListener(this)
        initAdapter()

    }

    private fun initAdapter() {
        binding.recyclerView.let {
            it.adapter = calendarProjectAdapter
            it.layoutManager = LinearLayoutManager(requireContext())
        }
        updateFilingList()
    }

    private fun updateFilingList() {
        calendarProjectAdapter.setItems(databaseHelper.getAllFilings())
    }

    private fun setupCalendar() {
        val current = YearMonth.now()
        val lastMonth = YearMonth.now().plusYears(1)

        binding.calendarView.setup(current, lastMonth, startOfWeekDay)
        binding.calendarView.scrollToMonth(current)

        binding.calendarView.monthHeaderBinder =
            object : MonthHeaderFooterBinder<HeaderViewContainer> {
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

            @SuppressLint("SetTextI18n")
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.bind(data)
                container.binding.calendarDayText.text = data.date.dayOfMonth.toString()
            }
        }

        binding.calendarView.monthScrollListener = { month ->
            val isCurrentMonth = month.yearMonth == YearMonth.now()
            when {
                month.yearMonth.isBefore(currentMonth) && !isCurrentMonth -> handleMonthChange(-1)
                month.yearMonth.isAfter(currentMonth) -> handleMonthChange(1)
                else -> {
                    currentMonth = month.yearMonth
                    updateMonthYearText(currentMonth)
                }
            }
        }
    }

    inner class DayViewContainer(view: View, private val onDayClick: (CalendarDay) -> Unit) :
        ViewContainer(view) {

        val binding: CalendarViewBinding = CalendarViewBinding.bind(view)
        private lateinit var day: CalendarDay

        init {
            view.setOnClickListener {
                onDayClick(day)
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(day: CalendarDay) {
            this.day = day
            val displayedMonth = YearMonth.from(day.date)
            val isInCurrentMonth = displayedMonth == currentMonth
            val isSelected = day.date == selectedDateLocal
            val isToday = day.date == currentDate
            val isPast = day.date < currentDate

            binding.calendarDayText.text = day.date.dayOfMonth.toString()
            val hasProjects = checkIfProjectsExistForDate(day.date)

            when {
                isToday -> {
                    binding.calendarDayText.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.base_white)
                    )
                    binding.calendarDayText.setBackgroundResource(R.drawable.bg_690acf_11)
                }

                isSelected -> {
                    binding.calendarDayText.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.text_icon_primary_inverse)
                    )
                    binding.calendarDayText.setBackgroundResource(R.drawable.bg_000000_11)
                }

                isPast -> {
                    binding.calendarDayText.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.text_icon_disabled)
                    )
                    binding.calendarDayText.setBackgroundResource(0)
                    if (hasProjects) {
                        binding.calendarDayText.setTextColor(
                            ContextCompat.getColor(requireContext(), R.color.text_icon_primary)
                        )
                        binding.calendarDayText.setBackgroundResource(R.drawable.bg_border_secondary_11)
                    }
                }

                else -> {
                    if (hasProjects) {
                        binding.calendarDayText.setTextColor(
                            ContextCompat.getColor(requireContext(), R.color.text_icon_primary)
                        )
                        binding.calendarDayText.setBackgroundResource(R.drawable.bg_border_secondary_11)
                    } else {
                        val textColor = if (isInCurrentMonth) {
                            ContextCompat.getColor(requireContext(), R.color.border_tertiary)
                        } else {
                            ContextCompat.getColor(requireContext(), R.color.text_icon_disabled)
                        }
                        binding.calendarDayText.setTextColor(textColor)
                        binding.calendarDayText.setBackgroundResource(0)
                    }
                }
            }
            if (selectedDateLocal != null && selectedDateLocal != currentDate) {
                updateTodayStyle()
            }

            binding.executePendingBindings()
        }

        private fun updateTodayStyle() {
            if (day.date == LocalDate.now()) {
                binding.calendarDayText.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.text_icon_brand)
                )
                binding.calendarDayText.setBackgroundResource(R.drawable.bg_10_border_brand_11)
            }
        }
    }

    inner class HeaderViewContainer(view: View) : ViewContainer(view) {
        val binding: CalendarViewHeaderBinding = CalendarViewHeaderBinding.bind(view)

        fun bind(data: CalendarMonth) {
            val adjustedDaysOfWeek = DayOfWeek.entries
                .dropWhile { it != startOfWeekDay } + DayOfWeek.entries
                .takeWhile { it != startOfWeekDay }

            val weekDaysFirstLetters = adjustedDaysOfWeek.map {
                it.getDisplayName(TextStyle.SHORT, Locale.getDefault()).take(1)
            }

            val dayTextViews = listOf(
                binding.mondayText,
                binding.tuesdayText,
                binding.wednesdayText,
                binding.thursdayText,
                binding.fridayText,
                binding.saturdayText,
                binding.sundayText
            )

            dayTextViews.forEachIndexed { index, textView ->
                textView.text = weekDaysFirstLetters[index]
            }
        }
    }

    fun onDateSelected(date: LocalDate) {
        selectedDate = date.atStartOfDay()
    }

    private fun handleMonthChange(direction: Int) {
        currentMonth = if (direction < 0) {
            currentMonth.minusMonths(1)
        } else {
            currentMonth.plusMonths(1)
        }
        binding.calendarView.scrollToMonth(currentMonth)
        updateMonthYearText(currentMonth)
    }

    private fun updateMonthYearText(month: YearMonth) {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
        binding.monthYearText.text = month.format(formatter)
    }

    private fun checkIfProjectsExistForDate(date: LocalDate): Boolean {
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedDate = date.format(dateFormatter)
        val projects = databaseHelper.getAllFilings()
        return projects?.any { it.startsAt?.startsWith(formattedDate) == true } == true
    }

    class CalendarFilingAdapter : RecyclerView.Adapter<CalendarFilingAdapter.ViewHolder>() {

        private val items = ArrayList<Filing>()

        @SuppressLint("NotifyDataSetChanged")
        fun setItems(newItems: List<Filing>) {
            items.clear()
            items.addAll(newItems)
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemFillBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount(): Int = items.size

        inner class ViewHolder(private val binding: ItemFillBinding) :
            RecyclerView.ViewHolder(binding.root) {

            fun bind(filing: Filing) {
                binding.filing = filing
                binding.executePendingBindings()
            }
        }
    }

    override fun onDestroyView() {
        databaseHelper.removeDatabaseChangeListener(this)
        super.onDestroyView()
    }

    override fun onDataChanged() {
        updateFilingList()
    }
}
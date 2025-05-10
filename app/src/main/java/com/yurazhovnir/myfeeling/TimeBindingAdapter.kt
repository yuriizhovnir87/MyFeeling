package com.yurazhovnir.myfeeling

import android.text.format.DateFormat
import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.text.*
import java.util.*

object TimeBindingAdapter {
    private val utcFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    private val shortDayFormat = SimpleDateFormat("EEE", Locale.getDefault()).apply {
        timeZone = TimeZone.getDefault()
    }

    private val shortDateFormat = SimpleDateFormat("dd MMM", Locale.getDefault()).apply {
        timeZone = TimeZone.getDefault()
    }

    @JvmStatic
//    @BindingAdapter("loadDateTimeMonth")
    fun loadDateTimeMonth(textView: TextView, createdAt: String?) {
        if (createdAt.isNullOrEmpty()) {
            textView.text = "--"
            return
        }

        try {
            val parsedDate = utcFormat.parse(createdAt)
            val timeFormat = DateFormat.getTimeFormat(textView.context).apply {
                timeZone = TimeZone.getDefault()
            }

            if (parsedDate != null) {
                val time = timeFormat.format(parsedDate)

                // Get current date with time set to 00:00:00
                val today = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                // Get tomorrow's date
                val tomorrow = Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_YEAR, 1)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                // Create a Calendar from parsed date
                val parsedCalendar = Calendar.getInstance().apply {
                    timeInMillis = parsedDate.time
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                val dateText = when {
                    parsedCalendar.timeInMillis == today.timeInMillis -> {
                        "Today"
                    }
                    parsedCalendar.timeInMillis == tomorrow.timeInMillis -> {
                        "Tomorrow"
                    }
                    else -> {
                        val day = shortDayFormat.format(parsedDate)
                        val date = shortDateFormat.format(parsedDate)
                        "$day, $date"
                    }
                }

                textView.text = "$time $dateText"
            } else {
                textView.text = "--"
            }

        } catch (e: Exception) {
            textView.text = "--"
        }
    }
}
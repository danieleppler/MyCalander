package com.example.mycalender

import CalendarAdapter
import CalenderViewModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mycalender.DateModels.CalendarDay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private const val TAG = "CALENDER_FRAGMENT"

class CalenderFragment : Fragment() {

    private var recyclerView: RecyclerView? = null
    private var txtMonthYear: TextView? = null
    private var btnPreviousMonth: ImageButton? = null
    private var btnNextMonth: ImageButton? = null

    private lateinit var calendarAdapter: CalendarAdapter
    private val calenderViewModel: CalenderViewModel by activityViewModels()
    private var calendarDays: MutableList<CalendarDay> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_calender, container, false)

        recyclerView = view.findViewById(R.id.recycler_view_calendar)
        txtMonthYear = view.findViewById(R.id.txt_month_year)
        btnPreviousMonth = view.findViewById(R.id.btn_previous_month)
        btnNextMonth = view.findViewById(R.id.btn_next_month)

        calendarDays = mutableListOf()
        setupRecyclerView()
        setupMonthNavigation()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Collect events changes
                launch {
                    calenderViewModel.events.collect {
                        generateCalendarData()
                    }
                }

                // Collect month changes
                launch {
                    calenderViewModel.currentMonth.collect {
                        updateMonthYearDisplay()
                        generateCalendarData()
                    }
                }

                // Collect year changes
                launch {
                    calenderViewModel.currentYear.collect {
                        updateMonthYearDisplay()
                        generateCalendarData()
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        recyclerView!!.layoutManager = GridLayoutManager(context, 7)
        calendarAdapter = CalendarAdapter(calendarDays) { dayClicked ->
            handleDayClicked(dayClicked)
        }
        recyclerView!!.adapter = calendarAdapter
    }

    private fun setupMonthNavigation() {
        btnPreviousMonth?.setOnClickListener {
            calenderViewModel.goToPreviousMonth()
        }

        btnNextMonth?.setOnClickListener {
            calenderViewModel.goToNextMonth()
        }
    }

    private fun updateMonthYearDisplay() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, calenderViewModel.currentMonth.value)
        calendar.set(Calendar.YEAR, calenderViewModel.currentYear.value)

        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        txtMonthYear?.text = dateFormat.format(calendar.time)
    }

    private fun handleDayClicked(day: CalendarDay) {
        if (day.dayText.isNotEmpty()) {
            val action = CalenderFragmentDirections.actionCalenderFragmentToDayViewFragment(
                day.dayText,
                calenderViewModel.currentMonth.value,
                calenderViewModel.currentYear.value
            )
            try{
                findNavController().navigate(action)
            }
            catch (e:Exception){
                Log.d("already in destination", TAG)
            }
        }
    }

    private fun generateCalendarData() {
        calendarDays.clear()

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, calenderViewModel.currentMonth.value)
        calendar.set(Calendar.YEAR, calenderViewModel.currentYear.value)
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1

        // Add empty cells for days before month starts
        for (i in 1..firstDayOfWeek) {
            calendarDays += CalendarDay("", false)
        }

        // Add days of current month
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (i in 1..daysInMonth) {
            val relevantEvents = calenderViewModel.events.value.filter { event ->
                val eventCalendar = Calendar.getInstance()
                eventCalendar.time = event?.eventDateFrom!!
                Log.d(TAG,"checking event ${event.eventName} that starts on ${event.eventDateTo} for day ${i}}")
                eventCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
                        eventCalendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
                        eventCalendar.get(Calendar.DAY_OF_MONTH) == i
            }


            for(day in relevantEvents){
               Log.d(TAG,"Found relevant day ${day?.eventName} that start on ${day?.eventDateTo} " +
                       "for day number ${i}")
            }
            calendarDays += if (relevantEvents.isNotEmpty())
                CalendarDay(i.toString(), true, events = relevantEvents)
            else CalendarDay(i.toString(), true)
        }

        calendarAdapter.notifyDataSetChanged()
    }
}
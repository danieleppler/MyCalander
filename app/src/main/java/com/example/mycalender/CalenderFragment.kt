package com.example.mycalender


import CalendarAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import java.util.Calendar


private const val TAG ="CALENDER_FRAGMENT"

class CalenderFragment : Fragment() {


    private var recyclerView: RecyclerView? = null
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
        calendarDays = mutableListOf()
        setupRecyclerView()
        generateCalendarData()
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch{
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                calenderViewModel.events.collect {
                    generateCalendarData()
                }
            }
        }


    }

    private fun setupRecyclerView() {
        // 7 columns for days of week
        recyclerView!!.layoutManager = GridLayoutManager(context, 7)
        calendarAdapter = CalendarAdapter(calendarDays,{dayClicked ->
            handleDayClicked(dayClicked)
        })
        recyclerView!!.adapter = calendarAdapter
    }

    private fun handleDayClicked(day : CalendarDay){
        if (day.dayText.isNotEmpty()) {
            val calendar = Calendar.getInstance()
            val action = CalenderFragmentDirections.actionCalenderFragmentToDayViewFragment(
                day.dayText,
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.YEAR)
            )
            findNavController().navigate(action)
        }
    }

    private fun generateCalendarData() {
        calendarDays.clear()
        // Get current month data
        val calendar = Calendar.getInstance();

        // Set to first day of month
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1; // 0-based

        // Add empty cells for days before month starts
        for (i in 1.. firstDayOfWeek) {
            calendarDays += CalendarDay("", false);
        }

        // Add days of current month
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (i in 1..daysInMonth) {
            val relevantEvents = calenderViewModel.events.value.filter { event ->
                val eventCalendar = Calendar.getInstance()
                eventCalendar.time = event?.eventDateFrom!! // Assuming eventDateFrom is Long
                eventCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
                        eventCalendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
                        eventCalendar.get(Calendar.DAY_OF_MONTH) == i
            }
            calendarDays += if(relevantEvents.isNotEmpty())
                CalendarDay(i.toString(), true, events = relevantEvents);
            else CalendarDay(i.toString(), true);
        }
        calendarAdapter.notifyDataSetChanged()
    }
}
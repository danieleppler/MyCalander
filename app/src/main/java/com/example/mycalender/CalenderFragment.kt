package com.example.mycalender


import CalendarAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Calendar


private const val TAG ="CALENDER_FRAGMENT"

class CalenderFragment : Fragment() {

    lateinit var addItemBtn: FloatingActionButton
    private var recyclerView: RecyclerView? = null

    private var calendarDays: List<CalendarDay> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_calender, container, false)

        recyclerView = view.findViewById(R.id.recycler_view_calendar)
        generateCalendarData()
        setupRecyclerView()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addItemBtn = view.findViewById(R.id.fab)
        // Set up button click with binding
        addItemBtn.setOnClickListener {
            try {
                findNavController().navigate(R.id.add_calender_item)
                Log.d("Navigation", "Navigation called successfully")
            } catch (e: Exception) {
                Log.e("Navigation", "Navigation failed: ${e.message}")
            }
        }
    }
    private fun setupRecyclerView() {
        // 7 columns for days of week
        recyclerView!!.layoutManager = GridLayoutManager(context, 7)
        recyclerView!!.setAdapter(CalendarAdapter(calendarDays))
    }

    private fun generateCalendarData() {


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
            calendarDays += CalendarDay(i.toString(), false);
        }
    }
}
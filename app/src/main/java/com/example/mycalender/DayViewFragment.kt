package com.example.mycalender

import CalenderViewModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mycalender.DateModels.Event
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

private const val TAG ="DayViewFragment"

class DayViewFragment : Fragment() {

    private lateinit var dayTitle: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAddEvent: FloatingActionButton
    private lateinit var dayViewAdapter: DayViewAdapter
    private val calenderViewModel: CalenderViewModel by activityViewModels()
    private val args: DayViewFragmentArgs by navArgs()

    private val hourSlots = mutableListOf<HourSlot>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_day_view, container, false)

        dayTitle = view.findViewById(R.id.day_title)
        recyclerView = view.findViewById(R.id.recycler_view_day)
        fabAddEvent = view.findViewById(R.id.fab_add_event)

        setupRecyclerView()
        setupDayTitle()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                calenderViewModel.events.collect {
                    generateHourSlots(it)
                }
            }
        }

        fabAddEvent.setOnClickListener {
            // Navigate to create event for this specific day
            try{
                val action = DayViewFragmentDirections.actionDayViewFragmentToEventDetailsFragment(
                    null,Date(args.year - 1900,args.month,args.dayNumber.toInt())
                )
                findNavController().navigate(action)
            }
            catch(e:Exception){
                Log.e(e.message,TAG)
            }
        }
    }

    private fun setupRecyclerView() {
        dayViewAdapter = DayViewAdapter(hourSlots,{ event ->
            // Handle event click - navigate to event details
            navigateToEventDetails(event)
        })
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = dayViewAdapter
    }


    private fun navigateToEventDetails(event: Event) {
        val action = DayViewFragmentDirections.actionDayViewFragmentToEventDetailsFragment(
            UUID.fromString(event.id),null
        )
        findNavController().navigate(action)
    }

    private fun setupDayTitle() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, args.dayNumber.toInt())
        calendar.set(Calendar.MONTH, args.month)
        calendar.set(Calendar.YEAR,args.year)

        val dateFormat = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())
        dayTitle.text = dateFormat.format(calendar.time)
    }

    private fun generateHourSlots(events: List<Event?>) {
        hourSlots.clear()

        // Generate 24 hour slots (or 12 for business hours)
        for (hour in 0..23) {
            val hourText = when {
                hour == 0 -> "12 AM"
                hour < 12 -> "$hour AM"
                hour == 12 -> "12 PM"
                else -> "${hour - 12} PM"
            }

            // Find events that fall in this hour
            val eventsInHour = events.filterNotNull().filter { event ->
                val eventCalendar = Calendar.getInstance()
                eventCalendar.time = event.eventDateTo

                val eventDay = eventCalendar.get(Calendar.DAY_OF_MONTH)
                val eventHour = eventCalendar.get(Calendar.HOUR_OF_DAY)
                val eventMonth = eventCalendar.get(Calendar.MONTH)
                val eventYear = eventCalendar.get(Calendar.YEAR)

                eventDay == args.dayNumber.toInt() && eventHour == hour && eventMonth == args.month
                        && eventYear == args.year
            }
            hourSlots.add(HourSlot(hourText, hour, eventsInHour))
        }
        dayViewAdapter.notifyDataSetChanged()
    }
}

// Data class for hour slot
data class HourSlot(
    val hourText: String,
    val hour: Int,
    val events: List<Event> = emptyList()
)

// Adapter for day view
class DayViewAdapter(
    private val hourSlots: List<HourSlot>,
    private val onEventClick: (Event) -> Unit
) : RecyclerView.Adapter<DayViewAdapter.HourViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_hour_slot, parent, false)
        return HourViewHolder(view,onEventClick)
    }

    override fun onBindViewHolder(holder: HourViewHolder, position: Int) {
        holder.bind(hourSlots[position])
    }

    override fun getItemCount() = hourSlots.size

    class HourViewHolder(itemView: View,private val onEventClick: (Event) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val hourText: TextView = itemView.findViewById(R.id.hour_text)
        private val eventsContainer: ViewGroup = itemView.findViewById(R.id.events_container)

        fun bind(hourSlot: HourSlot) {
            hourText.text = hourSlot.hourText

            eventsContainer.removeAllViews()

            hourSlot.events.forEach { event ->
                val eventView = LayoutInflater.from(itemView.context)
                    .inflate(R.layout.item_event_card, eventsContainer, false)

                val eventTitle = eventView.findViewById<TextView>(R.id.event_title)
                eventTitle?.text = event.eventName ?: "Untitled Event"
                eventView.setBackgroundColor(event.eventColor)
                eventView.setOnClickListener {
                    onEventClick(event)
                }
                eventsContainer.addView(eventView)
            }
        }
    }
}
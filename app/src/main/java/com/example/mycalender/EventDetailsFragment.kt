package com.example.mycalender

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mycalender.databinding.FragmentEventDetailsBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class EventDetailsFragment : Fragment() {

    private lateinit var binding: FragmentEventDetailsBinding
    private val eventDetailsViewModel: EventDetailsViewModel by viewModels()
    private val args: EventDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEventDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        args.eventId?.let { eventId ->
            viewLifecycleOwner.lifecycleScope.launch {
                eventDetailsViewModel.loadEventById(eventId)

                eventDetailsViewModel.event.value?.let { event ->
                    binding.itemTitle.setText(event.eventName)
                    binding.eventLocation.setText(event.eventLocation)
                    binding.eventColor.setText(event.eventColor.toString())
                    updateDateTimeFields(event.eventDateFrom, event.eventDateTo)
                }
            }
        }

        args.eventDate?.let {
            val date = Date(args.eventDate.toString())
            eventDetailsViewModel.updateEvent { oldEvent ->
                oldEvent.copy(eventDateTo = date, eventDateFrom = date)
            }
            updateDateTimeFields(date, date)
        }

        setupDateTimePickers()
        setupTextListeners()
        setupButtons()
    }

    private fun setupDateTimePickers() {
        // DateTime picker for "From" date
        binding.eventDateFrom.setOnClickListener {
            showDateTimePicker(
                currentDate = eventDetailsViewModel.event.value?.eventDateFrom
            ) { selectedDateTime ->
                binding.eventDateFrom.setText(formatDateTime(selectedDateTime))
                eventDetailsViewModel.updateEvent { oldEvent ->
                    oldEvent.copy(eventDateFrom = selectedDateTime)
                }
            }
        }

        // DateTime picker for "To" date
        binding.eventDateTo.setOnClickListener {
            showDateTimePicker(
                currentDate = eventDetailsViewModel.event.value?.eventDateTo
            ) { selectedDateTime ->
                binding.eventDateTo.setText(formatDateTime(selectedDateTime))
                eventDetailsViewModel.updateEvent { oldEvent ->
                    oldEvent.copy(eventDateTo = selectedDateTime)
                }
            }
        }
    }

    private fun showDateTimePicker(
        currentDate: Date? = null,
        onDateTimeSelected: (Date) -> Unit
    ) {
        val calendar = Calendar.getInstance()
        currentDate?.let { calendar.time = it }

        // First show date picker
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setSelection(calendar.timeInMillis)
            .build()

        datePicker.addOnPositiveButtonClickListener { dateSelection ->
            // Then show time picker
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.timeInMillis = dateSelection

            val timePicker = MaterialTimePicker.Builder()
                .setTitleText("Select time")
                .setHour(calendar.get(Calendar.HOUR_OF_DAY))
                .setMinute(calendar.get(Calendar.MINUTE))
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .build()

            timePicker.addOnPositiveButtonClickListener {
                selectedCalendar.set(Calendar.HOUR_OF_DAY, timePicker.hour)
                selectedCalendar.set(Calendar.MINUTE, timePicker.minute)
                selectedCalendar.set(Calendar.SECOND, 0)
                selectedCalendar.set(Calendar.MILLISECOND, 0)

                onDateTimeSelected(selectedCalendar.time)
            }

            timePicker.show(parentFragmentManager, "TIME_PICKER")
        }

        datePicker.show(parentFragmentManager, "DATE_PICKER")
    }

    private fun formatDateTime(date: Date): String {
        val formatter = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        return formatter.format(date)
    }

    private fun updateDateTimeFields(dateFrom: Date?, dateTo: Date?) {
        dateFrom?.let { binding.eventDateFrom.setText(formatDateTime(it)) }
        dateTo?.let { binding.eventDateTo.setText(formatDateTime(it)) }
    }

    private fun setupTextListeners() {
        binding.apply {
            itemTitle.doOnTextChanged { text, _, _, _ ->
                eventDetailsViewModel.updateEvent { oldEvent ->
                    oldEvent.copy(eventName = text.toString())
                }
            }

            eventLocation.doOnTextChanged { text, _, _, _ ->
                eventDetailsViewModel.updateEvent { oldEvent ->
                    oldEvent.copy(eventLocation = text.toString())
                }
            }
        }
    }

    private fun setupButtons() {
        binding.apply {
            tabTask.setOnClickListener {
                findNavController().navigate(R.id.go_to_task_details)
            }

            tabBirthday.setOnClickListener {
                findNavController().navigate(R.id.go_to_birthday_details)
            }

            btnClose.setOnClickListener {
                eventDetailsViewModel.event.value?.eventDateFrom?.let { date ->
                    val calendar = Calendar.getInstance()
                    calendar.time = date
                    val action = EventDetailsFragmentDirections.goBackToDayView(
                        calendar.get(Calendar.DAY_OF_MONTH).toString(),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.YEAR)
                    )
                    findNavController().navigate(action)
                }
            }

            btnSave.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    eventDetailsViewModel.saveEvent()
                    findNavController().navigate(R.id.go_back_to_calender)
                }
            }
        }
    }
}
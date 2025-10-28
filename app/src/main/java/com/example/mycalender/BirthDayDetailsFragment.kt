package com.example.mycalender

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mycalender.databinding.FragmentBirthdayDetailsBinding

class BirthDayDetailsFragment : Fragment() {

    private lateinit var binding : FragmentBirthdayDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBirthdayDetailsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            tabTask.setOnClickListener{  findNavController().navigate(R.id.go_to_task_details)}
            tabEvent.setOnClickListener{ findNavController().navigate(R.id.go_to_event_details)}
            btnClose.setOnClickListener{findNavController().navigate(R.id.go_back_to_calender)}
        }
    }
}
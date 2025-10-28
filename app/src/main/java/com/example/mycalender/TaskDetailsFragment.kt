package com.example.mycalender

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mycalender.databinding.FragmentEventDetailsBinding
import com.example.mycalender.databinding.FragmentTaskDetailsBinding

class TaskDetailsFragment : Fragment() {
    private lateinit var binding : FragmentTaskDetailsBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTaskDetailsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            tabEvent.setOnClickListener{  findNavController().navigate(R.id.go_to_event_details)}
            tabBirthday.setOnClickListener{ findNavController().navigate(R.id.go_to_birthday_details)}
            btnClose.setOnClickListener{findNavController().navigate(R.id.go_back_to_calender)}
        }
    }

}
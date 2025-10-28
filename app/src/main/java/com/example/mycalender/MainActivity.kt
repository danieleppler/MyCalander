package com.example.mycalender

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mycalender.Reposetories.EventRepository

class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        val currentFragment = supportFragmentManager.findFragmentById(R.id.calender_fragment_container)
        if(currentFragment == null){
            supportFragmentManager.beginTransaction().
            add(R.id.calender_fragment_container,CalenderFragment()).
            commit()
        }

    }
}
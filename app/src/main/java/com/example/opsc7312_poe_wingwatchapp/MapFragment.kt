package com.example.opsc7312_poe_wingwatchapp

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class MapFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map2, container, false)

        val newSightbtn = view.findViewById<Button>(R.id.newSightbtn)
        newSightbtn.setOnClickListener {
            // Create an Intent to open the NewActivity
            val intent = Intent(MainPageFrame(), NewSightPage::class.java)
            startActivity(intent)
            // Finish the current activity to close it
            MainPageFrame().finish()
        }

        return view
    }

}
package com.example.opsc7312_poe_wingwatchapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class HotspotDetsPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hotspot_dets_page)

        val Backbtn = findViewById<Button>(R.id.backbtn)

        Backbtn.setOnClickListener()
        {
            startActivity(Intent(this@HotspotDetsPage, MainPageFrame::class.java))
            NewSightPage().finish()
        }
    }
}
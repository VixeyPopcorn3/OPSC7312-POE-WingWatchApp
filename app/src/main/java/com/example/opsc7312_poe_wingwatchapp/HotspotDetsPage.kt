package com.example.opsc7312_poe_wingwatchapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class HotspotDetsPage : AppCompatActivity() {

    private var loginId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hotspot_dets_page)

        loginId = intent.getIntExtra("loginId", 0)
        val hotspotName = intent.getStringExtra("hotspot_name")


        val Backbtn = findViewById<Button>(R.id.backbtn)

        Backbtn.setOnClickListener()
        {
            startActivity(Intent(this@HotspotDetsPage, MainPageFrame::class.java).apply
            {
                if (loginId != null) {
                    intent.putExtra("loginId", loginId.toInt())
                }
            })
            NewSightPage().finish()
        }
    }
}
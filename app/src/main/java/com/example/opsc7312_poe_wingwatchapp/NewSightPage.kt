package com.example.opsc7312_poe_wingwatchapp
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class NewSightPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_sight_page)

        val Backbtn = findViewById<Button>(R.id.backbtn)

        Backbtn.setOnClickListener()
        {
            startActivity(Intent(this@NewSightPage, MainPageFrame::class.java))
            NewSightPage().finish()
        }
        val Savebtn = findViewById<Button>(R.id.Savebtn)

        Backbtn.setOnClickListener()
        {
            Save()
        }
    }
    private fun Save()
    {
        //Save logic
        startActivity(Intent(this@NewSightPage, MainPageFrame::class.java))
        NewSightPage().finish()
    }

}
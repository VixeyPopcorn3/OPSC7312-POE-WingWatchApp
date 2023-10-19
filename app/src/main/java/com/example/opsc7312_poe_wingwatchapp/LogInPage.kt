package com.example.opsc7312_poe_wingwatchapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.EditText

class LogInPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in_page)

        val LogInbtn = findViewById<Button>(R.id.LogInbtn)
        val Backbtn = findViewById<Button>(R.id.backbtn)

        LogInbtn.setOnClickListener()
        {
            logIn()
        }
        Backbtn.setOnClickListener()
        {
            startActivity(Intent(this@LogInPage, StartPage::class.java))
        }

        val showHideBtn= findViewById<Button>(R.id.showHideBtn)
        val password = findViewById<EditText>(R.id.PasswordEtxt)

        showHideBtn.setOnClickListener {
            if (showHideBtn.text.toString().equals("Show")) {
                password.transformationMethod = HideReturnsTransformationMethod.getInstance()
                showHideBtn.text = "Hide"
            } else {
                password.transformationMethod = PasswordTransformationMethod.getInstance()
                showHideBtn.text = "Show"
            }
        }
    }

    private fun logIn()
    {
        //login logic, dont forget error messages
        startActivity(Intent(this@LogInPage, MainPageFrame::class.java))//if login correct
    }
}
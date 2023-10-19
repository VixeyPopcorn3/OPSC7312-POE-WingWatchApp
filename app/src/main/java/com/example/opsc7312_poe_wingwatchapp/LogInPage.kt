package com.example.opsc7312_poe_wingwatchapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

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

    private fun logIn() {
        val db = Firebase.firestore
        val editTextEmail = findViewById<EditText>(R.id.SpeciesNameEtxt)
        val editTextPassword = findViewById<EditText>(R.id.PasswordEtxt)
        var temp = false

        val Eemail = editTextEmail.text.toString().trim()
        val password = editTextPassword.text.toString().trim()

        if (TextUtils.isEmpty(Eemail) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

        // Hash the password
        val hashedPassword = hashPassword(password)

        val usersCollection = db.collection("Users")
        //val enteredEmail = editTextEmail.text.toString().trim()

        //checks all emails in each document
        usersCollection.get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot)
                {
                    val dBemail = document.getString("Email")
                    val dBUsername = document.getString("Username")
                    val dBpassword = document.getString("Password")
                    if (dBemail == Eemail && dBpassword == hashedPassword)
                    {
                        // Email and Password found in the Firestore collection
                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                        val loginId = document.getLong("LoginID")
                        Toast.makeText(this, "Welcome " + dBUsername, Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@LogInPage, MainPageFrame::class.java).apply//if login correct
                        {
                            putExtra("loginId", loginId.toString())
                        })
                        temp = true
                        break // Exit the loop if a match is found
                    }
                }
                if(temp==false)
                {
                    // If the loop completes without finding a match, the email does not exist
                    Toast.makeText(this, "Please enter a valid email or password", Toast.LENGTH_SHORT).show()
                }

            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error connecting to Database", Toast.LENGTH_SHORT).show()
            }
    }
    private fun hashPassword(password: String): String? {
        return try {
            val messageDigest = MessageDigest.getInstance("SHA-256")
            val hashBytes = messageDigest.digest(password.toByteArray())
            val stringBuilder = StringBuilder()
            for (hashByte in hashBytes) {
                stringBuilder.append(Integer.toString((hashByte.toInt() and 0xff) + 0x100, 16).substring(1))
            }
            stringBuilder.toString()
        }
        catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            null
        }
    }
}
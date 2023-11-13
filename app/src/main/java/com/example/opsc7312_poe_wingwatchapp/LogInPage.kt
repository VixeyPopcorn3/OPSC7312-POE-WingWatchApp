package com.example.opsc7312_poe_wingwatchapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings.Global.putInt
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class LogInPage : AppCompatActivity() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

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

        val email = editTextEmail.text.toString().trim()
        val password = editTextPassword.text.toString().trim()

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Login successful
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                    val user = auth.currentUser
                    if (user != null) {
                        val uid = user.uid

                        val usersCollection = db.collection("Users")

                        usersCollection.get()
                            .addOnSuccessListener { querySnapshot ->
                                for (document in querySnapshot)
                                {
                                    val dbUID = document.getString("UID")
                                    val dBUsername = document.getString("Username")

                                    if (dbUID == uid)
                                    {
                                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                                        val loginId = document.getLong("LoginID")
                                        Toast.makeText(this, "Welcome " + dBUsername, Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this@LogInPage, MainPageFrame::class.java)
                                        if (loginId != null) {
                                            intent.putExtra("loginId", loginId.toInt())
                                        }
                                        startActivity(intent)
                                        temp = true
                                        break // Exit the loop if a match is found
                                    }
                                }
                                if(temp==false)
                                {
                                    // If the loop completes without finding a match, the email does not exist
                                    Toast.makeText(this, "Please enter a valid email and/or password", Toast.LENGTH_SHORT).show()
                                }

                            }
                            .addOnFailureListener { exception ->
                                Toast.makeText(this, "Error connecting to Database", Toast.LENGTH_SHORT).show()
                            }
                    } else
                    {
                        Toast.makeText(this, "User is null", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
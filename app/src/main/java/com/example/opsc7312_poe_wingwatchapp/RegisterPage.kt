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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class RegisterPage : AppCompatActivity() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_page)

        val Registerbtn = findViewById<Button>(R.id.Registerbtn)
        val Backbtn = findViewById<Button>(R.id.backbtn)

        Registerbtn.setOnClickListener()
        {
            register()
        }
        Backbtn.setOnClickListener()
        {
            startActivity(Intent(this@RegisterPage, StartPage::class.java))
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
    private fun register() {
        val db = Firebase.firestore
        val editTextEmail = findViewById<EditText>(R.id.SpeciesNameEtxt)
        val editTextUsername = findViewById<EditText>(R.id.UsernameEtxt)
        val editTextPassword = findViewById<EditText>(R.id.PasswordEtxt)

        val email = editTextEmail.text.toString().trim()
        val username = editTextUsername.text.toString().trim()
        val password = editTextPassword.text.toString().trim()

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Please enter email, username, and password", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Registration success
                    val user = auth.currentUser
                    if (user != null) {
                        val uid = user.uid
                        val usersCollection = db.collection("Users")
                        usersCollection.orderBy("LoginID", Query.Direction.DESCENDING).limit(1)
                            .get()
                            .addOnSuccessListener { querySnapshot ->
                                var highestLoginID = 0
                                for (document in querySnapshot) {
                                    highestLoginID = document.getLong("LoginID")?.toInt() ?: 0
                                }

                                // Increment the highest loginID by 1 to generate a new unique loginID
                                val newLoginID = highestLoginID + 1

                                val newUser = hashMapOf(
                                    "Username" to username,
                                    "LoginID" to newLoginID,
                                    "UID" to uid,
                                    "Sight" to 0,
                                    "Species" to 0,
                                    "Email" to email
                                )

                                // Save user to Firestore
                                usersCollection.add(newUser)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                                        startActivity(Intent(this@RegisterPage, StartPage::class.java))
                                    }
                                    .addOnFailureListener { exception ->
                                        Toast.makeText(this, "Error registering user", Toast.LENGTH_SHORT).show()
                                    }
                            }
                            .addOnFailureListener { exception ->
                                Toast.makeText(this, "Error retrieving highest LoginID", Toast.LENGTH_SHORT).show()
                            }
                        // Use the UID as needed (e.g., store in database reference, perform specific actions)
                    } else {
                        Toast.makeText(this, "User is null", Toast.LENGTH_SHORT).show()
                    }



                } else {
                    // If registration fails, display a message to the user.
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
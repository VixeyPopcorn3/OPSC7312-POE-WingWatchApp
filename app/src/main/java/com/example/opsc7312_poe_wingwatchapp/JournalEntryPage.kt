package com.example.opsc7312_poe_wingwatchapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class JournalEntryPage : AppCompatActivity() {
    private lateinit var entryTxt: TextView

    private var loginId: Int = 0
    private val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journal_entry_page)

        loginId = intent.getIntExtra("loginId", 0)

        entryTxt = findViewById<EditText>(R.id.entryEdittxt)

        val Backbtn = findViewById<Button>(R.id.backbtn)

        Backbtn.setOnClickListener()
        {
            val intent = Intent(this@JournalEntryPage, MainPageFrame::class.java)

            if (loginId != null) {
                val bundle = Bundle()
                bundle.putInt("loginId", loginId)
                intent.putExtras(bundle)
            }

            startActivity(intent)
            finish()
        }
        val Savebtn = findViewById<Button>(R.id.Savebtn)

        Savebtn.setOnClickListener()
        {
            val entry = entryTxt.text.toString().trim()

            if (entry.isEmpty()) {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            } else {
                saveEntry(entry, loginId)
            }
        }
    }
    private fun saveEntry(entry: String, loginID: Int) {
        val date = getCurrentDateAsString()
        val journal = hashMapOf(
            "Entry" to entry,
            "LoginID" to loginID,
            "Date" to date
        )

        val journalCollection = db.collection("Journal")
        journalCollection.add(journal)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "Journal Entry added: ${documentReference.id}", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainPageFrame::class.java)

                if (loginId != null) {
                    val bundle = Bundle()
                    bundle.putInt("loginId", loginId)
                    intent.putExtras(bundle)
                }

                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error adding Journal Entry: $e", Toast.LENGTH_SHORT).show()
            }
    }
    fun getCurrentDateAsString(): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy")
        val date = Date()
        return dateFormat.format(date)
    }

}
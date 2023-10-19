package com.example.opsc7312_poe_wingwatchapp
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class NewSightPage : AppCompatActivity() {

    private lateinit var speciesNameTxt: TextView
    private lateinit var hotspotTxt: TextView
    private lateinit var behaviourTxt: TextView
    private lateinit var notesTxt: TextView

    private var loginId: Int = 0
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_sight_page)

        loginId = intent.getIntExtra("loginId", 0)

        speciesNameTxt = findViewById<EditText>(R.id.SpeciesNameEtxt)
        hotspotTxt = findViewById<EditText>(R.id.HotspotEtxt)
        behaviourTxt = findViewById<EditText>(R.id.BehaviourEtxt)
        notesTxt = findViewById<EditText>(R.id.NotesEtxt)

        val Backbtn = findViewById<Button>(R.id.backbtn)

        Backbtn.setOnClickListener()
        {
            startActivity(Intent(this@NewSightPage, MainPageFrame::class.java).apply
            {
                if (loginId != null) {
                    intent.putExtra("loginId", loginId.toInt())
                }
            })
            NewSightPage().finish()
        }
        val Savebtn = findViewById<Button>(R.id.Savebtn)

        Savebtn.setOnClickListener()
        {
            val species = speciesNameTxt.text.toString().trim()
            val hotspot = hotspotTxt.text.toString().trim()
            val behaviour = behaviourTxt.text.toString().trim()
            val notes = notesTxt.text.toString().trim()

            if (species.isEmpty() || hotspot.isEmpty() || behaviour.isEmpty() || notes.isEmpty()) {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            } else {
                saveObservation(species, hotspot, behaviour, notes, loginId)
            }
        }
    }

    private fun saveObservation(species: String, hotspot: String, behaviour: String, notes: String, loginID: Int) {
        val dateSeen = getCurrentDateAsString()
        val observation = hashMapOf(
            "SpeciesName" to species,
            "Hotspot" to hotspot,
            "Behaviour" to behaviour,
            "Notes" to notes,
            "LoginID" to loginID,
            "DateSeen" to dateSeen
        )

        val observationsCollection = db.collection("Observations")
        observationsCollection.add(observation)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "Observation added: ${documentReference.id}", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@NewSightPage, MainPageFrame::class.java).apply
                {
                    if (loginId != null) {
                        intent.putExtra("loginId", loginId.toInt())
                    }
                })
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error adding observation: $e", Toast.LENGTH_SHORT).show()
            }
    }
    fun getCurrentDateAsString(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val date = Date()
        return dateFormat.format(date)
    }

}
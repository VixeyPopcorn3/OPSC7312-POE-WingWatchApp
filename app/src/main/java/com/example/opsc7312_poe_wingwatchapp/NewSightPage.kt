package com.example.opsc7312_poe_wingwatchapp
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
        //Log.d("login", loginId.toString())

        speciesNameTxt = findViewById<EditText>(R.id.SpeciesNameEtxt)
        hotspotTxt = findViewById<EditText>(R.id.HotspotEtxt)
        behaviourTxt = findViewById<EditText>(R.id.BehaviourEtxt)
        notesTxt = findViewById<EditText>(R.id.NotesEtxt)

        val Backbtn = findViewById<Button>(R.id.backbtn)

        Backbtn.setOnClickListener()
        {
            val intent = Intent(this@NewSightPage, MainPageFrame::class.java)

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

        val observationsCollection = db.collection("Observations")

        // Check if the species already exists in the user's observations
        val matchingSpeciesQuery = observationsCollection.whereEqualTo("LoginID", loginID)
            .whereEqualTo("SpeciesName", species)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty()) {
                    // If the species is not found, update the "Species" field in the "Users" collection
                    val userCollection = db.collection("Users")
                    userCollection.whereEqualTo("LoginID", loginID)
                        .get()
                        .addOnSuccessListener { userDocuments ->
                            if (!userDocuments.isEmpty) {
                                val userDoc = userDocuments.documents[0]
                                val currentSight = userDoc.get("Sight") as Long
                                userDoc.reference.update("Sight", currentSight + 1)

                                val currentSpecies = userDoc.get("Species") as Long
                                userDoc.reference.update("Species", currentSpecies + 1)

                                // Now that the necessary user updates are made, add the observation
                                val observation = hashMapOf(
                                    "SpeciesName" to species,
                                    "Hotspot" to hotspot,
                                    "Behaviour" to behaviour,
                                    "Notes" to notes,
                                    "LoginID" to loginID,
                                    "DateSeen" to dateSeen
                                )

                                observationsCollection.add(observation)
                                    .addOnSuccessListener { documentReference ->
                                        Toast.makeText(this, "Observation added: ${documentReference.id}", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this@NewSightPage, MainPageFrame::class.java)
                                        val bundle = Bundle()
                                        bundle.putInt("loginId", loginID)
                                        intent.putExtras(bundle)
                                        startActivity(intent)
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this, "Error adding observation: $e", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        }
                } else {
                    // If the species is found, just add the observation without updating user fields
                    val observation = hashMapOf(
                        "SpeciesName" to species,
                        "Hotspot" to hotspot,
                        "Behaviour" to behaviour,
                        "Notes" to notes,
                        "LoginID" to loginID,
                        "DateSeen" to dateSeen
                    )

                    observationsCollection.add(observation)
                        .addOnSuccessListener { documentReference ->
                            Toast.makeText(this, "Observation added: ${documentReference.id}", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@NewSightPage, MainPageFrame::class.java)
                            val bundle = Bundle()
                            bundle.putInt("loginId", loginID)
                            intent.putExtras(bundle)
                            startActivity(intent)
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error adding observation: $e", Toast.LENGTH_SHORT).show()
                        }
                }
            }
    }

    private fun saveObservation1(species: String, hotspot: String, behaviour: String, notes: String, loginID: Int) {
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
        //val usersCollection = db.collection("Users")

        observationsCollection.add(observation)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "Observation added: ${documentReference.id}", Toast.LENGTH_SHORT).show()

                // Update the "Sight" field in the "Users" collection linked to the user by incrementing it by one
                val usersCollection = db.collection("Users")
                val userDocument = usersCollection.whereEqualTo("LoginID", loginID)
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            val currentSight = document.get("Sight") as Long
                            document.reference.update("Sight", currentSight + 1)
                        }
                    }

                // Check if the species already exists in the user's observations
                val matchingSpeciesQuery = observationsCollection.whereEqualTo("LoginID", loginID)
                    .whereEqualTo("SpeciesName", species)
                    .get()
                    .addOnSuccessListener { documents ->
                        if (documents.isEmpty()) {
                            // If the species is not found, update the "Species" field in the "Users" collection
                            for (document in documents) {
                                val currentSpecies = document.get("Species") as Long
                                document.reference.update("Species", currentSpecies + 1)
                            }
                        }
                    }
                val intent = Intent(this@NewSightPage, MainPageFrame::class.java)

                if (loginId != null) {
                    val bundle = Bundle()
                    bundle.putInt("loginId", loginId)
                    intent.putExtras(bundle)
                }

                startActivity(intent)
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
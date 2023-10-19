package com.example.opsc7312_poe_wingwatchapp

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*


class ObservationsFragment : Fragment() {
    private lateinit var spinner: Spinner
    private lateinit var recyclerView: RecyclerView
    private lateinit var observationAdapter: ObservationsAdapter
    private val observationsList: MutableList<Observations> = mutableListOf()

    private var loginId: Int = 0
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_observations2, container, false)

        loginId = arguments?.getInt("loginId") ?: 0

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        observationAdapter = ObservationsAdapter(observationsList)
        recyclerView.adapter = observationAdapter

        val newSightbtn = view.findViewById<Button>(R.id.newSightbtn)
        newSightbtn.setOnClickListener {
            // Create an Intent to open the NewActivity
            val intent = Intent(requireContext(), NewSightPage::class.java)

            val fragment = ObservationsFragment()
            val bundle = Bundle()
            bundle.putInt("loginId", loginId.toInt()) // Cast loginId to an integer
            fragment.arguments = bundle

            startActivity(intent)
            // Finish the current activity to close it
            MainPageFrame().finish()
        }

        val caltxt = view.findViewById<TextView>(R.id.caltxt)
        val calendarBtn = view.findViewById<Button>(R.id.calendarbtn)

        // Set an OnClickListener for the calendar button
        calendarBtn.setOnClickListener {
            showDatePicker(caltxt)
        }
        val filterBtn = view.findViewById<Button>(R.id.Filterbtn)

        // Set an OnClickListener for the calendar button
        filterBtn.setOnClickListener {
            filter()
        }

        // Initialize the Spinner
        spinner = view.findViewById(R.id.LocationSpinner)
        fetchLocationfromDb()

        // Call fetchObservations to populate the observationsList
        fetchObservations()

        return view
    }
    // Function to show the date picker dialog
    private fun showDatePicker(caltxt: TextView) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                // Update the caltxt TextView with the selected date
                val formattedDate = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear)
                caltxt.text = formattedDate
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun filter()
    {

    }
    private fun fetchLocationfromDb()
    {
        // Fetch "Hotspot" values from the "Observations" collection for the specific loginId
        db.collection("Observations")
            .whereEqualTo("LoginID", loginId) // Replace with your actual field name and value
            .get()
            .addOnSuccessListener { querySnapshot ->
                val hotspotList = mutableListOf<String>()

                for (document in querySnapshot) {
                    val hotspot = document.getString("Hotspot")
                    if (hotspot != null) {
                        hotspotList.add(hotspot)
                    }
                }

                // Create an ArrayAdapter with the hotspotList
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item, // Use the correct layout resource
                    hotspotList
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                // Apply the adapter to the spinner
                spinner.adapter = adapter
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error in loading Locations Seen: $e", Toast.LENGTH_SHORT).show()
            }

        // Handle item selection
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                // Do something with the selected item
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle nothing selected if needed
            }
        }
    }

        //fetches observations where saved
        private fun fetchObservations() {
            // Clear the existing observations
            observationsList.clear()

            // Fetch observations from Firestore for the specific loginId
            db.collection("Observations")
                .whereEqualTo("LoginID", loginId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot) {
                        val species = document.getString("SpeciesName")
                        val locationSeen = document.getString("Hotspot")
                        val dateSeen = document.getString("DateSeen")
                        val behaviour = document.getString("Behaviour")

                        if (species != null && locationSeen != null && dateSeen != null && behaviour != null) {
                            val observation = Observations(species, locationSeen, dateSeen, behaviour, loginId)
                            observationsList.add(observation)
                        }
                    }

                    // Notify the adapter that the data has changed
                    observationAdapter.notifyDataSetChanged()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error in loading observations: $e", Toast.LENGTH_SHORT).show()
                }
        }

}

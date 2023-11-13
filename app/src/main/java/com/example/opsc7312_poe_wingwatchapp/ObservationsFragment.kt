package com.example.opsc7312_poe_wingwatchapp

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
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

    private var selectedDate: String? = null
    private var selectedLocation: String = ""
    private lateinit var caltxt: TextView
    private var temp: Boolean = false

    private var loginId: Int = 0
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_observations2, container, false)

        loginId = arguments?.getInt("loginId") ?: 0
        Log.d("login obs frag", loginId.toString())

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
            bundle.putInt("loginId", loginId) // Cast loginId to an integer
            intent.putExtras(bundle)
            fragment.arguments = bundle

            startActivity(intent)
            // Finish the current activity to close it
            activity?.finish()
        }

        // Initialize the Spinner
        spinner = view.findViewById(R.id.LocationSpinner)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Store the selected location
                selectedLocation = parent.getItemAtPosition(position).toString()
                temp = true
                Log.d("Loc", "through")
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle nothing selected if needed
                selectedLocation = ""
                Log.d("Loc nothing","nik")
            }
        }

        caltxt = view.findViewById<TextView>(R.id.caltxt)
        val calendarBtn = view.findViewById<Button>(R.id.calendarbtn)

        // Set an OnClickListener for the calendar button
        calendarBtn.setOnClickListener {
            showDatePicker(caltxt)
        }

        val filterBtn = view.findViewById<Button>(R.id.Filterbtn)
        //Log.d("Loc", "x" + spinner.selectedItem.toString()?: "")

        // Set an OnClickListener for the filter button
        filterBtn.setOnClickListener {
            selectedDate = caltxt.text.toString()
            if(spinner.selectedItem != null)
            {
                selectedLocation = spinner.selectedItem.toString()
            }
            filter(selectedDate.toString(), selectedLocation.toString())
        }

        fetchObservations()
        fetchLocationfromDb()

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
                val formattedDate = String.format("%02d-%02d-%d", selectedDay, selectedMonth + 1, selectedYear)
                caltxt.text = formattedDate
                // Store the formatted date in the selectedDate property for filtering
                selectedDate = formattedDate
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun filter(selectDate: String, selectLoc: String) {
        val date = selectDate
        val location = selectLoc

        // Check if both date and location are not null or empty
        if ((!date.isNullOrBlank() && !location.isNullOrBlank()) || temp) {
            // Filter the observations based on selected date and location
            val filteredObservations = observationsList.filter { observation ->
                (selectedDate.isNullOrBlank() || observation.dateSeen == date) &&
                        (selectedLocation.isNullOrBlank() || observation.locationSeen == location)
            }

            // Update the adapter with filtered observations
            observationAdapter.updateData(filteredObservations)

            // Check if there are no filtered observations
            if (filteredObservations.isEmpty()) {
                Toast.makeText(context, "No observations match the filter criteria", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Handle the case when no filters are selected
            Toast.makeText(context, "Please select at least one filter", Toast.LENGTH_SHORT).show()
        }
    }



    private fun fetchLocationfromDb() {
        // Fetch "Hotspot" values from the "Observations" collection for the specific loginId
        db.collection("Observations")
            .whereEqualTo("LoginID", loginId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val hotspotSet = mutableSetOf<String>() // Using a Set to prevent duplicates

                for (document in querySnapshot) {
                    val hotspot = document.getString("Hotspot")
                    if (hotspot != null) {
                        hotspotSet.add(hotspot)
                    }
                }

                val hotspotList = hotspotSet.toList() // Convert Set to List

                // Create an ArrayAdapter with the hotspotList
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
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
                //selectedItem = parent.getItemAtPosition(position).toString()
                selectedLocation = parent.getItemAtPosition(position).toString()
                temp = true
                Log.d("Loc", "through")

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle nothing selected if needed
            }
        }
    }
        private fun fetchObservations() {
            // Clear the existing observations
            observationsList.clear()

            // Create a Firestore query for observations filtered by loginId
            var query = db.collection("Observations")
                .whereEqualTo("LoginID", loginId)

            // Apply additional filters based on the selected date and location
            if (!selectedDate.isNullOrBlank()) {
                query = query.whereEqualTo("DateSeen", selectedDate)
            }
            if (!selectedLocation.isNullOrBlank()) {
                query = query.whereEqualTo("Hotspot", selectedLocation)
            }

            // Execute the query
            query.get()
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

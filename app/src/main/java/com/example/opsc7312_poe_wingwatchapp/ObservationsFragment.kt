package com.example.opsc7312_poe_wingwatchapp

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*


class ObservationsFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var observationAdapter: ObservationsAdapter
    private val observationsList: MutableList<Observations> = mutableListOf()

    private lateinit var loginId: String
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_observations2, container, false)

        loginId = arguments?.getString("loginId") ?: ""

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        observationAdapter = ObservationsAdapter(observationsList)
        recyclerView.adapter = observationAdapter

        val newSightbtn = view.findViewById<Button>(R.id.newSightbtn)
        newSightbtn.setOnClickListener {
            // Create an Intent to open the NewActivity
            val intent = Intent(requireContext(), NewSightPage::class.java)
            intent.putExtra("loginId", loginId)
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

    /*override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //loginId = arguments?.getString("loginId") ?: ""

        //fetchObservations()

        val newSightbtn = view.findViewById<Button>(R.id.newSightbtn)
        newSightbtn.setOnClickListener {
            // Create an Intent to open the NewActivity
            val intent = Intent(MainPageFrame(), NewSightPage::class.java)
            startActivity(intent)
            // Finish the current activity to close it
            MainPageFrame().finish()
            }
        }*/


        //fetches observations where saved //Still need to code
        private fun fetchObservations() {
            /*db.collection("Projects")
                .whereEqualTo("LoginID", loginId)
                //.orderBy("StartDate")
                .get()
                .addOnSuccessListener { documents ->
                    projectsList.clear()
                    for (document in documents) {
                        val projectName = document.getString("Name")
                        val client = document.getString("Client")
                        val startDate = document.getString("StartDate")
                        val endDate = document.getString("EndDate")
                        val projectID = document.getString("ProjectID")

                        val totalHours = calculateTotalHours(projectID.toString())

                        val project = Project(projectName, client, startDate, endDate,totalHours,projectID)
                        projectsList.add(project)
                    }
                    projectAdapter.notifyDataSetChanged()
                }
                .addOnFailureListener { exception ->
                    // Handle any errors
                */}

}

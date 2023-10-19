package com.example.opsc7312_poe_wingwatchapp

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class ObservationsFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var observationAdapter: ObservationsAdapter
    private val observationsList: MutableList<Observations> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_observations2, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        observationAdapter = ObservationsAdapter(observationsList)
        recyclerView.adapter = observationAdapter

        val newSightbtn = view.findViewById<Button>(R.id.newSightbtn)
        newSightbtn.setOnClickListener {
            // Create an Intent to open the NewActivity
            val intent = Intent(requireContext(), NewSightPage::class.java)
            startActivity(intent)
            // Finish the current activity to close it
            MainPageFrame().finish()
        }

        return view
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

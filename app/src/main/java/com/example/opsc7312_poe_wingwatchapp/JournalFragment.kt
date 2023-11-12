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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class JournalFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var journalAdapter: JournalAdapter
    private val journalList: MutableList<Journal> = mutableListOf()

    private lateinit var caltxt: TextView

    private var loginId: Int = 0
    private val db = Firebase.firestore
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_journal2, container, false)


        loginId = arguments?.getInt("loginId") ?: 0

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        journalAdapter = JournalAdapter(journalList)
        recyclerView.adapter = journalAdapter

        val newEntrybtn = view.findViewById<Button>(R.id.newEntrybtn)
        newEntrybtn.setOnClickListener {
            // Create an Intent to open the NewActivity
            val intent = Intent(requireContext(), JournalEntryPage::class.java)

            val fragment = JournalFragment()
            val bundle = Bundle()
            bundle.putInt("loginId", loginId) // Cast loginId to an integer
            intent.putExtras(bundle)
            fragment.arguments = bundle

            startActivity(intent)
            // Finish the current activity to close it
            activity?.finish()
        }

        fetchJournal()

        return view
    }

    private fun fetchJournal() {
        // Clear the existing journal
        journalList.clear()

        // Create a Firestore query for observations filtered by loginId
        var query = db.collection("Journal")
            .whereEqualTo("LoginID", loginId)

        // Execute the query
        query.get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    val entry = document.getString("Entry")
                    val date = document.getString("Date")

                    if (entry != null && date != null) {
                        val journal = Journal(entry, date,loginId)
                        journalList.add(journal)
                    }
                }

                // Notify the adapter that the data has changed
                journalAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error in loading Journal Entries: $e", Toast.LENGTH_SHORT).show()
            }
    }


}

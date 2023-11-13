package com.example.opsc7312_poe_wingwatchapp

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class ComandAchieFragment : Fragment() {

    private lateinit var usernameTxt: TextView
    private lateinit var sightTxt: TextView
    private lateinit var speciesTxt: TextView

    private var loginId: Int = 0
    private val db = Firebase.firestore


    private lateinit var leadSpeciesAdapter: LeadSpeciesAdapter
    private lateinit var leadSightAdapter: LeadSightAdapter
    private lateinit var leadSpeciesRecycler: RecyclerView
    private lateinit var leadSightRecycler: RecyclerView
    private lateinit var speciesMedal: ImageView
    private lateinit var sightMedal: ImageView
    private lateinit var keyBtn: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_comand_achie, container, false)

        loginId = arguments?.getInt("loginId") ?: 0

        usernameTxt = view.findViewById<TextView>(R.id.userNametxt)
        sightTxt = view.findViewById<TextView>(R.id.userSighttxt)
        speciesTxt = view.findViewById<TextView>(R.id.userSpeciestxt)
        speciesMedal = view.findViewById(R.id.speciesMedal)
        sightMedal = view.findViewById(R.id.sightMedal)
        keyBtn = view.findViewById(R.id.imageButton)

        // Initialize RecyclerViews and Adapters
        leadSpeciesRecycler = view.findViewById(R.id.leadSpeciesRecycler)
        leadSightRecycler = view.findViewById(R.id.leadSightRecycler)

        leadSpeciesRecycler.layoutManager = LinearLayoutManager(context)
        leadSightRecycler.layoutManager = LinearLayoutManager(context)

        leadSpeciesAdapter = LeadSpeciesAdapter()
        leadSightAdapter = LeadSightAdapter()

        leadSpeciesRecycler.adapter = leadSpeciesAdapter
        leadSightRecycler.adapter = leadSightAdapter

        getDets(usernameTxt, sightTxt, speciesTxt)
        fetchLeadSpeciesData()
        fetchLeadSightData()


        keyBtn.setOnClickListener()
        {
            val dialogBuilder = AlertDialog.Builder(requireContext())
            val dialogView = layoutInflater.inflate(R.layout.popup_image, null)
            val imageView = dialogView.findViewById<ImageView>(R.id.iv_popup_image)
            // Set the image to display in the popup
            imageView.setImageResource(R.drawable.badge_system)

            dialogBuilder.setView(dialogView)
            val alertDialog = dialogBuilder.create()
            alertDialog.show()

            alertDialog.setOnCancelListener {
                alertDialog.dismiss()
            }
        }



        return view
    }


    private fun getDets(usernameTxt: TextView, sightTxt: TextView, speciesTxt: TextView) {
        db.collection("Users")
            .whereEqualTo("LoginID", loginId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val userName = document.getString("Username")
                    val sight = document.getLong("Sight")
                    val species = document.getLong("Species")

                    if (userName != null && sight != null && species != null) {
                        val formattedUsername = "<i>$userName</i>"
                        val formattedSight = "Sightings:        <i>${sight.toString()}</i>"
                        val formattedSpecies = "Species:           <i>${species.toString()}</i>"

                        usernameTxt.text = Html.fromHtml(formattedUsername, Html.FROM_HTML_MODE_LEGACY)
                        sightTxt.text = Html.fromHtml(formattedSight, Html.FROM_HTML_MODE_LEGACY)
                        speciesTxt.text = Html.fromHtml(formattedSpecies, Html.FROM_HTML_MODE_LEGACY)

                        // Get colors based on rules
                        val sightColor = getColorForSight(sight)
                        val speciesColor = getColorForSpecies(species)
                        if(sight<25)
                        {
                            sightMedal.visibility = View.GONE
                        }
                        else
                        {
                            updateInnerCircleColor(sightMedal, sightColor)
                        }
                        if(species<15)
                        {
                            speciesMedal.visibility = View.GONE
                        }
                        else
                        {
                            updateInnerCircleColor(speciesMedal, speciesColor)
                        }

                    }
                }
            }
            .addOnFailureListener {
                // Handle any errors
            }
    }
    private fun updateInnerCircleColor(circleImageView: ImageView, color: Int) {
        val drawable = circleImageView.drawable

        if (drawable is LayerDrawable) {
            val innerCircle = drawable.findDrawableByLayerId(R.id.innerCircle) as GradientDrawable
            innerCircle.setColor(color)
        }
    }
    private fun getColorForSight(sightCount: Long): Int {
        return when (sightCount) {
            in 0..24L -> Color.parseColor("#7cfc00")
            in 25..49L -> Color.parseColor("#CD7F32")
            in 50..74L -> Color.parseColor("#C0C0C0")
            in 75..99L -> Color.parseColor("#FFD700")
            in 100..149L -> Color.parseColor("#EDF8E5")
            in 150..199L -> Color.parseColor("#B9F2FF")
            in 200..249L -> Color.parseColor("#FF66C4")
            else -> Color.parseColor("#8C52FF")
        }
    }

    private fun getColorForSpecies(speciesCount: Long): Int {
        return when (speciesCount) {
            in 0..14L -> Color.parseColor("#7cfc00")
            in 15..24L -> Color.parseColor("#000000")
            in 25..49L -> Color.parseColor("#CD7F32")
            in 50..74L -> Color.parseColor("#C0C0C0")
            in 75..99L -> Color.parseColor("#FFD700")
            in 100..149L -> Color.parseColor("#EDF8E5")
            in 150..199L -> Color.parseColor("#B9F2FF")
            in 200..249L -> Color.parseColor("#FF66C4")
            else -> Color.parseColor("#8C52FF")
        }
    }

    private fun fetchLeadSpeciesData() {
        // Fetch and populate lead species data
        db.collection("Users")
            .whereNotEqualTo("LoginID", loginId)  // Exclude the current user
            .get()
            .addOnSuccessListener { documents ->
                val speciesList = mutableListOf<Community>()
                for (document in documents) {
                    val userName = document.getString("Username")
                    val species = document.getLong("Species")

                    if (userName != null && species != null) {
                        val community = Community(userName, species.toInt(), 0)
                        speciesList.add(community)
                    }
                }
                leadSpeciesAdapter.submitList(speciesList)
            }
            .addOnFailureListener { exception ->
                // Handle any errors
            }
    }

    private fun fetchLeadSightData() {
        // Fetch and populate lead sight data
        db.collection("Users")
            .whereNotEqualTo("LoginID", loginId)  // Exclude the current user
            .get()
            .addOnSuccessListener { documents ->
                val sightList = mutableListOf<Community>()
                for (document in documents) {
                    val userName = document.getString("Username")
                    val sight = document.getLong("Sight")

                    if (userName != null && sight != null) {
                        val community = Community(userName, 0, sight.toInt())
                        sightList.add(community)
                    }
                }
                leadSightAdapter.submitList(sightList)
            }
            .addOnFailureListener { exception ->
                // Handle any errors
            }
    }
}

package com.example.opsc7312_poe_wingwatchapp

import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class ComandAchieFragment : Fragment() {

    private lateinit var usernameTxt: TextView
    private lateinit var sightTxt: TextView
    private lateinit var speciesTxt: TextView

    private var loginId: Int = 0
    private val db = Firebase.firestore

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

        getDets(usernameTxt, sightTxt, speciesTxt)

        return view
    }

    private fun getDets(usernameTxt: TextView, sightTxt: TextView, speciesTxt: TextView)
    {
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
                    }
                }
            }
            .addOnFailureListener { exception ->
                // Handle any errors
            }
    }
}
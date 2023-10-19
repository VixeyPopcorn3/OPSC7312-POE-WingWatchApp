package com.example.opsc7312_poe_wingwatchapp
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfandSetFragment : Fragment() {

    private lateinit var spinner: Spinner
    private lateinit var seekBar: SeekBar
    private lateinit var distanceUnitsEtxt: TextView
    private lateinit var distanceUnitstxt: TextView
    private lateinit var usernameTxt: TextView
    private lateinit var emailTxt: TextView

    private var selectedDistanceUnit: String = "m"

    private var loginId: Int = 0
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profand_set, container, false)

        loginId = arguments?.getInt("loginId") ?: 0

        usernameTxt = view.findViewById<TextView>(R.id.Usernametxt)
        emailTxt = view.findViewById<TextView>(R.id.Emailtxt)

        /* Initialize the Spinner
        spinner = view.findViewById(R.id.Units_spinner)
        // Define an ArrayAdapter for the Spinner
        val unitsAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.units_array, // Create this array in your strings.xml
            android.R.layout.simple_spinner_item
        )
        // Set the dropdown view resource
        unitsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set the ArrayAdapter to the Spinner
        spinner.adapter = unitsAdapter*/

        // Initialize the Spinner
        spinner = view.findViewById(R.id.Units_spinner)
        // Define an ArrayAdapter for the Spinner
        val unitsAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.units_array, // Create this array in your strings.xml
            android.R.layout.simple_spinner_item
        )
        distanceUnitstxt = view.findViewById(R.id.DistanceUnitstxt)
        // Set the dropdown view resource
        unitsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set the ArrayAdapter to the Spinner
        spinner.adapter = unitsAdapter

        // Add an OnItemSelectedListener to the Spinner
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Get the selected item from the Spinner
                val selectedUnit = parent?.getItemAtPosition(position).toString()

                // Update the "DistanceUnitsEtxt" TextView based on the selected unit
                if (selectedUnit == "Imperial") {
                    distanceUnitstxt.text = "m" // Change to "m" for Imperial
                } else if (selectedUnit == "Metric") {
                    distanceUnitstxt.text = "Km" // Change to "Km" for Metric
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle the case when nothing is selected (if needed)
            }
        }


        seekBar = view.findViewById(R.id.seekBar)
        distanceUnitsEtxt = view.findViewById(R.id.DistanceUnitsEtxt)

        // Add a listener to the SeekBar
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // Update the EditText with the current SeekBar value
                distanceUnitsEtxt.text = progress.toString()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // This method is called when the user starts to drag the SeekBar
            }
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // This method is called when the user stops dragging the SeekBar
            }
        })


        val Applybtn = view.findViewById<Button>(R.id.ApplyBtn)
        Applybtn.setOnClickListener()
        {
            SettingsChange()
        }

        getDets(usernameTxt, emailTxt)

        // Inflate the layout for this fragment
        return view
    }

    private fun SettingsChange()
    {
        //Code for settings change
    }
    private fun getDets(usernameTxt: TextView, emailTxt: TextView) {
        db.collection("Users")
            .whereEqualTo("LoginID", loginId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val userName = document.getString("Username")
                    val email = document.getString("Email")

                    if (userName != null && email != null) {
                        val formattedUsername = "      Username: <i>$userName</i>"
                        val formattedEmail = "      Email: <i>$email</i>"

                        usernameTxt.text = Html.fromHtml(formattedUsername, Html.FROM_HTML_MODE_LEGACY)
                        emailTxt.text = Html.fromHtml(formattedEmail, Html.FROM_HTML_MODE_LEGACY)
                    }
                }
            }
            .addOnFailureListener { exception ->
                // Handle any errors
            }
    }
}
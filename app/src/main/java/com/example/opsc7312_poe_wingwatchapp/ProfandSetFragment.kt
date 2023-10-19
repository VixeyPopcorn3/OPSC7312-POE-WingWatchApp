package com.example.opsc7312_poe_wingwatchapp
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

class ProfandSetFragment : Fragment() {

    private lateinit var spinner: Spinner
    private lateinit var seekBar: SeekBar
    private lateinit var distanceUnitsEtxt: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profand_set, container, false)

        // Initialize the Spinner
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
        spinner.adapter = unitsAdapter


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

        // Inflate the layout for this fragment
        return view
    }
    private fun SettingsChange()
    {
        //Code for settings change
    }
}
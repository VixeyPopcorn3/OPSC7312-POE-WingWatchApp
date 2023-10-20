package com.example.opsc7312_poe_wingwatchapp

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException
import java.lang.Math.*
import kotlin.math.pow

class HotspotDetsPage : AppCompatActivity() {


    private lateinit var nameTxt: TextView
    private lateinit var locationTxt: TextView
    private lateinit var speciesTxt: TextView
    private lateinit var locationDistTxt: TextView
    private var loginId: Int = 0
    private lateinit var subNate2: String
    private lateinit var hotspotName: String
    private lateinit var locName: String
    private var hLat: Double = 0.0
    private var hLong: Double = 0.0
    private var uLat: Double = 0.0
    private var uLong: Double = 0.0

    private lateinit var requestQueue: RequestQueue
    private val eBirdApiKey = "m1gcp6fdtt7b"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hotspot_dets_page)

        requestQueue = Volley.newRequestQueue(this)

        loginId = intent.getIntExtra("loginId", 0)
        hotspotName = intent.getStringExtra("hotspot_name") ?: ""
        subNate2 = intent.getStringExtra("subNate2") ?: ""
        locName = intent.getStringExtra("locName") ?: ""
        hLat =  intent.getDoubleExtra("hLat", 0.0)
        hLong =  intent.getDoubleExtra("hLong", 0.0)
        uLat =  intent.getDoubleExtra("uLat", 0.0)
        uLong =  intent.getDoubleExtra("uLong", 0.0)

        //Toast.makeText(this, subNate2, Toast.LENGTH_SHORT).show()

        nameTxt = findViewById<TextView>(R.id.Nametxt)
        locationTxt = findViewById<TextView>(R.id.Locationtxt)
        speciesTxt = findViewById<TextView>(R.id.DSpeciesFoundtxt)
        locationDistTxt = findViewById(R.id.DistanceFromtxt)


        val Backbtn = findViewById<Button>(R.id.backbtn)

        Backbtn.setOnClickListener()
        {
            startActivity(Intent(this@HotspotDetsPage, MainPageFrame::class.java).apply
            {
                if (loginId != null) {
                    intent.putExtra("loginId", loginId)
                }
            })
            NewSightPage().finish()
        }
        updatePage()
    }
    private fun fetchBirdHotspots()
    {

    }
    private fun updatePage() {
        val url = "https://api.ebird.org/v2/product/spplist/$subNate2?key=$eBirdApiKey"

        val request = StringRequest(
            Request.Method.GET, url,
            { response ->
                try {
                    // Parse the JSON response
                    val jsonArray = JSONArray(response)

                    // Process the data as needed
                    val speciesList = ArrayList<String>()

                    for (i in 0 until jsonArray.length()) {
                        val species = jsonArray.getJSONObject(i).getString("comName")
                        speciesList.add(species)
                    }

                    // Update the UI with the species list
                    speciesTxt.text = speciesList.joinToString("\n")


                } catch (e: JSONException) {
                    // Handle JSON parsing error
                    e.printStackTrace()
                }
            },
            { error ->
                // Handle error
                error.printStackTrace()
            }
        )

        requestQueue.add(request)
        nameTxt.text = hotspotName
        locationTxt.text = locName
        //locationDistTxt.text = calculateDistance().toString() + "km"
        locationDistTxt.text = String.format("%.2f km", calculateDistance())
    }
    private fun calculateDistance(
    ): Double {
        val earthRadius = 6371 // Radius of the Earth in kilometers

        // Convert latitude and longitude from degrees to radians
        val uLatRad = Math.toRadians(uLat)
        val uLongRad = Math.toRadians(uLong)
        val hLatRad = Math.toRadians(hLat)
        val hLongRad = Math.toRadians(hLong)

        // Haversine formula
        val dLat = hLatRad - uLatRad
        val dLong = hLongRad - uLongRad

        val a = sin(dLat / 2).pow(2) + cos(uLatRad) * cos(hLatRad) * sin(dLong / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        // Calculate the distance in kilometers
        return earthRadius * c
    }
}


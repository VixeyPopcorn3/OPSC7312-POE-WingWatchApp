package com.example.opsc7312_poe_wingwatchapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.json.JSONArray
import org.json.JSONException
import java.lang.Math.*
import kotlin.math.pow

class HotspotDetsPage : AppCompatActivity() {


    private lateinit var nameTxt: TextView
    private lateinit var locationTxt: TextView
    private lateinit var speciesTxt: TextView
    private lateinit var locationDistTxt: TextView
    private lateinit var unitsTxt: TextView
    private var loginId: Int = 0
    private lateinit var subNate2: String
    private lateinit var hotspotName: String
    private lateinit var locName: String
    private var hLat: Double = 0.0
    private var hLong: Double = 0.0
    private var uLat: Double = 0.0
    private var uLong: Double = 0.0
    private var userSetDist: Int = 0
    private var userUnits: String = ""

    private lateinit var requestQueue: RequestQueue
    private val eBirdApiKey = "m1gcp6fdtt7b"

    private val db = Firebase.firestore

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
        unitsTxt = findViewById(R.id.Unitstxt)


        val Backbtn = findViewById<Button>(R.id.backbtn)

        Backbtn.setOnClickListener()
        {
            startActivity(Intent(this@HotspotDetsPage, MainPageFrame::class.java).apply
            {
                if (loginId != null) {
                    intent.putExtra("loginId", loginId.toInt())
                }
            })
            HotspotDetsPage().finish()
        }
        updatePage()
    }
    private fun fetchBirdHotspots()
    {

    }
    private fun updatePage() {

        nameTxt.text = hotspotName
        locationTxt.text = locName
        //locationDistTxt.text = calculateDistance().toString() + "km"
        nearbyobs()
        locationDistTxt.text = String.format("%.2f ", calculateDistance())
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
    // still code names to species codes
    private fun speciesList()
    {
        val url = "https://api.ebird.org/v2/product/spplist/$subNate2?key=$eBirdApiKey"
        //https://api.ebird.org/v2/data/obs/geo/recent?lat={{lat}}&lng={{lng}}??

        val request = StringRequest(
            Request.Method.GET, url,
            { response ->
                try {
                    // Parse the JSON response
                    val jsonArray = JSONArray(response)

                    // Process the data as needed
                    val speciesList = ArrayList<String>()

                    for (i in 0 until jsonArray.length()) {
                        val species = jsonArray.getString(i)
                        speciesList.add(species)
                        Log.d("Species", species)
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
    }

    private fun nearbyobs()
    {
        getUserSettings(object : UserSettingsCallback {
            override fun onUserSettingsFetched(userSetDist: String) {
                val url =
                    "https://api.ebird.org/v2/data/obs/geo/recent?lat=$hLat&lng=$hLong&back=5&dist=$userSetDist&hotspot=true&maxResults=50&key=$eBirdApiKey"
                //https://api.ebird.org/v2/data/obs/geo/recent?lat=$hLat&lng=$hLong&back=5&dist=20&hotspot=true&maxResults=50&key=$eBirdApiKey
                //https://api.ebird.org/v2/data/obs/geo/recent?lat={{lat}}&lng={{lng}}??

                val request = StringRequest(
                    Request.Method.GET, url,
                    { response ->
                        try {
                            // Parse the JSON response
                            val jsonArray = JSONArray(response)

                            // Process the data as needed
                            val speciesList = ArrayList<String>()

                            for (i in 0 until jsonArray.length()) {
                                val j = jsonArray.getJSONObject(i)
                                val species = j.getString("comName")
                                speciesList.add(species)
                                //Log.d("Species", species)
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
            }
        })
    }
    private fun getUserSettings(callback: UserSettingsCallback) {
        val settingsRef = db.collection("Settings")

        settingsRef.whereEqualTo("LoginID", loginId)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.first()
                    val distance = document.getLong("Distance")
                    val units = document.getString("Units")

                    if (distance != null && units != null) {
                        if (units.equals("m", ignoreCase = true)) {
                            unitsTxt.text = "m"
                            // Convert miles to kilometers
                            userSetDist = (distance * 1.60934).toInt()
                            callback.onUserSettingsFetched(userSetDist.toString())
                        } else if (units.equals("Km", ignoreCase = true)) {
                            // Use the original distance if units are not "m"
                            unitsTxt.text = "Km"
                            callback.onUserSettingsFetched(distance.toString())

                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to retrieve settings: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
    interface UserSettingsCallback {
        fun onUserSettingsFetched(userSetDist: String)
    }
}


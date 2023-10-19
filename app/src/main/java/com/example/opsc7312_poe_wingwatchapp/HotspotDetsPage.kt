package com.example.opsc7312_poe_wingwatchapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException

class HotspotDetsPage : AppCompatActivity() {


    private lateinit var nameTxt: TextView
    private lateinit var locationTxt: TextView
    private lateinit var speciesTxt: TextView
    private lateinit var locationDistTxt: TextView
    private var loginId: Int = 0

    private lateinit var requestQueue: RequestQueue
    private val eBirdApiKey = "m1gcp6fdtt7b"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hotspot_dets_page)

        loginId = intent.getIntExtra("loginId", 0)
        val hotspotName = intent.getStringExtra("hotspot_name")
        val userLatitude =  intent.getIntExtra("uLat", 0)/* Get user's latitude */
        val userLongitude =  intent.getIntExtra("uLong", 0)/* Get user's longitude */

        nameTxt = findViewById<TextView>(R.id.Nametxt)
        locationTxt = findViewById<TextView>(R.id.Locationtxt)
        speciesTxt = findViewById<TextView>(R.id.DSpeciesFoundtxt)
        locationDistTxt = findViewById(R.id.DistanceFromtxt)

        // Make a network request to eBird to get hotspot details
        if (hotspotName != null) {
            val url = "https://api.ebird.org/v2/ref/hotspot/info/$hotspotName?locale=en_US&fmt=json&key=$eBirdApiKey"
            requestQueue = Volley.newRequestQueue(this)

            val request = StringRequest(
                Request.Method.GET, url,
                Response.Listener<String> { response ->
                    try {
                        // Parse the JSON response
                        val jsonObject = JSONArray(response).getJSONObject(0)
                        val name = jsonObject.getString("locName")
                        val location = jsonObject.getString("subnational2Name")
                        val speciesFound = jsonObject.getString("speciesObserved")
                        val hotspotLatitude = jsonObject.getDouble("lat")
                        val hotspotLongitude = jsonObject.getDouble("lng")


                        /*val distance = calculateDistance(
                            userLatitude,
                            userLongitude,
                            hotspotLatitude,
                            hotspotLongitude
                        )*/

                        // Update the UI with hotspot details
                        nameTxt.text = "Name: $name"
                        locationTxt.text = "Location: $location"
                        speciesTxt.text = "Species Found: $speciesFound"
                        //locationDistTxt.text = "Distance: $distance km"

                    } catch (e: JSONException) {
                        // Handle JSON parsing error
                    }
                },
                Response.ErrorListener { error ->
                    // Handle error
                }
            )

            requestQueue.add(request)
        }
        val Backbtn = findViewById<Button>(R.id.backbtn)

        Backbtn.setOnClickListener()
        {
            startActivity(Intent(this@HotspotDetsPage, MainPageFrame::class.java).apply
            {
                if (loginId != null) {
                    intent.putExtra("loginId", loginId.toInt())
                }
            })
            NewSightPage().finish()
        }
        updatePage(hotspotName.toString())
    }
    private fun updatePage(name: String)
    {

    }
    /*private fun calculateDistance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val radius = 6371 // Earth's radius in kilometers
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return radius * c
    }*/
}


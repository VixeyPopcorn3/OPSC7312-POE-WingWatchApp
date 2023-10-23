package com.example.opsc7312_poe_wingwatchapp

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import android.Manifest
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class MapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var requestQueue: RequestQueue
    private val eBirdApiKey = "m1gcp6fdtt7b"
    private var loginId: Int = 0
    private var uLat: Double = 0.0
    private var uLong: Double = 0.0
    private var subNate2: String =""
    private var locName: String =""
    private var locID: String =""
    private var hLat: Double = 0.0
    private var hLong: Double = 0.0
    private var detsCallback: DetsCallback? = null
    private var userSetDist: Int = 0
    private var userUnits: String = ""

    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map2, container, false)

        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        requestQueue = Volley.newRequestQueue(requireContext())

        loginId = arguments?.getInt("loginId") ?: 0
        Log.d("login frag", loginId.toString())

        val newSightbtn = view.findViewById<Button>(R.id.newSightbtn)
        newSightbtn.setOnClickListener {
            // Create an Intent to open the NewActivity
            val intent = Intent(requireContext(), NewSightPage::class.java)
            intent.putExtra("loginId", loginId)
            startActivity(intent)
            activity?.finish()
        }
        return view
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        enableMyLocation()
        fetchBirdHotspots()
        // Set an OnMarkerClickListener for the map
        googleMap.setOnMarkerClickListener { marker ->
            // Get the hotspot name from the marker's tag
            val hotspotInfo = marker.tag as? HotspotInfo

            if (hotspotInfo != null) {
                // Access the properties from the tag
                val hotspotName = hotspotInfo.hName
                val locID = hotspotInfo.locID
                val hLat = hotspotInfo.hLat
                val hLong = hotspotInfo.hLong

                //Toast.makeText(context, "locID: $locID, hLat: $hLat, hLong: $hLong", Toast.LENGTH_SHORT).show()
                getDets(locID)
                detsCallback = object : DetsCallback {
                    override fun onDetsCompleted() {
                        //Log.d("Response", subNate2)

                        val intent = Intent(requireContext(), HotspotDetsPage::class.java)
                        val extras = Bundle()

                        extras.putString("hotspot_name", hotspotName)
                        extras.putInt("loginId", loginId)
                        extras.putString("subNate2", subNate2)
                        extras.putString("locName", locName)
                        extras.putDouble("uLat", uLat)
                        extras.putDouble("uLong", uLong)
                        extras.putDouble("hLat", hLat)
                        extras.putDouble("hLong", hLong)

                        intent.putExtras(extras)
                        startActivity(intent)
                        activity?.finish()
                    }
                }
                // Return 'true' to indicate that you've handled the click event
                true
            }
            true // Return true to indicate that the click event is handled
        }
    }

    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true
            getMyLocation()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }
    }

    private fun getMyLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener(requireActivity()) { location: Location? ->
            if (location != null) {
                val myLocation = LatLng(location.latitude, location.longitude)
                uLat = location.latitude
                uLong = location.longitude
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15f))
            }
        }
    }

    private fun fetchBirdHotspots() {
        getUserSettings(object : UserSettingsCallback {
            override fun onUserSettingsFetched(userSetDist: String) {
                Log.d("dist", userSetDist)

                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    fusedLocationClient.lastLocation.addOnSuccessListener(requireActivity()) { location: Location? ->
                        if (location != null) {
                            val latitude = location.latitude
                            val longitude = location.longitude
                            val url = "https://api.ebird.org/v2/data/obs/geo/recent?lat=$latitude&lng=$longitude&dist=$userSetDist&key=$eBirdApiKey"

                            val request = StringRequest(
                                Request.Method.GET, url,
                                { response ->
                                    addBirdHotspotsToMap(response)
                                },
                                { error ->
                                    // Handle error
                                }
                            )

                            requestQueue.add(request)
                        }
                    }
                } else {
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        1
                    )
                }
            }
        })
    }

    private fun addBirdHotspotsToMap(jsonResponse: String) {
        try {
            val jsonArray = JSONArray(jsonResponse)

            for (i in 0 until jsonArray.length()) {
                val hotspot = jsonArray.getJSONObject(i)
                val lat = hotspot.getDouble("lat")
                //hLat = lat
                val lng = hotspot.getDouble("lng")
                //hLong = lng
                val name = hotspot.getString("locName")

                locID = hotspot.getString("locId")

                val hotspotInfo = HotspotInfo(name, locID, lat, lng)


                //Toast.makeText(context, locName, Toast.LENGTH_SHORT).show()

                hLat = lat
                hLong = lng
                //subNate2 = hotspot.getString("subnational2Code")
                //locName = hotspot.getString("hierarchicalName")

                val hotspotLocation = LatLng(lat, lng)

                // Customize the marker as needed
                val markerOptions = MarkerOptions()
                    .position(hotspotLocation)
                    .title("Bird Hotspot")
                    .snippet(name)

                val marker = googleMap.addMarker(markerOptions)

                if (marker != null) {
                    marker.tag = hotspotInfo
                }
            }
        } catch (e: JSONException) {
            // Handle JSON parsing error
        }
    }
    data class HotspotInfo(val hName:String, val locID: String, val hLat: Double, val hLong: Double)
    fun getDets(locID: String)
    {
        val url = "https://api.ebird.org/v2/ref/hotspot/info/$locID?key=$eBirdApiKey"

        val request = StringRequest(
            Request.Method.GET, url,
            { response ->
                try {
                    // Parse the JSON response
                    //val jsonArray = JSONObject(response)
                    //Log.d("Response", response)

                    // Process the data as needed
                    val hotspot = JSONObject(response)
                    //val hotspot = jsonArray.getJSONObject()

                    subNate2 = hotspot.getString("subnational2Code")
                    locName = hotspot.getString("hierarchicalName")

                    //Log.d("Response", subNate2)
                    detsCallback?.onDetsCompleted()

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
                            // Convert miles to kilometers
                            userSetDist = (distance * 1.60934).toInt()
                            callback.onUserSettingsFetched(userSetDist.toString())
                        } else if (units.equals("Km", ignoreCase = true)) {
                            // Use the original distance if units are not "m"
                            callback.onUserSettingsFetched(distance.toString())
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Failed to retrieve settings: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
    interface UserSettingsCallback {
        fun onUserSettingsFetched(userSetDist: String)
    }

    interface DetsCallback {
        fun onDetsCompleted()
    }

    override fun onResume() {
    super.onResume()
    mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, perform your actions here
                    getMyLocation()
                    fetchBirdHotspots()
                } else {
                    // Permission denied, handle accordingly
                    Toast.makeText(context, "Location permission denied.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
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
import android.location.Geocoder
import android.net.Uri
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.lang.Math.*
import kotlin.math.asin
import kotlin.math.pow
import android.graphics.Color

class MapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var requestQueue: RequestQueue
    private lateinit var  navigatebtn: Button
    private lateinit var  hotspotDetsbtn: Button
    private val eBirdApiKey = "m1gcp6fdtt7b"

    private var hotspotName: String =""
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

        /*val newSightbtn = view.findViewById<Button>(R.id.newSightbtn)
        newSightbtn.setOnClickListener {
            // Create an Intent to open the NewActivity
            val intent = Intent(requireContext(), NewSightPage::class.java)
            intent.putExtra("loginId", loginId)
            startActivity(intent)
            activity?.finish()
        }*/
        navigatebtn = view.findViewById<Button>(R.id.navigatebtn)
        navigatebtn.visibility = View.GONE

        hotspotDetsbtn = view.findViewById<Button>(R.id.newSightbtn)
        hotspotDetsbtn.visibility = View.GONE
        return view
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        enableMyLocation()
        fetchBirdHotspots()
        markUserObservations()
        var dist: Double = 0.0

        // Set an OnMarkerClickListener for the map
        googleMap.setOnMarkerClickListener { marker ->

            val hotspotInfo = marker.tag as? HotspotInfo

            if (hotspotInfo != null) {
                hotspotName = hotspotInfo.hName
                dist = hotspotInfo.distance

                // Show the hotspot information in a custom view
                //showHotspotInfoView(hotspotName, distance)
                val infoView = showHotspotInfoView(hotspotInfo)

                // Set this view as the info window for the marker
                googleMap.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
                    override fun getInfoWindow(marker: Marker): View? {
                        return null // Returning null here ensures that getInfoContents() is called.
                    }

                    override fun getInfoContents(marker: Marker): View {
                        return infoView // Return the custom view for the info window
                    }
                })

                marker.showInfoWindow() // Show the info window for the marker

                //val view = layoutInflater.inflate(R.layout.fragment_map2, null) // Replace 'your_layout_containing_button' with the actual layout name
                //navigatebtn = view.findViewById<Button>(R.id.navigatebtn)
                navigatebtn.visibility  = View.VISIBLE
                navigatebtn.setOnClickListener {
                    val location = marker.position

                    // Create an intent to open Google Maps on the web browser
                    val navigationUrl = "https://www.google.com/maps/dir/?api=1&destination=${location.latitude},${location.longitude}"

                    // Create an intent to open the browser with the Google Maps URL
                    val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(navigationUrl))

                    // Verify that there's a browser available to handle the intent
                    if (mapIntent.resolveActivity(requireContext().packageManager) != null) {
                        startActivity(mapIntent)
                    } else {
                        // Handle if no browser is available
                        Toast.makeText(requireContext(), "No web browser found", Toast.LENGTH_SHORT).show()
                    }
                }
                hotspotDetsbtn.visibility  = View.VISIBLE
                hotspotDetsbtn.setOnClickListener {
                    // using an Intent:
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
                            extras.putDouble("dist", dist)

                            intent.putExtras(extras)
                            startActivity(intent)
                            activity?.finish()
                        }
                    }
                }

                // Return 'true' to indicate that you've handled the click event
                true
            }
            true // Return true to indicate that the click event is handled
        }

        // Set an OnInfoWindowClickListener to handle clicks on the info window
        googleMap.setOnInfoWindowClickListener { marker ->
            // using an Intent:
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
                    extras.putDouble("dist", dist)

                    intent.putExtras(extras)
                    startActivity(intent)
                    activity?.finish()
                }
            }
        }
    }
    private fun showHotspotInfoView(hotspotInfo: HotspotInfo): View {
        val view = layoutInflater.inflate(R.layout.info_window_layout, null)

        val nameTextView = view.findViewById<TextView>(R.id.info_window_title)
        val distanceTextView = view.findViewById<TextView>(R.id.info_window_distance)

        nameTextView.text = hotspotInfo.hName
        Log.d("distance", hotspotInfo.distance.toString())

        val  dist = String.format("%.2f ",hotspotInfo.distance)
        distanceTextView.text = "Distance: ${dist} Km" // You can format this as needed


        return view
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

                val distance = calculateDistance(lat, lng)
                val hotspotInfo = HotspotInfo(name, locID, lat, lng, distance)


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
    /*private fun addBirdHotspotsToMap1(jsonResponse: String) {
        try {
            val jsonArray = JSONArray(jsonResponse)

            for (i in 0 until jsonArray.length()) {
                val hotspot = jsonArray.getJSONObject(i)
                val lat = hotspot.getDouble("lat")
                val lng = hotspot.getDouble("lng")
                val name = hotspot.getString("locName")
                locID = hotspot.getString("locId")
                hLat = lat
                hLong = lng

                val distance = calculateDistance(lat, lng)
                val hotspotInfo = HotspotInfo(name, locID, lat, lng, distance)
                val hotspotLocation = LatLng(lat, lng)

                // Customize the marker as needed
                val markerOptions = MarkerOptions()
                    .position(hotspotLocation)
                    .title("Bird Hotspot")
                    .snippet(name)

                val marker = googleMap.addMarker(markerOptions)

                if (marker != null) {
                    marker.tag = hotspotInfo

                    // Set a custom InfoWindow adapter
                    googleMap.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
                        override fun getInfoWindow(marker: Marker): View? {
                            // Return null to use the default InfoWindow
                            return null
                        }

                        override fun getInfoContents(marker: Marker): View? {
                            // Inflate the custom InfoWindow layout
                            val view = layoutInflater.inflate(R.layout.info_window_layout, null)

                            // Get references to the TextViews in the layout
                            val titleTextView = view.findViewById<TextView>(R.id.info_window_title)
                            val distanceTextView = view.findViewById<TextView>(R.id.info_window_distance)

                            // Get the hotspot information from the marker's tag
                            val hotspotInfo = marker.tag as? HotspotInfo
                            if (hotspotInfo != null) {
                                // Set the hotspot name and distance in the InfoWindow layout
                                titleTextView.text = hotspotInfo.hName
                                val distance = calculateDistance(lat, lng)
                                distanceTextView.text = "Distance: $distance km"
                            }

                            return view
                        }
                    })
                }
            }
        } catch (e: JSONException) {
            // Handle JSON parsing error
        }
    }*/

    private fun calculateDistance(hotLat: Double, hotLong: Double
    ): Double {
        val earthRadius = 6371 // Radius of the Earth in kilometers

        // Convert latitude and longitude from degrees to radians
        val lat1 = Math.toRadians(uLat)
        val lon1 = Math.toRadians(uLong)
        val lat2 = Math.toRadians(hotLat)
        val lon2 = Math.toRadians(hotLong)


        // Haversine formula
        val dLon = lon2 - lon1
        val dLat = lat2 - lat1

        val a = sin(dLat / 2).pow(2) + cos(lat1) * cos(lat2) * sin(dLon / 2).pow(2)
        val c = 2 * asin(sqrt(a))

        // Calculate the distance in kilometers
        return earthRadius * c
    }

    data class HotspotInfo(val hName:String, val locID: String, val hLat: Double, val hLong: Double, val distance: Double)
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
                    // Log the specific values obtained
                    Log.d("subNate2", subNate2)
                    Log.d("locName", locName)

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

    private fun markUserObservations() {
        val observationsCollection = db.collection("Observations")

        observationsCollection
            .whereEqualTo("LoginID", loginId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val speciesName = document.getString("SpeciesName") ?: ""
                    val hotspot = document.getString("Hotspot") ?: ""
                    val currentLoc = document.getString("currentLoc") ?: ""

                    // If latitude and longitude are present, use these coordinates
                    if (currentLoc.matches(Regex("^-?\\d+\\.\\d+\\|-?\\d+\\.\\d+\$"))) {
                        val (latitude, longitude) = currentLoc.split("|")
                        val observationLocation = LatLng(latitude.toDouble(), longitude.toDouble())
                        placeMarker(observationLocation, speciesName)
                    } else {
                        // If latitude and longitude are not available, use the hotspot string
                        if (hotspot.isNotEmpty()) {
                            // Use a geocoder or some service to get the coordinates for the hotspot
                            val hotspotCoordinates = getCoordinatesForHotspot(hotspot)
                            if (hotspotCoordinates != null) {
                                placeMarker(hotspotCoordinates, speciesName)
                            }
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                // Handle errors
            }
    }

    private fun placeMarker(location: LatLng, title: String) {
        // Place a blue marker for the user's observation
        googleMap.addMarker(MarkerOptions().position(location).title(title).icon(
            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))
    }

    private fun getCoordinatesForHotspot(hotspot: String): LatLng? {
        val geocoder = Geocoder(requireContext())

        try {
            val addresses = geocoder.getFromLocationName(hotspot, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val latitude = addresses[0].latitude
                val longitude = addresses[0].longitude
                return LatLng(latitude, longitude)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
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
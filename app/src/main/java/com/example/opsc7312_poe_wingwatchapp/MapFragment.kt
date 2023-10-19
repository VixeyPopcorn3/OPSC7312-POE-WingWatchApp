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
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.json.JSONArray
import org.json.JSONException

class MapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var requestQueue: RequestQueue
    private val eBirdApiKey = "m1gcp6fdtt7b"
    private lateinit var loginId: String
    //private lateinit var uLat:
    //private lateinit var uLat:

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

        loginId = arguments?.getString("loginId") ?: ""

        val newSightbtn = view.findViewById<Button>(R.id.newSightbtn)
        newSightbtn.setOnClickListener {
            // Create an Intent to open the NewActivity
            val intent = Intent(requireContext(), NewSightPage::class.java)
            intent.putExtra("loginId", loginId)
            startActivity(intent)
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
            val hotspotName = marker.tag as? String
            if (hotspotName != null) {
                // Handle the marker click here, e.g., navigate to the HotspotDetailActivity
                val intent = Intent(requireContext(), HotspotDetsPage::class.java)
                intent.putExtra("hotspot_name", hotspotName)
                intent.putExtra("loginId", loginId)
                //intent.putExtra("uLat", uLat)
                //intent.putExtra("uLong", uLong)
                startActivity(intent)
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
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15f))
            }
        }
    }

    private fun fetchBirdHotspots() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener(requireActivity()) { location: Location? ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    val url = "https://api.ebird.org/v2/data/obs/geo/recent?lat=$latitude&lng=$longitude&dist=10&key=$eBirdApiKey"

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

    private fun addBirdHotspotsToMap(jsonResponse: String) {
        try {
            val jsonArray = JSONArray(jsonResponse)

            for (i in 0 until jsonArray.length()) {
                val hotspot = jsonArray.getJSONObject(i)
                val lat = hotspot.getDouble("lat")
                val lng = hotspot.getDouble("lng")
                val name = hotspot.getString("locName")

                val hotspotLocation = LatLng(lat, lng)

                // Customize the marker as needed
                val markerOptions = MarkerOptions()
                    .position(hotspotLocation)
                    .title("Bird Hotspot")
                    .snippet(name)

                val marker = googleMap.addMarker(markerOptions)

                if (marker != null) {
                    marker.tag = name
                }
            }
        } catch (e: JSONException) {
            // Handle JSON parsing error
        }
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
package com.example.googlemapkotlin

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import com.google.android.gms.maps.SupportMapFragment as SupportMapFragment1

class MainActivity : FragmentActivity(), OnMapReadyCallback {
    private val LOCATION_PERMISSION_REQUEST = 1
    lateinit var map: GoogleMap
    lateinit var mapFragment: SupportMapFragment1
    lateinit var latLng: LatLng
    lateinit var client: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mapFragment = supportFragmentManager
            .findFragmentById(R.id.google_map) as SupportMapFragment1
        mapFragment.getMapAsync(this)
        client = LocationServices.getFusedLocationProviderClient(this)

        search_bar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if(map!= null){
                    map.clear()
                    val searchlocation: String = search_bar.getQuery().toString()
                    var addresses: List<Address>? = null
                    if (searchlocation != null || searchlocation != "") {
                        val geocoder = Geocoder(this@MainActivity)
                        try {
                            addresses = geocoder.getFromLocationName(searchlocation, 1)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                        val address = addresses!![0]
                        latLng = LatLng(address.latitude, address.longitude)
                        map.addMarker(MarkerOptions().position(latLng).title(searchlocation))
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
                    }
                }

                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 44) {
            if (requestCode == LOCATION_PERMISSION_REQUEST) {
                if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return
                    }
                    map.isMyLocationEnabled = true
                } else {
                    Toast.makeText(
                        this,
                        "User has not granted location access permission",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
            }
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        map = p0
        getCurrentLocation()
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        val task = client.lastLocation
        task.addOnSuccessListener { location ->
            if (location != null) {
                mapFragment.getMapAsync(OnMapReadyCallback { googleMap ->
                    latLng = LatLng(location.latitude, location.longitude)
                    val markerOptions = MarkerOptions().position(latLng).title("Here I am")
                    googleMap.addMarker(markerOptions)
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
                })
            }
        }
    }


}
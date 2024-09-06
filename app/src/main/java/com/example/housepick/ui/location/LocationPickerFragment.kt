package com.example.housepick.ui.location

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.housepick.R
import com.example.housepick.databinding.FragmentLocationPickerBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class LocationPickerFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: FragmentLocationPickerBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var centerMarker: Marker? = null
    private var selectedLatLng: LatLng? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentLocationPickerBinding.inflate(inflater, container, false)

        // Initialize fusedLocationClient to get last known location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.confirmLocationButton.setOnClickListener {
            // Use the current center of the map as the selected location
            selectedLatLng = mMap.cameraPosition.target
            selectedLatLng?.let {
                val result = Bundle().apply {
                    putDouble("latitude", it.latitude)
                    putDouble("longitude", it.longitude)
                }
                parentFragmentManager.setFragmentResult("requestKey", result)
                findNavController().popBackStack()
            }
        }

        return binding.root
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Enable the 'My Location' button to move the camera to user's location
        mMap.isMyLocationEnabled = true

        // Move camera to user's current location on the map
        getLastKnownLocation()

        // Add a listener to update the marker when the camera is moved
        mMap.setOnCameraIdleListener {
            // Get the current center of the map (target) and update the marker
            val centerOfMap = mMap.cameraPosition.target
            if (centerMarker == null) {
                // Create the marker for the first time at the center
                centerMarker =
                    mMap.addMarker(MarkerOptions().position(centerOfMap).title("Selected Location"))
            } else {
                // Move the marker to the new center position
                centerMarker?.position = centerOfMap
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12F))
            } else {
                // If location is null, move to a default location
                val defaultLocation = LatLng(-34.0, 151.0)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12F))
            }
        }
    }
}

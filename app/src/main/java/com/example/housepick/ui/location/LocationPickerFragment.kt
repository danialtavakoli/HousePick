package com.example.housepick.ui.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
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

        // Enable the 'My Location' button in Google Maps
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            mMap.uiSettings.isMyLocationButtonEnabled = true
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }

        // Add a listener to update the marker when the camera is moved
        mMap.setOnCameraIdleListener {
            val centerOfMap = mMap.cameraPosition.target
            if (centerMarker == null) {
                centerMarker =
                    mMap.addMarker(MarkerOptions().position(centerOfMap).title("Selected Location"))
            } else {
                centerMarker?.position = centerOfMap
            }
        }

        // Handle the "My Location" button click event
        mMap.setOnMyLocationButtonClickListener {
            moveToUserLocation()
            true // Return true if the event is handled, false otherwise
        }
    }

    @SuppressLint("MissingPermission")
    private fun moveToUserLocation() {
        if (isLocationEnabled()) {
            // If GPS is enabled, proceed with getting the location
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12F))
                } else {
                    // If location is null, move to a default location
                    val defaultLocation = LatLng(-34.0, 151.0)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12F))
                    // Optionally, notify the user that location is unavailable
                    // Toast.makeText(context, "Unable to get your location", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            // If GPS is disabled, prompt the user to enable it
            promptEnableLocation()
        }
    }

    // Check if the GPS is enabled
    private fun isLocationEnabled(): Boolean {
        val locationManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requireContext().getSystemService(LocationManager::class.java)
        } else {
            TODO("VERSION.SDK_INT < M")
        }
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    // Prompt user to enable GPS if it is disabled
    private fun promptEnableLocation() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.enable_gps_title))
            .setMessage(getString(R.string.enable_gps_message))
            .setPositiveButton(getString(R.string.enable_button)) { _, _ ->
                // Redirect the user to the location settings page to enable GPS
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
            .setNegativeButton(getString(R.string.cancel_button), null)
            .show()
    }

    // Check if location services (GPS) are enabled and act accordingly
    private fun checkLocationServicesAndProceed() {
        if (!isLocationEnabled()) {
            promptEnableLocation()  // Prompt the user to enable GPS
        } else {
            getLastKnownLocation()  // Get the last known location if GPS is enabled
        }
    }

    // Get the user's last known location if permissions are granted and GPS is enabled
    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12F))
            } else {
                val defaultLocation = LatLng(-34.0, 151.0)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12F))
            }
        }
    }
}

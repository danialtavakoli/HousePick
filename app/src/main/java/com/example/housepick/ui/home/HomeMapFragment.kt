package com.example.housepick.ui.home


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.housepick.R
import com.example.housepick.databinding.FragmentHomeMapBinding
import com.example.housepick.ui.utils.showSnackBar
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.json.JSONObject
import java.io.IOException
import java.util.Locale


class HomeMapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var homeMapViewModel: HomeMapViewModel

    private var _binding: FragmentHomeMapBinding? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val binding get() = _binding!!
    private lateinit var marker: MarkerOptions

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeMapViewModel = ViewModelProvider(this)[HomeMapViewModel::class.java]
        // Inflate the layout for this fragment
        _binding = FragmentHomeMapBinding.inflate(inflater, container, false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        homeMapViewModel.getAction().observe(
            viewLifecycleOwner
        ) { action -> action?.let { handleAction(it) } }

        /*binding.floatingActionButton.setOnClickListener{
            getLastKnownLocation()
        }*/

        binding.button.setOnClickListener {
            loadHousesAroundCity(binding.editTextTextPersonName.text.toString())
        }

        val root: View = binding.root

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    private fun loadHousesAroundCity(name: String) {
        homeMapViewModel.displayHomesByCity(name)
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        try {
            val addresses: List<Address> =
                geocoder.getFromLocationName(name, 1)!!
            if (addresses.isNotEmpty()) {
                mMap.clear()
                val latLong = LatLng(addresses[0].latitude, addresses[0].longitude)
                val markerOptions = MarkerOptions()
                markerOptions.title(name)
                markerOptions.position(latLong)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerOptions.position, 12F))

            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation() {
        mMap.clear()
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    homeMapViewModel.displayHomesByArea(location.latitude, location.longitude)
                    val latLng = LatLng(location.latitude, location.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12F))
                }

            }

    }

    private fun handleAction(action: Action) {
        if (this::mMap.isInitialized) {
            when (action.value) {
                Action.HOMES_LOADED -> {
                    val homeArray = homeMapViewModel.homesArray
                    for (i in 0 until homeArray.length()) {
                        val obj: JSONObject = homeArray[i] as JSONObject
                        val latLong = JSONObject(obj.get("latlong").toString())
                        val position =
                            LatLng(latLong.getDouble("latitude"), latLong.getDouble("longitude"))
                        marker = MarkerOptions()
                            .position(position)
                            .title(obj.get("title").toString())
                        mMap.addMarker(marker).tag = obj.get("id");

                    }
                }

                Action.NETWORK_ERROR -> {
                    showSnackBar(
                        binding.root,
                        R.string.no_houses_found,
                        R.drawable.mail_box_icon
                    )
                    //Toast.makeText(context, R.string.no_houses_found, Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    private fun BitmapFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        // below line is use to generate a drawable.
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)

        // below line is use to set bounds to our vector drawable.
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )

        // below line is use to create a bitmap for our
        // drawable which we have added.
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )

        // below line is use to add bitmap in our canvas.
        val canvas = Canvas(bitmap)

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas)

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }


    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.isMyLocationEnabled = true
        getLastKnownLocation()

        mMap.setOnMyLocationButtonClickListener {
            mMap.clear()

            val location = mMap.myLocation
            if (location != null) {
                val ll = LatLng(location.latitude, location.longitude)
                val update = CameraUpdateFactory.newLatLngZoom(ll, 12F)
                mMap.animateCamera(update)
                homeMapViewModel.displayHomesByArea(location.latitude, location.longitude)
            } else {
                // Handle the case where the location is not yet available
                showSnackBar(
                    binding.root,
                    R.string.network_error,
                    R.drawable.mail_box_icon
                )
            }
            false

        }

        mMap.setOnMarkerClickListener { marker ->
            val bundle = bundleOf("id" to marker.tag)
            findNavController().navigate(
                R.id.action_navigation_home_to_oneHomeFragment, bundle
            )
            true
        }
    }


}
package com.example.housepick

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.housepick.data.services.NotificationsService
import com.example.housepick.databinding.ActivityAppBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.ArrayList

class AppActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAppBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkAndRequestPermissions()
        binding = ActivityAppBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val serviceIntent = Intent(applicationContext, NotificationsService::class.java)
        startService(serviceIntent)
        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_app)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_notifications, R.id.navigation_ads
            )
        )
        navView.setupWithNavController(navController)
    }

    override fun onResume() {
        super.onResume()
        Application.activityResumed()
    }

    override fun onPause() {
        super.onPause()
        Application.activityPaused()
    }

    private fun checkAndRequestPermissions(): Boolean {
        val internet = Application.appContext?.let {
            ContextCompat.checkSelfPermission(
                it,
                Manifest.permission.INTERNET
            )
        }
        val loc = Application.appContext?.let {
            ContextCompat.checkSelfPermission(
                it,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }
        val loc2 = Application.appContext?.let {
            ContextCompat.checkSelfPermission(
                it,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        if (internet != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.INTERNET)
        }
        if (loc != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if (loc2 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                (this),
                listPermissionsNeeded.toTypedArray(),
                1
            )
            return false
        }
        return true
    }

}
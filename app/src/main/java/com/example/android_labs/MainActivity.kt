package com.example.android_labs

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.android_labs.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.GeoApiContext


class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MapViewModel by viewModels { MapViewModelFactory(application) }
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) viewModel.toMyLocation()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
        setupMapFragment()
    }

    private fun initUI() {
        binding.btnZoomToUser.setOnClickListener {
            checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) { viewModel.toMyLocation() }
        }

        binding.btnZoomToLocation.setOnClickListener {
            viewModel.moveToDefaultLocation()
        }

        binding.btnClear.setOnClickListener {
            viewModel.clearPath()
        }

        viewModel.toastMessage.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                Toast.makeText(this, "Building route...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkPermission(permission: String, action: () -> Unit) {
        when {
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> {
                action()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(this, permission) -> {
                showRationaleDialog(permission)
            }
            else -> {
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    private fun showRationaleDialog(permission: String) {
        AlertDialog.Builder(this)
            .setTitle("Location Permission Needed")
            .setMessage("This app needs the Location permission to show your current location")
            .setPositiveButton("OK") { _, _ ->
                requestPermissionLauncher.launch(permission)
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    private fun setupMapFragment() {
        (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        viewModel.initGoogleMap(googleMap)
    }
}
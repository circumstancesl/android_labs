package com.example.android_labs

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.android_labs.Osrm.OsrmApi
import com.example.android_labs.Osrm.RetrofitProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.IOException

class MapViewModel(application: Application) : AndroidViewModel(application) {
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationManager: LocationManager
    private var startMarker: Marker? = null
    private var endMarker: Marker? = null
    private var routePolyline: Polyline? = null

    private val osrmApi: OsrmApi by lazy {
        RetrofitProvider.getInstance().create(OsrmApi::class.java)
    }

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    val defaultLocation = LatLng(55.354993, 86.085805)

    init {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)
        locationManager = application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    fun initGoogleMap(googleMap: GoogleMap) {
        this.googleMap = googleMap.apply {
            setOnMapLongClickListener { latLng ->
                onMapLongClick(latLng)
            }
        }
    }

    fun toMyLocation() {
        val hasLocationPermission = ActivityCompat.checkSelfPermission(
            getApplication(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            getApplication(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasLocationPermission) {
            _toastMessage.value = "Location permission not granted"
            return
        }

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            _toastMessage.value = "Please enable GPS"
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                moveToPoint(LatLng(it.latitude, it.longitude), 15f)
            } ?: run {
                _toastMessage.value = "Unable to get location"
            }
        }
    }

    fun moveToDefaultLocation() {
        moveToPoint(defaultLocation, 15f)
    }

    private fun moveToPoint(coords: LatLng, zoom: Float) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coords, zoom))
    }

    private fun onMapLongClick(latLng: LatLng) {
        if (startMarker == null) {
            startMarker = googleMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("Start")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            )
            _toastMessage.value = "Start point set"
        } else if (endMarker == null) {
            endMarker = googleMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("End")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            )
            startMarker?.position?.let { start ->
                drawRoute(start, latLng)
            }
        } else {
            _toastMessage.value = "Clear existing route first"
        }
    }

    private fun drawRoute(start: LatLng, end: LatLng) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = withContext(Dispatchers.IO) {
                    osrmApi.getRoute(
                        start = "${start.longitude},${start.latitude}",
                        end = "${end.longitude},${end.latitude}"
                    )
                }

                if (response.isSuccessful) {
                    response.body()?.let { osrmResponse ->
                        if (osrmResponse.routes.isEmpty() || osrmResponse.routes[0].distance == 0f) {
                            _toastMessage.value = "Route not found"
                            clearPath()
                            return@let
                        }

                        val polyline = osrmResponse.routes[0].geometry
                        val decodedPolyline = PolyUtil.decode(polyline)

                        withContext(Dispatchers.Main) {
                            routePolyline?.remove()
                            routePolyline = googleMap.addPolyline(
                                PolylineOptions()
                                    .addAll(decodedPolyline)
                                    .color(Color.BLUE)
                                    .width(12f)
                            )
                            _toastMessage.value = "Route built successfully"
                        }
                    }
                } else {
                    _toastMessage.value = "Failed to build route"
                    clearPath()
                }
            } catch (e: IOException) {
                _toastMessage.value = "Network error: ${e.message}"
                clearPath()
            } catch (e: Exception) {
                _toastMessage.value = "Error: ${e.message}"
                clearPath()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearPath() {
        startMarker?.remove()
        endMarker?.remove()
        routePolyline?.remove()
        startMarker = null
        endMarker = null
        routePolyline = null
        _toastMessage.value = "Map cleared"
    }
}
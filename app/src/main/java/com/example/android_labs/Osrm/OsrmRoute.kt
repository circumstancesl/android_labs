package com.example.android_labs.Osrm

data class OsrmRoute(
    val geometry: String,
    val weight: Float,
    val duration: Float,
    val distance: Float
)
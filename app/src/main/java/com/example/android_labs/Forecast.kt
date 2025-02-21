package com.example.android_labs

data class Forecast(
    val cod: String,
    val message: Double,
    val cnt: Int,
    val list: List<ForecastItem>,
    val city: City
)
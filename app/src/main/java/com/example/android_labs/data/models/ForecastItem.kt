package com.example.android_labs.data.models

data class ForecastItem(
    val dt: Int,
    val main: Main,
    val weather: List<Weather>,
    val clouds: Clouds,
    val wind: Wind,
    val dt_txt: String
)
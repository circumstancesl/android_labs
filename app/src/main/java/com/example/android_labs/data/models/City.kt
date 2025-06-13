package com.example.android_labs.data.models

data class City(
    val id: Int,
    val name: String,
    val coord: Coord,
    val country: String,
    val population: Int
)
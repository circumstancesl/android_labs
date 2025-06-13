package com.example.android_labs.data.api

import com.example.android_labs.BuildConfig
import com.example.android_labs.data.models.Forecast
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherMapService {
    companion object {
        const val api = BuildConfig.API_KEY_OPEN_WEATHER_MAP
        const val constUnits = "metric"
    }
    @GET("forecast")
    fun getForecast(
        @Query("q") city: String,
        @Query("units") units: String = constUnits,
        @Query("appid") apiKey: String = api,
    ): Call<Forecast>
}
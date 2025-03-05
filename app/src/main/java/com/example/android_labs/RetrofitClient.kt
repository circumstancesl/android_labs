package com.example.android_labs

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

object RetrofitClient {

    val weatherService: OpenWeatherMapService by lazy {

        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenWeatherMapService::class.java)
    }
}

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
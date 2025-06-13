package com.example.android_labs.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android_labs.data.api.OpenWeatherMapService
import com.example.android_labs.data.api.RetrofitClient
import com.example.android_labs.data.models.Forecast
import com.example.android_labs.data.models.ForecastItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewModel(
    private val service: OpenWeatherMapService = RetrofitClient.weatherService
) : ViewModel() {

    private val _forecastData = MutableLiveData<List<ForecastItem>>()
    val forecastData: LiveData<List<ForecastItem>> = _forecastData

    private val _toastMessage = MutableLiveData<String?>()
    val toastMessage: LiveData<String?> = _toastMessage

    private val _isCelsius = MutableLiveData(true)
    val isCelsius: LiveData<Boolean> = _isCelsius

    fun fetchWeather(city: String) {
        val call = service.getForecast(city)

        call.enqueue(object : Callback<Forecast> {
            override fun onResponse(call: Call<Forecast>, response: Response<Forecast>) {
                if (response.isSuccessful) {
                    response.body()?.list?.let { forecastList ->
                        _forecastData.value = forecastList
                    } ?: run {
                        _toastMessage.value = "Ошибка формата данных"
                    }
                } else {
                    _toastMessage.value = when (response.code()) {
                        404 -> "Город не найден"
                        else -> "Ошибка сервера: ${response.code()}"
                    }
                }
            }

            override fun onFailure(call: Call<Forecast>, t: Throwable) {
                _toastMessage.value = "Ошибка сети: ${t.localizedMessage}"
            }
        })
    }

    fun convertTemperature(celsius: Double): Double {
        return if (_isCelsius.value == true) celsius else celsius * 9 / 5 + 32
    }

    fun onToastShown() {
        _toastMessage.value = null
    }

    fun toggleTemperatureUnit() {
        _isCelsius.value = !(_isCelsius.value ?: true)
    }
}
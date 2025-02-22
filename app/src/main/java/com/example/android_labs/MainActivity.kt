package com.example.android_labs

import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.ToggleButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.Serializable

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: ViewModel
    private lateinit var adapter: ForecastAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rView)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        viewModel = ViewModel(
            retrofit.create(OpenWeatherMapService::class.java),
            resources
        )

        adapter = ForecastAdapter(ForecastDiffCallback(), viewModel)
        findViewById<RecyclerView>(R.id.rView).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }

        viewModel.forecastData.observe(this) { data ->
            data?.let { adapter.submitList(it) }
        }

        viewModel.isCelsius.observe(this) { isCelsius ->
            findViewById<ToggleButton>(R.id.toggleTempUnit).isChecked = !isCelsius
            adapter.notifyDataSetChanged()
        }

        findViewById<ToggleButton>(R.id.toggleTempUnit).setOnCheckedChangeListener { _, isChecked ->
            viewModel.toggleTemperatureUnit()
        }

        viewModel.toastMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).apply {
                    setGravity(Gravity.CENTER, 0, 0)
                }.show()
                viewModel.onToastShown()
            }
        }

        findViewById<Button>(R.id.btnGetWeather).setOnClickListener {
            val city = findViewById<EditText>(R.id.etCity).text.toString()
            if (city.isNotEmpty()) {
                viewModel.fetchWeather(city)
            }
        }
    }
}
package com.example.android_labs

import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.ToggleButton
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android_labs.ui.ForecastAdapter
import com.example.android_labs.ui.ForecastDiffCallback
import com.example.android_labs.ui.ViewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: ViewModel by viewModels()
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

        adapter = ForecastAdapter(ForecastDiffCallback(), viewModel)

        recyclerView()
        observeList()
        celsiumObserve()
        observeMessage()

        findViewById<ToggleButton>(R.id.toggleTempUnit).setOnCheckedChangeListener { _, isChecked ->
            viewModel.toggleTemperatureUnit()
        }


        findViewById<Button>(R.id.btnGetWeather).setOnClickListener {
            val city = findViewById<EditText>(R.id.etCity).text.toString()
            if (city.isNotEmpty()) {
                viewModel.fetchWeather(city)
            }
        }

    }
    private fun recyclerView() {
        findViewById<RecyclerView>(R.id.rView).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
    }

    private fun observeList() {
        viewModel.forecastData.observe(this) { data ->
            data?.let { adapter.submitList(it) }
        }
    }

    private fun celsiumObserve() {
        viewModel.isCelsius.observe(this) { isCelsius ->
            findViewById<ToggleButton>(R.id.toggleTempUnit).isChecked = !isCelsius
            adapter.notifyDataSetChanged()
        }
    }

    private fun observeMessage() {
        viewModel.toastMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).apply {
                    setGravity(Gravity.CENTER, 0, 0)
                }.show()
                viewModel.onToastShown()
            }
        }
    }
}
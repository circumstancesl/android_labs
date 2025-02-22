package com.example.android_labs

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ForecastAdapter(private val diffCallback: ForecastDiffCallback, private val viewModel: ViewModel) :
    ListAdapter<ForecastItem, RecyclerView.ViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.forecast_item, parent, false)
        return when (viewType) {
            0 -> ViewHolderHot(view)
            else -> ViewHolderCold(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val forecastItem = getItem(position)
        when (holder) {
            is ViewHolderHot -> holder.bind(forecastItem, viewModel)
            is ViewHolderCold -> holder.bind(forecastItem, viewModel)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).main.temp > 0) 0 else 1
    }

    abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(forecastItem: ForecastItem, viewModel: ViewModel) {
            itemView.findViewById<TextView>(R.id.date).text = forecastItem.dt_txt
            val temperature = viewModel.convertTemperature(forecastItem.main.temp)
            val unit = if (viewModel.isCelsius.value == true) "°C" else "°F"
            itemView.findViewById<TextView>(R.id.temperature).text = "%.1f%s".format(temperature, unit)
            itemView.findViewById<TextView>(R.id.pressure).text = "${forecastItem.main.pressure} hPa"

            Glide.with(itemView.context)
                .load("https://openweathermap.org/img/wn/${forecastItem.weather[0].icon}@2x.png")
                .into(itemView.findViewById(R.id.temperature_icon))
        }
    }

    class ViewHolderHot(itemView: View) : BaseViewHolder(itemView) {
        init {
            itemView.setBackgroundColor(Color.parseColor("#FFC080"))
            itemView.setPadding(0, 0, 0, 0)
        }
    }

    class ViewHolderCold(itemView: View) : BaseViewHolder(itemView) {
        init {
            itemView.setBackgroundColor(Color.parseColor("#80B1FF"))
            itemView.setPadding(16, 16, 16, 16)
        }
    }
}
package com.example.android_labs.ui

import androidx.recyclerview.widget.DiffUtil
import com.example.android_labs.data.models.ForecastItem

class ForecastDiffCallback : DiffUtil.ItemCallback<ForecastItem>() {
    override fun areItemsTheSame(oldItem: ForecastItem, newItem: ForecastItem): Boolean {
        return oldItem.dt == newItem.dt
    }

    override fun areContentsTheSame(oldItem: ForecastItem, newItem: ForecastItem): Boolean {
        return oldItem == newItem &&
                oldItem.main.temp == newItem.main.temp &&
                oldItem.main.pressure == newItem.main.pressure
    }
}
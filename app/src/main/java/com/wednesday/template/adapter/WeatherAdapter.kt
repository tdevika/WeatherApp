package com.wednesday.template.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wednesday.template.databinding.PartialWeatherRowBinding
import com.wednesday.template.model.Weather

class WeatherAdapter : ListAdapter<Weather, WeatherAdapter.WeatherViewHolder>(
    weatherDiffUtils
) {

    lateinit var binding: PartialWeatherRowBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        binding =
            PartialWeatherRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WeatherViewHolder(binding)
    }

    override fun onBindViewHolder(holderWeather: WeatherViewHolder, position: Int) {
        holderWeather.bind(getItem(position))
    }

    inner class WeatherViewHolder(val binding: PartialWeatherRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Weather) {
            binding.weatherData = item
            if(!item.consolidatedWeathers.isNullOrEmpty()) binding.weatherImage.setImageResource(item.consolidatedWeathers[0].weatherConditionImageResourceId)
        }
    }
}

val weatherDiffUtils: DiffUtil.ItemCallback<Weather> = object : DiffUtil.ItemCallback<Weather>() {
    override fun areItemsTheSame(oldItem: Weather, newItem: Weather): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(oldItem: Weather, newItem: Weather): Boolean {
        return oldItem == newItem
    }
}
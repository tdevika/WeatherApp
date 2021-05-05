package com.wednesday.template.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wednesday.template.databinding.PartialCityRowBinding
import com.wednesday.template.model.City

interface CitySelected {
    fun onCitySelected(city: City)
}

class CitiesAdapter(val citySelectedDelegate: CitySelected) :
    ListAdapter<City, CitiesAdapter.CityViewHolder>(diffUtils) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val binding =
            PartialCityRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        holder.bind(getItem(position),position)
    }

    inner class CityViewHolder(private val binding: PartialCityRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(city: City, position: Int) {
            binding.cityData = city
            itemView.setOnClickListener {
                city.isFavorite = !city.isFavorite
                notifyItemChanged(position)
                citySelectedDelegate.onCitySelected(city)
            }
        }
    }
}

val diffUtils = object : DiffUtil.ItemCallback<City>() {
    override fun areItemsTheSame(oldItem: City, newItem: City): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(oldItem: City, newItem: City): Boolean {
        return oldItem == newItem
    }
}

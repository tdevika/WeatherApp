package com.wednesday.template.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wednesday.template.database.DatabaseDao
import com.wednesday.template.network.WeatherApiService
import kotlinx.coroutines.Dispatchers

class WeatherViewModelFactory(
    private val application: Application,
    private val databaseDao: DatabaseDao,
    private val weatherApiService: WeatherApiService
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        WeatherViewModel(application, databaseDao,weatherApiService, Dispatchers.IO) as T
}


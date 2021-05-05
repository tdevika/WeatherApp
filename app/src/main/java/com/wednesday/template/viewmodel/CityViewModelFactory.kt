package com.wednesday.template.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wednesday.template.database.DatabaseDao
import com.wednesday.template.network.WeatherApiService

class CityViewModelFactory(
    private val application: Application,
    private val databaseDao: DatabaseDao,
    private val weatherApiService: WeatherApiService
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        CityViewModel(application, databaseDao,weatherApiService) as T
}

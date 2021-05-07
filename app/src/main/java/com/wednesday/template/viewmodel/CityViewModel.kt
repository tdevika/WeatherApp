package com.wednesday.template.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wednesday.template.database.DatabaseDao
import com.wednesday.template.model.City
import com.wednesday.template.network.WeatherApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CityViewModel(
    application: Application,
    private val databaseDao: DatabaseDao,
    private val apiService: WeatherApiService
) : AndroidViewModel(application) {

    val searchCities = MutableLiveData<List<City>>()
    var favoriteCities = MutableLiveData<List<City>>()

    init {
        getFavoriteCities()
    }

    fun fetchCities(searchText: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = apiService.searchCities(searchText)
                result.map { city ->
                    val isCitySelected = favoriteCities.value?.find { it.title == city.title }
                    isCitySelected?.let {
                        city.isFavorite = true
                    }
                }
                searchCities.postValue(result)
            }
        }
    }

     fun getFavoriteCities() =
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                databaseDao.getObservableFavoriteCities().collectLatest { cityList ->
                    favoriteCities.postValue(cityList)
                }
            }
        }

    fun markCityAsFavorite(city: City) =
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                city.let {
                    databaseDao.markCityAsFavorite(city)
                }
            }
        }
}


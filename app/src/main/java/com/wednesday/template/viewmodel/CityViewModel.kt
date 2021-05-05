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
import org.kodein.di.DIAware
import org.kodein.di.android.x.di
import org.kodein.di.instance

class CityViewModel(application: Application) : AndroidViewModel(application), DIAware {

    override val di by di()
    private val databaseDao: DatabaseDao by instance("databaseDao")
    private val apiService: WeatherApiService by instance("apiService")

    val searchCities = MutableLiveData<List<City>>()
    private var favoriteCities = listOf<City>()

    init {
        getFavoriteCities()
    }

    fun fetchCities(searchText: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = apiService.searchCities(searchText)
                result.map {city->
                    val c = favoriteCities.find {it.title == city.title}
                       c?.let {
                           city.isFavorite = true
                       }
                    }
                searchCities.postValue(result)
            }
        }
    }

    private fun getFavoriteCities() =
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                databaseDao.getObservableFavoriteCities().collectLatest { cityList ->
                    favoriteCities = cityList
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


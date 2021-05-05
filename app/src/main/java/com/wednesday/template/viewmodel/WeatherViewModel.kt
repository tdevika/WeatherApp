package com.wednesday.template.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wednesday.template.database.DatabaseDao
import com.wednesday.template.model.Resource
import com.wednesday.template.model.Weather
import com.wednesday.template.network.WeatherApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.kodein.di.DIAware
import org.kodein.di.android.x.di
import org.kodein.di.instance

class WeatherViewModel(application: Application) : AndroidViewModel(application), DIAware {

    override val di by di()
    private val databaseDao: DatabaseDao by instance("databaseDao")
    private val apiService: WeatherApiService by instance("apiService")
    val weatherLiveData = MutableLiveData(Resource.success(listOf<Weather>()))


    val weatherList = MutableLiveData<UiState>()
    val weatherData = mutableMapOf<Int, Weather>()

init {
    triggerLoadForAllFavoriteCities()
}

    private fun triggerLoadForAllFavoriteCities() {
        weatherList.postValue(UiState.Loading)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                databaseDao.getObservableFavoriteCities().collectLatest {cities->
                    cities.forEach { city ->
                        try {
                            if(!weatherData.keys.contains(city.woeid)) {
                                val weather = apiService.weatherForCity(city.woeid)
                                weatherData[city.woeid] = weather
                            }
                        } catch (e: Exception) {
                            weatherList.postValue(e.message?.let { it -> UiState.Error(it) })
                        }
                    }
                    weatherList.postValue(UiState.Succuss(weatherData.values))
                }
            }
        }
    }
    fun triggerLoadForAllFavoriteCities1() {
        viewModelScope.launch {
            weatherLiveData.value = Resource.loading(listOf())

            val cities = databaseDao.getFavoriteCities()
            val liveDataWeatherValues = weatherLiveData.value!!.data!!.toMutableList()

            cities.forEach { city ->
                val weather = apiService.weatherForCity(city.woeid)
                val liveDataWeather =
                    liveDataWeatherValues.find { value -> value.title == weather.title }
                if (liveDataWeather !== null) {
                    liveDataWeather.consolidatedWeathers = weather.consolidatedWeathers
                } else {
                    liveDataWeatherValues.add(weather)
                }
                weatherLiveData.value = Resource.loading(liveDataWeatherValues)
            }
            weatherLiveData.value = Resource.success(liveDataWeatherValues)
        }
    }
}


sealed class UiState() {
    data class Succuss(val data:Any) : UiState()
    data class Error(val message: String) : UiState()
    object Loading : UiState()
}

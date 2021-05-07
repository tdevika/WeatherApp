package com.wednesday.template.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wednesday.template.database.DatabaseDao
import com.wednesday.template.model.Weather
import com.wednesday.template.network.WeatherApiService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class WeatherViewModel(
    application: Application,
    private val databaseDao: DatabaseDao,
    private val apiService: WeatherApiService,
    private val dispatcher: CoroutineDispatcher
) : AndroidViewModel(application) {

    val weatherList = MutableLiveData<UiState>()
    val weatherData = mutableMapOf<Int, Weather>()

    init {
        triggerLoadForAllFavoriteCities()
    }

    fun triggerLoadForAllFavoriteCities() {
        weatherList.postValue(UiState.Loading)
        viewModelScope.launch(dispatcher) {
            databaseDao.getObservableFavoriteCities().collectLatest { cities ->
                cities.forEach { city ->
                    try {
                        if (!weatherData.keys.contains(city.woeid)) {
                            val weather = apiService.weatherForCity(city.woeid)
                            weatherData[city.woeid] = weather
                        }
                    } catch (e: Exception) {
                        weatherList.postValue(e.message?.let { it -> UiState.Error(it) })
                    }
                }
                weatherList.postValue(UiState.Success(weatherData.values))
            }
        }
    }
}


sealed class UiState {
    data class Success(val data: Any) : UiState()
    data class Error(val message: String) : UiState()
    object Loading : UiState()
}

package com.wednesday.template.viewmodel

import com.wednesday.template.MainCoroutineRule
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.wednesday.template.database.DatabaseDao
import com.wednesday.template.model.City
import com.wednesday.template.network.WeatherApiService
import com.wednesday.template.utils.TestUtil
import com.wednesday.template.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations.initMocks

@RunWith(AndroidJUnit4::class)
class CityViewModelTest {
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: CityViewModel
    private lateinit var databaseDao: DatabaseDao
    private lateinit var weatherApiService: WeatherApiService


    @Before
    fun init() {
        initMocks(this)
        databaseDao = mock(DatabaseDao::class.java)
        weatherApiService = mock(WeatherApiService::class.java)
        viewModel = CityViewModel(
            ApplicationProvider.getApplicationContext(),
            databaseDao,
            weatherApiService
        )

    }

    @ExperimentalCoroutinesApi
    @Test
    fun testFetchCities() = mainCoroutineRule.runBlockingTest {
        viewModel.fetchCities("Pune")
        verify(weatherApiService).searchCities("Pune")
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getFavoriteCities() = mainCoroutineRule.runBlockingTest {
        stubCities()
        viewModel.getFavoriteCities()
        val cities = viewModel.favoriteCities.getOrAwaitValue()
        assertThat(cities).isEqualTo(TestUtil.cities)
        assertThat(cities.size).isEqualTo(2)
    }

    private fun stubCities() {
        val flow = flow<List<City>> { emit(TestUtil.cities) }
        `when`(databaseDao.getObservableFavoriteCities()).thenReturn(flow)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getFavoriteCities_empty_returnEmpty() = mainCoroutineRule.runBlockingTest {
        stubEmptyCities()
        viewModel.getFavoriteCities()
        viewModel.favoriteCities.getOrAwaitValue()
        assertThat(viewModel.favoriteCities.value).isEmpty()
    }

    private fun stubEmptyCities() {
        val flow = flow<List<City>> { emit(emptyList()) }
        `when`(databaseDao.getObservableFavoriteCities()).thenReturn(flow)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun markCityAsFavorite() = mainCoroutineRule.runBlockingTest {
        val city = City(2295414, "Hyderabad", "City")
        viewModel.markCityAsFavorite(city)
        verify(databaseDao).markCityAsFavorite(city)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun fetchCities() = mainCoroutineRule.runBlockingTest {
        val searchedCities = listOf(
            City(2295414, "Hyderabad", "City"),
            City(33267, "Rhyl", "City")
        )
        `when`(weatherApiService.searchCities("hy")).thenReturn(searchedCities)
        viewModel.fetchCities("hy")
        viewModel.searchCities.getOrAwaitValue()
        assertThat(viewModel.searchCities.value).isEqualTo(searchedCities)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun fetchCities_incorrectInput_returnEmpty() = mainCoroutineRule.runBlockingTest {
        `when`(weatherApiService.searchCities("hygytf")).thenReturn(emptyList())
        viewModel.fetchCities("hygytf")
        viewModel.searchCities.getOrAwaitValue()
        assertThat(viewModel.searchCities.value).isEmpty()
    }

}




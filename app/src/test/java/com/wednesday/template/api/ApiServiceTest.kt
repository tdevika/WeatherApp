package com.example.bankdetails.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.wednesday.template.network.WeatherApiService
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import com.google.common.truth.Truth.assertThat
import okio.buffer
import okio.source
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@RunWith(JUnit4::class)
class ApiServiceTest {
    private lateinit var service: WeatherApiService

    private lateinit var mockWebServer: MockWebServer

    @Before
    fun createService() {
        mockWebServer = MockWebServer()
        service = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
            .create(WeatherApiService::class.java)
    }

    @After
    fun stopService() {
        mockWebServer.shutdown()
    }

    @Test
    fun getCities() {
        enqueueResponse("city-details.json")
        val cityDetails = runBlocking { service.searchCities("New York") }
        assertThat(cityDetails.first().title).isEqualTo("New York")
    }

    @Test
    fun getWeatherData() {
        enqueueResponse("weather-details.json")
        val weatherDetails = runBlocking { service.weatherForCity(2459115) }
        assertThat(weatherDetails.title).isEqualTo("New York")
        assertThat(weatherDetails.consolidatedWeathers[0].minTemp).isEqualTo(11.184999999999999)
        assertThat(weatherDetails.consolidatedWeathers[0].maxTemp).isEqualTo(16.369999999999997)
    }

    private fun enqueueResponse(fileName: String) {
        val inputStream = javaClass.classLoader!!
            .getResourceAsStream("api-response/$fileName")
        val source = inputStream.source().buffer()
        val mockResponse = MockResponse()
        mockWebServer.enqueue(
            mockResponse
                .setBody(source.readString(Charsets.UTF_8))
        )
    }
}
package com.wednesday.template.viewmodel


import com.wednesday.template.MainCoroutineRule
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.wednesday.template.database.DatabaseDao
import com.wednesday.template.network.WeatherApiService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations.initMocks
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
class WeatherViewModelTest {

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()


    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: WeatherViewModel
    private lateinit var databaseDao: DatabaseDao
    private lateinit var apiService: WeatherApiService

    @ExperimentalCoroutinesApi
    @Before
    fun setup() {
        initMocks(this)
        databaseDao = mock(DatabaseDao::class.java)
        apiService = mock(WeatherApiService::class.java)
        viewModel =
            WeatherViewModel(
                ApplicationProvider.getApplicationContext(),
                databaseDao,
                apiService,
                mainCoroutineRule.dispatcher)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun triggerLoadForAllFavoriteCities() = mainCoroutineRule.runBlockingTest {
        viewModel.triggerLoadForAllFavoriteCities()
        mainCoroutineRule.advanceTimeBy(10)
        verify(databaseDao, times(2)).getObservableFavoriteCities()
    }
}
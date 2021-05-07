package com.wednesday.template

import android.app.Application
import androidx.room.Room.databaseBuilder
import com.google.gson.GsonBuilder
import com.wednesday.template.database.AndroidTemplateDatabase
import com.wednesday.template.database.DatabaseDao
import com.wednesday.template.network.WeatherApiService
import com.wednesday.template.viewmodel.CityViewModelFactory
import com.wednesday.template.viewmodel.WeatherViewModelFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.kodein.di.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://www.metaweather.com/"

class AndroidTemplateApplication: Application(), DIAware {

  override val di by DI.lazy {

    bind<Interceptor>("loggingInterceptor") with singleton {
      val interceptor = HttpLoggingInterceptor()
      interceptor.level = HttpLoggingInterceptor.Level.BODY
      interceptor
    }

    bind<OkHttpClient>("httpClient") with singleton {
      val builder = OkHttpClient.Builder()
      builder.addInterceptor(instance<Interceptor>("loggingInterceptor"))
      builder.build()
    }

    bind<Retrofit>("retrofit") with singleton {
      val gson = GsonBuilder().setLenient().create()
      Retrofit.Builder().baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(instance("httpClient"))
        .build()
    }

    bind<WeatherApiService>("apiService") with singleton {
      instance<Retrofit>("retrofit").create(WeatherApiService::class.java)
    }

    bind<AndroidTemplateDatabase>("database") with singleton {
      databaseBuilder(
        applicationContext,
        AndroidTemplateDatabase::class.java,
        "android_template_database").build()
    }

    bind<DatabaseDao>("databaseDao") with singleton {
      instance<AndroidTemplateDatabase>("database").databaseDao()
    }
    bind<CityViewModelFactory>("cityViewModelFactory") with singleton {
      CityViewModelFactory(this@AndroidTemplateApplication, instance("databaseDao"), instance("apiService") )
    }

    bind<WeatherViewModelFactory>("weatherViewModelFactory") with singleton {
      WeatherViewModelFactory(this@AndroidTemplateApplication, instance("databaseDao"), instance("apiService") )
    }
  }
}

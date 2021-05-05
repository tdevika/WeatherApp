package com.wednesday.template.database

import androidx.room.*
import com.wednesday.template.model.City
import com.wednesday.template.model.Weather
import kotlinx.coroutines.flow.Flow

@Dao
interface DatabaseDao {
  @Query("select * from favorite_cities")
  fun getObservableFavoriteCities():Flow<List<City>>

  @Query("select * from favorite_cities")
  suspend fun getFavoriteCities(): List<City>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun markCityAsFavorite(city: City)

  @Delete
  suspend fun deleteFavoriteCity(city: City)


}

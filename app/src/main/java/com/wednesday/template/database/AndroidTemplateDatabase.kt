package com.wednesday.template.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.wednesday.template.model.City
import com.wednesday.template.model.Weather

@Database(entities = [City::class], version = 1)
abstract class AndroidTemplateDatabase: RoomDatabase() {
  abstract fun databaseDao(): DatabaseDao
}

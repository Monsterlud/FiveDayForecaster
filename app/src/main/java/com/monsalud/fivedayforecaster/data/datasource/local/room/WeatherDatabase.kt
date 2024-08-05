package com.monsalud.fivedayforecaster.data.datasource.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.monsalud.fivedayforecaster.data.datasource.local.WeatherEntity

@Database(entities = [WeatherEntity::class], version = 1, exportSchema = false)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun weatherDao(): WeatherDAO

    companion object {
        @Volatile
        private var INSTANCE: WeatherDatabase? = null

        fun getDatabase(context: Context): WeatherDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context = context.applicationContext,
                    klass = WeatherDatabase::class.java,
                    name = "weather_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
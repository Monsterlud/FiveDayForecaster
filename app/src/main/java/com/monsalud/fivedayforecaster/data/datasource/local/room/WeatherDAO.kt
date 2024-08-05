package com.monsalud.fivedayforecaster.data.datasource.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.monsalud.fivedayforecaster.data.datasource.local.WeatherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addWeatherResponseToRoom(entity: WeatherEntity)

    @Query("SELECT * FROM weather_table ORDER BY id ASC")
    fun getWeatherResponseFromRoom() : Flow<List<WeatherEntity>>

    @Query("DELETE FROM weather_table")
    suspend fun clearDatabase()
}
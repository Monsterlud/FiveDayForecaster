package com.monsalud.fivedayforecaster.data

import com.monsalud.fivedayforecaster.data.datasource.local.WeatherEntity
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {

    suspend fun saveWeatherForecast(value: String?)

    fun getWeatherForecast(): Flow<List<WeatherEntity>>
}
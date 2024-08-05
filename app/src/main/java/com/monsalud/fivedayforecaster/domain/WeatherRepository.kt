package com.monsalud.fivedayforecaster.domain

import com.monsalud.fivedayforecaster.data.datasource.local.WeatherEntity
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {

    suspend fun cacheWeatherResponseJson(value: String?)

    fun retrieveWeatherResponseJson(): Flow<List<WeatherEntity>>

    suspend fun getAndSaveFiveDayWeatherForecast(zipCode: String) : String

    suspend fun getGeocodingResponseJson(zipCode: String) : Result<String?>

    suspend fun getWeatherResponseJson(lat: Double, lon: Double): Result<String?>

    fun retrieveLocation() : Flow<String>
}
package com.monsalud.fivedayforecaster.data.datasource.local

import com.google.gson.Gson
import com.monsalud.fivedayforecaster.data.LocalDataSource
import com.monsalud.fivedayforecaster.data.datasource.local.room.WeatherDAO
import com.monsalud.fivedayforecaster.data.utils.WeatherMappers
import com.monsalud.fivedayforecaster.data.datasource.remote.FiveDayWeatherResponseFromApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalDataSourceImpl(
    private val weatherDAO: WeatherDAO,
    private val gson: Gson,
    private val mapper: WeatherMappers
) : LocalDataSource {

    override suspend fun saveWeatherForecast(value: String?) {
        weatherDAO.clearDatabase()
        if (!value.isNullOrEmpty()) {
            val weatherResponseList =
                gson.fromJson(value, FiveDayWeatherResponseFromApi::class.java).list

            for (apiResponse in weatherResponseList) {
                val item = mapper.mapFromDtoToEntity(apiResponse)
                weatherDAO.addWeatherResponseToRoom(item)
            }
        } else throw IllegalArgumentException("Invalid Json String")
    }

    override fun getWeatherForecast(): Flow<List<WeatherEntity>> {
        return weatherDAO.getWeatherResponseFromRoom()
    }

    override suspend fun saveLocation(location: String) {
        weatherDAO.saveLocation(LocationEntity(location = location))
    }

    override fun getLocation(): Flow<String> =
        weatherDAO.getLocation().map { it?.location ?: "" }
}